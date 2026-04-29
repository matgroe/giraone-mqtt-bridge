/*
 * GiraOne Bridge
 * Copyright (C) 2025 Matthias Gröger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.matgroe.mqtt;

import com.hivemq.client.mqtt.MqttClientState;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishBuilder;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishResult;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Duration.TWO_SECONDS;
import org.jetbrains.annotations.NotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.message.auth.Mqtt5SimpleAuthBuilder;
import com.hivemq.client.mqtt.mqtt5.message.connect.Mqtt5ConnectBuilder;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAckReasonCode;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.OngoingStubbing;

/**
 * Test class for {@link MqttClient}
 *
 * @author Matthias Groeger - Initial contribution
 */
class MqttClientTest {
  MqttClientProperties mqttClientProperties = new MqttClientProperties();
  ;

  MqttClient mqttClient;
  Mqtt5AsyncClient mqtt5ClientMock = mock(Mqtt5AsyncClient.class);

  Mqtt5ConnectBuilder.Send<CompletableFuture<Mqtt5ConnAck>> mqtt5ConnectBuilderSendMock =
      mock(Mqtt5ConnectBuilder.Send.class, RETURNS_SELF);

  Mqtt5SimpleAuthBuilder.Nested mqtt5SimpleAuthBuilderNestedMock =
      mock(Mqtt5SimpleAuthBuilder.Nested.class);

  Mqtt5SimpleAuthBuilder.Nested.Complete.Complete mqtt5SimpleAuthBuilderMock =
      mock(Mqtt5SimpleAuthBuilder.Nested.Complete.Complete.class, RETURNS_SELF);

  Mqtt5PublishBuilder.Send<CompletableFuture<Mqtt5PublishResult>> mqtt5PublishBuilder =
          mock(Mqtt5PublishBuilder.Send.class, RETURNS_SELF);

  @BeforeEach
  void setUp() {
    mqttClientProperties.mqttBroker = "localhost";
    mqttClientProperties.mqttPort = 1883;
    mqttClientProperties.username = "user";
    mqttClientProperties.password = "secret";

    mqttClient = spy(new MqttClient(mqttClientProperties));
    when(mqttClient.buildMqtt5Client()).thenReturn(mqtt5ClientMock);

    // configure mocks for connect
    when(mqtt5ConnectBuilderSendMock.simpleAuth()).thenReturn(mqtt5SimpleAuthBuilderMock);
    when(mqtt5SimpleAuthBuilderNestedMock.username(anyString()))
        .thenReturn(mqtt5SimpleAuthBuilderMock);
    when(mqtt5SimpleAuthBuilderNestedMock.password(any(byte[].class)))
        .thenReturn(mqtt5SimpleAuthBuilderMock);
    when(mqtt5SimpleAuthBuilderMock.applySimpleAuth()).thenReturn(mqtt5ConnectBuilderSendMock);
    when(mqtt5ClientMock.connectWith()).thenReturn(mqtt5ConnectBuilderSendMock);
    when(mqtt5ClientMock.publishWith()).thenReturn(mqtt5PublishBuilder);

  }

  @Test
  @DisplayName("should connect and auth with given credentials")
  void testMqttConnectWithGivenCredentials() {
    CompletableFuture<Mqtt5ConnAck> connAckFuture = new CompletableFuture<>();
    when(mqtt5ConnectBuilderSendMock.send()).thenReturn(connAckFuture);

    Mqtt5ConnAck connAck = mock(Mqtt5ConnAck.class);
    mqttClient.connect("junit");

    connAckFuture.complete(mock(Mqtt5ConnAck.class));
    verify(mqtt5SimpleAuthBuilderMock).username(mqttClientProperties.username);
    verify(mqtt5SimpleAuthBuilderMock).password(mqttClientProperties.password.getBytes());
  }

  @Test
  @DisplayName("should have MqttClientConnectionState.Connected by Mqtt5ConnAckReasonCode.SUCCESS")
  void testMqttConnectionState_Connected() {
    CompletableFuture<Mqtt5ConnAck> connAckFuture = new CompletableFuture<>();
    when(mqtt5ConnectBuilderSendMock.send()).thenReturn(connAckFuture);

    Mqtt5ConnAck connAck = mock(Mqtt5ConnAck.class);
    when(connAck.getReasonCode()).thenReturn(Mqtt5ConnAckReasonCode.SUCCESS);
    mqttClient.connect("junit");
    connAckFuture.complete(connAck);
    await()
        .atMost(TWO_SECONDS)
        .until(() -> mqttClient.connectionState.getValue() == MqttClientConnectionState.Connected);
  }

  @Test
  @DisplayName(
      "should have MqttClientConnectionState.Error should by Mqtt5ConnAckReasonCode.NOT_AUTHORIZED")
  void testMqttConnectionState_Error() {
    CompletableFuture<Mqtt5ConnAck> connAckFuture = new CompletableFuture<>();
    when(mqtt5ConnectBuilderSendMock.send()).thenReturn(connAckFuture);

    Mqtt5ConnAck connAck = mock(Mqtt5ConnAck.class);
    when(connAck.getReasonCode()).thenReturn(Mqtt5ConnAckReasonCode.NOT_AUTHORIZED);
    mqttClient.connect("junit");
    connAckFuture.complete(connAck);
    await()
        .atMost(TWO_SECONDS)
        .until(() -> mqttClient.connectionState.getValue() == MqttClientConnectionState.Error);
  }

  @Test
  @DisplayName("should return false on empty message")
  void testMqttPublishEmptyMessage() {
    mqttClient.mqtt5Client = mock(Mqtt5AsyncClient.class);
    MqttClientState stateMock = mock(MqttClientState.class);

    when(stateMock.isConnected()).thenReturn(false);
    when(mqttClient.mqtt5Client.getState()).thenReturn(stateMock);
    assertFalse(mqttClient.publish(null));
  }

  @Test
  @DisplayName("should return false on not connected")
  void testMqttPublishOnNotConnected() {
    mqttClient.mqtt5Client = mock(Mqtt5AsyncClient.class);
    MqttClientState stateMock = mock(MqttClientState.class);

    when(stateMock.isConnected()).thenReturn(false);
    when(mqttClient.mqtt5Client.getState()).thenReturn(stateMock);
    assertFalse(mqttClient.publish(new MqttMessage("topic", "payload")));
  }

  @Test
  @DisplayName("should not publish on error state")
  void testMqttPublishOnError() {
    mqttClient.mqtt5Client = mock(Mqtt5AsyncClient.class);
    MqttClientState stateMock = mock(MqttClientState.class);
    mqttClient.connectionState.onNext(MqttClientConnectionState.Error);
    when(stateMock.isConnected()).thenReturn(true);

    when(mqttClient.mqtt5Client.getState()).thenReturn(stateMock);
    assertFalse(mqttClient.publish(new MqttMessage("topic", "payload")));
  }

  @Test
  @DisplayName("should return true on connected")
  void testMqttPublishOnConnected() {
    mqttClient.mqtt5Client = mqtt5ClientMock;

    MqttClientState stateMock = mock(MqttClientState.class);
    mqttClient.connectionState.onNext(MqttClientConnectionState.Connected);
    when(stateMock.isConnected()).thenReturn(true);
    when(mqttClient.mqtt5Client.getState()).thenReturn(stateMock);
    when(mqtt5ClientMock.publishWith()).thenReturn(mqtt5PublishBuilder);

    CompletableFuture<Mqtt5PublishResult> publishResultMock = mock(CompletableFuture.class);

    Mqtt5PublishBuilder.Send.Complete<CompletableFuture<Mqtt5PublishResult>> publishBuilderComplete = mock(Mqtt5PublishBuilder.Send.Complete.class, RETURNS_SELF);
    when(publishBuilderComplete.send()).thenReturn(publishResultMock);
    when(mqtt5PublishBuilder.topic(anyString())).thenReturn(publishBuilderComplete);
    assertTrue(mqttClient.publish(new MqttMessage("topic", "payload")));
  }
}
