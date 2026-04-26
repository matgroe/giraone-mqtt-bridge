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
import com.hivemq.client.mqtt.datatypes.MqttUtf8String;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.datatypes.Mqtt5UserProperties;
import com.hivemq.client.mqtt.mqtt5.datatypes.Mqtt5UserProperty;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAckReasonCode;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishResult;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.ReplaySubject;

import io.reactivex.rxjava3.subjects.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public class MqttClient {
    private static final String CLIENT_ID = "client-id";
    private static final String MESSAGE_ID = "message-id";

    private final Logger logger = LoggerFactory.getLogger(MqttClient.class);

    /** Observe this subject for MQTT Broker connection state */
    private final ReplaySubject<MqttClientConnectionState> connectionState = ReplaySubject.createWithSize(1);

    /** this subject receives the incoming messages from {@link MqttClient} */
    private final Subject<MqttMessage> inboundQueue = PublishSubject.create();

    private final MqttClientProperties mqttClientProperties;

    private String topicNamePrefix = "";

    private String clientIdentifier = UUID.randomUUID().toString();

    Mqtt5AsyncClient mqtt5Client;

    public MqttClient(MqttClientProperties mqttClientProperties)  {
                this.mqttClientProperties = mqttClientProperties;
        this.connectionState.onNext(MqttClientConnectionState.Disconnected);
        this.connectionState.subscribe(this::onConnectionStateChanged);
    }

    private void onConnectionStateChanged(MqttClientConnectionState mqttClientConnectionState) {
        logger.debug("MqttClientConnectionState changed to {}", mqttClientConnectionState);
        String topicFilter = String.format("%s/#", topicNamePrefix);
        if (mqttClientConnectionState == MqttClientConnectionState.Connected) {
            mqtt5Client.subscribeWith()
                    .topicFilter(topicFilter)
                    .callback(this::onMessageReceived)
                    .send();
        } else {
            if (mqtt5Client != null) {
                mqtt5Client.unsubscribeWith().topicFilter(topicFilter).send();
            }
        }
    }

    /**
     * Disconnect from Broker
     */
    public void disconnect() {
        if (mqtt5Client != null && mqtt5Client.getState().isConnected()) {
            mqtt5Client.disconnect().whenComplete((Void unused, Throwable throwable) -> {
                if (throwable != null) {
                    logger.error("Error on disconnecting from '{}'", mqttClientProperties.getMqttBroker(), throwable);
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

    /**
     *  Disconnects from  MQTT Broker if connected.
     */
    public void disonnect() {
        if (this.mqtt5Client != null) {
            this.mqtt5Client.disconnect();
            this.mqtt5Client = null;
        }
    }

    /**
     *  Connects to the MQTT Broker as given within the {@link MqttClientProperties} object.
     */
    public void connect(String topicNamePrefix) {
        this.topicNamePrefix = topicNamePrefix;
        disconnect();
        this.connectionState.onNext(MqttClientConnectionState.Connecting);
        mqtt5Client = buildMqtt5Client();
        mqtt5Client.connectWith()
                .noSessionExpiry()
                .keepAlive(mqttClientProperties.keepAlive)
                .userProperties().add(CLIENT_ID, clientIdentifier).applyUserProperties()
                .simpleAuth()
                .username(mqttClientProperties.getUsername())
                .password(mqttClientProperties.getPassword().getBytes())
                .applySimpleAuth()
                .send()
                .whenComplete((Mqtt5ConnAck connAck, Throwable throwable) -> {
                    if (connAck != null) {
                        logger.debug("MQTT Connect Completes with :: {}", connAck);
                        if (connAck.getReasonCode() == Mqtt5ConnAckReasonCode.SUCCESS) {
                            this.connectionState.onNext(MqttClientConnectionState.Connected);
                        }
                    } else {
                        logger.error("Establish connection to MQTT-Broker failed.", throwable);
                        this.connectionState.onNext(MqttClientConnectionState.Error);
                    }
                });
    }

    private String getUserPropertyValue(Mqtt5UserProperties userproperties, String propertyName) {
        Optional<? extends Mqtt5UserProperty> mqttProp = userproperties.asList().stream().filter(p-> MqttUtf8String.of(propertyName).equals(p.getName())).findFirst();
        return mqttProp.map(mqtt5UserProperty -> mqtt5UserProperty.getValue().toString()).orElse(null);
    }

    private MqttMessage createMqttMessage(Mqtt5Publish mqtt5Publish) {
        String messageId = getUserPropertyValue(mqtt5Publish.getUserProperties(), MESSAGE_ID);
        String payload = StandardCharsets.UTF_8.decode(mqtt5Publish.getPayload().orElse(ByteBuffer.wrap(new byte[0]))).toString();
        return new MqttMessage(mqtt5Publish.getTopic(), payload, messageId);
        }

    void onMessageReceived(Mqtt5Publish mqtt5Publish) {
        try {
            String clientId = getUserPropertyValue(mqtt5Publish.getUserProperties(), CLIENT_ID);
            MqttMessage mqttMessage = createMqttMessage(mqtt5Publish);
            if (clientIdentifier.equals(clientId)) {
                logger.debug("dropping self published message '{}' at '{}'", mqttMessage.messageId(), mqttMessage.topic());
            } else {
                logger.debug("received at topic: '{}'", mqttMessage);
                this.inboundQueue.onNext(mqttMessage);
            }
        } catch (Throwable throwable) {
            logger.warn("Something went wrong on processing received payload.", throwable);
        }
    }

    public void publish(MqttMessage message) {
        if (message == null) {
            logger.warn("Cannot send empty message");
            return;
        }

        if (mqtt5Client == null || !mqtt5Client.getState().isConnected() || this.connectionState.getValue() != MqttClientConnectionState.Connected) {
            logger.warn("MQTT is not fully connected, ignoring message {}", message);
            return;
        }

        logger.debug("Publishing {}", message);
        mqtt5Client.publishWith()
                .topic(message.topic())
                .payload(message.payload().getBytes())
                .userProperties()
                    .add(CLIENT_ID, clientIdentifier)
                    .add(MESSAGE_ID, message.messageId())
                    .applyUserProperties()
                .qos(MqttQos.AT_LEAST_ONCE)
                .send()
                .whenComplete((Mqtt5PublishResult mqttPublishResult, Throwable throwable) -> {
                    if (throwable != null) {
                        logger.error("publish; ", throwable);
                    } else {
                        logger.debug("publish {}", mqttPublishResult);
                    }
                });
    }

}