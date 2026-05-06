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
package de.matgroe.bridge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.matgroe.GiraOneMqttApplicationProperties;
import de.matgroe.giraone.GiraOneTestDataProvider;
import de.matgroe.giraone.client.GiraOneClient;
import de.matgroe.giraone.client.GiraOneClientConnectionState;
import de.matgroe.giraone.client.GiraOneClientException;
import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.mqtt.MqttClient;
import de.matgroe.mqtt.MqttClientConnectionState;
import de.matgroe.mqtt.MqttMessage;
import io.reactivex.rxjava3.disposables.Disposable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;

public class GiraOneMqttBridgeTest {
  GiraOneClient giraOneClient = mock(GiraOneClient.class);
  MqttClient mqttClient = mock(MqttClient.class);

  GiraOneMqttBridge theBridge;

  @BeforeEach
  void setup() throws Exception {
    GiraOneMqttApplicationProperties applicationProperties =
        mock(GiraOneMqttApplicationProperties.class);
    when(applicationProperties.getName()).thenReturn("appname");
    when(applicationProperties.getUrl()).thenReturn("http://localhost");

    when(mqttClient.observeMqttConnectionState(any())).thenReturn(Disposable.empty());
    when(mqttClient.observeInboundQueue(any())).thenReturn(Disposable.empty());

    when(giraOneClient.observeOnGiraOneClientExceptions(any())).thenReturn(Disposable.empty());
    when(giraOneClient.observeGiraOneConnectionState(any())).thenReturn(Disposable.empty());

    when(giraOneClient.getGiraOneProject())
        .thenReturn(GiraOneTestDataProvider.createGiraOneProject());
    when(giraOneClient.lookupGiraOneDeviceConfiguration())
        .thenReturn(GiraOneTestDataProvider.createGiraOneDeviceConfiguration());
    theBridge = spy(new GiraOneMqttBridge(applicationProperties, giraOneClient, mqttClient));
  }

  @Description("should initialize connecting giraone client")
  @Test
  void testInitialize() {
    theBridge.initialize();
    verify(theBridge).handleBridgeStateConnectingGiraOneClient();
    verify(giraOneClient).connect();
  }

  @Description("should move to error state on giraone client connect failure")
  @Test
  void testInitializedWithConnectError() {
    doThrow(new GiraOneClientException("xx")).when(giraOneClient).connect();
    theBridge.initialize();
    verify(theBridge).handleBridgeStateConnectingGiraOneClient();
    verify(giraOneClient).connect();
    assertEquals(GiraOneMqttBridgeState.Error, theBridge.bridgeState.getValue());
  }

  @Description("should stay executeable on non error state")
  @Test
  void testExecuteableFlag() {
    theBridge.bridgeState.onNext(GiraOneMqttBridgeState.Connected);
    assertTrue(theBridge.isExecuteable());
    theBridge.bridgeState.onNext(GiraOneMqttBridgeState.Error);
    assertFalse(theBridge.isExecuteable());
  }

  @Description("should trigger internal state change handler")
  @Test
  void testGiraOneBridgeConnectionStateChanges() {
    theBridge.onGiraOneMqttBridgeStateChanged(GiraOneMqttBridgeState.Stopped);
    verify(theBridge).handleBridgeStateStopped();

    theBridge.onGiraOneMqttBridgeStateChanged(GiraOneMqttBridgeState.ConnectingGiraOneClient);
    verify(theBridge).handleBridgeStateConnectingGiraOneClient();

    theBridge.onGiraOneMqttBridgeStateChanged(GiraOneMqttBridgeState.ConnectingMqttClient);
    verify(theBridge).handleBridgeStateConnectingMqttClient();

    theBridge.onGiraOneMqttBridgeStateChanged(GiraOneMqttBridgeState.Connected);
    verify(theBridge).handleBridgeStateConnected();

    theBridge.onGiraOneMqttBridgeStateChanged(GiraOneMqttBridgeState.Disconnected);
    verify(theBridge).handleBridgeStateDisconnected();

    theBridge.onGiraOneMqttBridgeStateChanged(GiraOneMqttBridgeState.Error);
    verify(theBridge).handleBridgeStateError();
  }

  @Description("should handle giraone client state changes")
  @Test
  void testGiraOneClientConnectionStateChanges() {
    theBridge.onGiraOneClientConnectionStateChanged(GiraOneClientConnectionState.Disconnected);
    assertEquals(GiraOneMqttBridgeState.Disconnected, theBridge.bridgeState.getValue());

    theBridge.onGiraOneClientConnectionStateChanged(GiraOneClientConnectionState.Connected);
    assertEquals(GiraOneMqttBridgeState.ConnectingMqttClient, theBridge.bridgeState.getValue());

    theBridge.onGiraOneClientConnectionStateChanged(GiraOneClientConnectionState.Error);
    assertEquals(GiraOneMqttBridgeState.Error, theBridge.bridgeState.getValue());
  }

  @Description("should handle mqtt client state changes")
  @Test
  void testMqttClientConnectionStateChanges() {
    theBridge.onMqttClientConnectionStateChanged(MqttClientConnectionState.Disconnected);
    assertEquals(GiraOneMqttBridgeState.Disconnected, theBridge.bridgeState.getValue());

    theBridge.onMqttClientConnectionStateChanged(MqttClientConnectionState.Connected);
    assertEquals(GiraOneMqttBridgeState.Connected, theBridge.bridgeState.getValue());

    theBridge.onMqttClientConnectionStateChanged(MqttClientConnectionState.Error);
    assertEquals(GiraOneMqttBridgeState.Error, theBridge.bridgeState.getValue());
  }

  @Description("should handle state change to Stopped")
  @Test
  void testGiraOneClientConnectionStateChangesToStopped() {
    theBridge.onGiraOneMqttBridgeStateChanged(GiraOneMqttBridgeState.Stopped);
    verify(theBridge).handleBridgeStateStopped();

    theBridge.onGiraOneMqttBridgeStateChanged(GiraOneMqttBridgeState.ConnectingGiraOneClient);
    verify(theBridge).handleBridgeStateConnectingGiraOneClient();

    theBridge.onGiraOneMqttBridgeStateChanged(GiraOneMqttBridgeState.ConnectingMqttClient);
    verify(theBridge).handleBridgeStateConnectingMqttClient();

    theBridge.onGiraOneMqttBridgeStateChanged(GiraOneMqttBridgeState.Connected);
    verify(theBridge).handleBridgeStateConnected();

    theBridge.onGiraOneMqttBridgeStateChanged(GiraOneMqttBridgeState.Disconnected);
    verify(theBridge).handleBridgeStateDisconnected();

    theBridge.onGiraOneMqttBridgeStateChanged(GiraOneMqttBridgeState.Error);
    verify(theBridge).handleBridgeStateError();
  }

  @Description("should forward received MqttMessage to GiraOneClient")
  @Test
  void testOnMqttMessage() {
    theBridge.initialize();
    theBridge.handleBridgeStateConnectingMqttClient();
    MqttMessage m =
        new MqttMessage(
            "GiraOneServer/OSRVKX03/state/schlafen/covering/61900bc1_schlafen_raffstore_kl_fenster/slat-position",
            "value");
    theBridge.onMqttMessage(m);
    verify(giraOneClient).changeGiraOneDataValue(any());
  }

  @Description("should forward received GiraOneValue to MqttClient")
  @Test
  void testOnGiraOneValue() {
    theBridge.initialize();
    theBridge.handleBridgeStateConnectingMqttClient();
    GiraOneValue value =
        new GiraOneValue(
            "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxHvacActuator6-gang-1.Heatingactuator-1:Set-Point",
            "22");
    theBridge.onGiraOneValue(value);
    verify(mqttClient).publish(any());
  }
}
