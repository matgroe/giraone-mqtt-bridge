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

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAckReasonCode;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishResult;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.ReplaySubject;
import io.reactivex.rxjava3.subjects.Subject;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttClient {
  private final Logger logger = LoggerFactory.getLogger(MqttClient.class);

  /** this subject receives the incoming messages from {@link MqttClient} */
  private final Subject<MqttMessage> inboundQueue = PublishSubject.create();

  private final MqttClientProperties mqttClientProperties;

  private String topicNamePrefix = "";

  private String clientIdentifier = UUID.randomUUID().toString();

  /** Observe this subject for MQTT Broker connection state */
  final ReplaySubject<MqttClientConnectionState> connectionState = ReplaySubject.createWithSize(1);

  Mqtt5AsyncClient mqtt5Client;

  public MqttClient(MqttClientProperties mqttClientProperties) {
    this.mqttClientProperties = mqttClientProperties;
    this.connectionState.onNext(MqttClientConnectionState.Disconnected);
    this.connectionState.subscribe(this::onConnectionStateChanged);
  }

  void onConnectionStateChanged(MqttClientConnectionState mqttClientConnectionState) {
    logger.debug("MqttClientConnectionState changed to {}", mqttClientConnectionState);
    String topicFilter = String.format("%s/command/#", topicNamePrefix);
    if (mqttClientConnectionState == MqttClientConnectionState.Connected && mqtt5Client != null) {
      mqtt5Client.subscribeWith().topicFilter(topicFilter).callback(this::onMessageReceived).send();
    } else {
      if (mqtt5Client != null) {
        mqtt5Client.unsubscribeWith().topicFilter(topicFilter).send();
      }
    }
  }

  /** Disconnect from Broker */
  public void disconnect() {
    if (mqtt5Client != null && mqtt5Client.getState().isConnected()) {
      mqtt5Client
          .disconnect()
          .whenComplete(
              (Void unused, Throwable throwable) -> {
                if (throwable != null) {
                  logger.error(
                      "Error on disconnecting from '{}'",
                      mqttClientProperties.getMqttBroker(),
                      throwable);
                }
                this.connectionState.onNext(MqttClientConnectionState.Disconnected);
              });
    }
  }

  /**
   * Register's a listener for changes on {@link MqttClientConnectionState}.
   *
   * @param consumer The Consumer for {@link MqttClientConnectionState} changes.
   * @return a {@link Disposable}
   */
  public Disposable observeMqttConnectionState(Consumer<MqttClientConnectionState> consumer) {
    return connectionState.subscribe(consumer);
  }

  /**
   * Register's a listener incoming {@link MqttMessage}
   *
   * @param consumer The Consumer for {@link MqttMessage} changes.
   * @param errorHandler The exception handler
   * @return a {@link Disposable}
   */
  public Disposable observeInboundQueue(
      Consumer<MqttMessage> consumer, Consumer<Throwable> errorHandler) {
    return inboundQueue.subscribe(consumer, errorHandler);
  }

  /**
   * Register's a listener incoming {@link MqttMessage}
   *
   * @param consumer The Consumer for {@link MqttMessage} changes.
   * @return a {@link Disposable}
   */
  public Disposable observeInboundQueue(Consumer<MqttMessage> consumer) {
    return inboundQueue.subscribe(consumer);
  }

  /**
   * Build an {@link Mqtt5AsyncClient} to be used in this class.
   *
   * @return Mqtt5AsyncClient
   */
  Mqtt5AsyncClient buildMqtt5Client() {
    return com.hivemq.client.mqtt.MqttClient.builder()
        .useMqttVersion5()
        .identifier(UUID.randomUUID().toString())
        .serverHost(mqttClientProperties.getMqttBroker())
        .serverPort(mqttClientProperties.getMqttPort())
        .buildAsync();
  }

  /** Connects to the MQTT Broker as given within the {@link MqttClientProperties} object. */
  public void connect(String topicNamePrefix) {
    this.topicNamePrefix = topicNamePrefix;
    disconnect();
    this.connectionState.onNext(MqttClientConnectionState.Connecting);
    mqtt5Client = buildMqtt5Client();
    mqtt5Client
        .connectWith()
        .noSessionExpiry()
        .keepAlive(mqttClientProperties.keepAlive)
        .simpleAuth()
        .username(mqttClientProperties.getUsername())
        .password(mqttClientProperties.getPassword().getBytes())
        .applySimpleAuth()
        .send()
        .whenComplete(
            (Mqtt5ConnAck connAck, Throwable throwable) -> {
              if (connAck != null) {
                logger.debug("MQTT Connect Completes with :: {}", connAck);
                this.connectionState.onNext(
                    connAck.getReasonCode() == Mqtt5ConnAckReasonCode.SUCCESS
                        ? MqttClientConnectionState.Connected
                        : MqttClientConnectionState.Error);
              } else {
                logger.error("Establish connection to MQTT-Broker failed.", throwable);
                this.connectionState.onNext(MqttClientConnectionState.Error);
              }
            });
  }

  private MqttMessage createMqttMessage(Mqtt5Publish mqtt5Publish) {
    String payload =
        StandardCharsets.UTF_8
            .decode(mqtt5Publish.getPayload().orElse(ByteBuffer.wrap(new byte[0])))
            .toString();
    return new MqttMessage(mqtt5Publish.getTopic(), payload);
  }

  void onMessageReceived(Mqtt5Publish mqtt5Publish) {
    try {
      MqttMessage mqttMessage = createMqttMessage(mqtt5Publish);
      logger.debug("received at topic: '{}'", mqttMessage);
      this.inboundQueue.onNext(mqttMessage);
    } catch (Throwable throwable) {
      logger.warn("Something went wrong on processing received payload.", throwable);
    }
  }

  public boolean publish(MqttMessage message) {
    if (message == null) {
      logger.warn("Cannot send empty message");
      return false;
    }

    if (mqtt5Client == null
        || !mqtt5Client.getState().isConnected()
        || this.connectionState.getValue() != MqttClientConnectionState.Connected) {
      logger.warn("MQTT is not fully connected, ignoring message {}", message);
      return false;
    }

    logger.debug("publish {}", message);
    mqtt5Client
        .publishWith()
        .topic(message.topic())
        .payload(message.payload().getBytes())
        .messageExpiryInterval(message.expiresAfterMs())
        .qos(MqttQos.AT_LEAST_ONCE)
        .retain(true)
        .send()
        .whenComplete(
            (Mqtt5PublishResult mqttPublishResult, Throwable throwable) -> {
              if (throwable != null) {
                logger.error("publish; ", throwable);
              } else {
                logger.trace("publish {}", mqttPublishResult);
              }
            });
    return true;
  }
}
