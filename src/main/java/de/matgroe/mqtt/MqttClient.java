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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MqttClient {
    private final Logger logger = LoggerFactory.getLogger(MqttClient.class);

    /** Observe this subject for MQTT Broker connection state */
    private final ReplaySubject<MqttClientConnectionState> connectionState = ReplaySubject.createWithSize(1);

    /** this subject receives the incoming messages from {@link MqttClient} */
    private final Subject<MqttMessage> inboundQueue = PublishSubject.create();

    private final MqttClientProperties mqttClientProperties;

    private String topicNamePrefix = "";

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
     *  Connects to the MQTT Broker as given within the {@link MqttClientProperties} object.
     */
    public void connect(String topicNamePrefix) {
        this.topicNamePrefix = topicNamePrefix;
        disconnect();
        this.connectionState.onNext(MqttClientConnectionState.Connecting);
        mqtt5Client = com.hivemq.client.mqtt.MqttClient.builder()
                .useMqttVersion5()
                .identifier(UUID.randomUUID().toString())
                .serverHost(mqttClientProperties.getMqttBroker())
                .serverPort(mqttClientProperties.getMqttPort())
                .buildAsync();

        mqtt5Client.connectWith()
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



    private void onMessageReceived(Mqtt5Publish mqtt5Publish) {
        try {
            mqtt5Publish.getPayload().ifPresentOrElse((ByteBuffer byteBuffer) -> {
                String payload = StandardCharsets.UTF_8.decode(byteBuffer).toString();
                logger.info("received from topic: '{}'. Payload: '{}'", mqtt5Publish.getTopic(), payload);
                this.inboundQueue.onNext(new MqttMessage(mqtt5Publish.getTopic(), payload));
            }, () -> {
                logger.info("received from topic: '{}'. {Empty Payload}", mqtt5Publish.getTopic());
                this.inboundQueue.onNext(new MqttMessage(mqtt5Publish.getTopic(), null));
            });
        } catch (Throwable throwable) {
            logger.warn("Something went wrong on processing received payload.", throwable);
        }
    }

    public void publish(MqttMessage message) {
        if (this.connectionState.getValue() == MqttClientConnectionState.Connected) {
            logger.info("Publishing MqttMessage: {}", message);
            publish(message.getTopic(), message.getPayload());
        } else {
            logger.warn("MQTT is not fully connected, ignoring message {}", message);
        }
    }

    void publish(final String topic, final String payload) {
        if (mqtt5Client == null || !mqtt5Client.getState().isConnected()) {
            logger.info("MQTT is not connected, skip publish message '{}' to {}", payload,topic);
            return;
        }
        logger.debug("Publish to topic: '{}' with payload: '{}'", topic, payload);
        mqtt5Client.publishWith()
                .topic(topic)
                .payload(payload.getBytes())
                .qos(MqttQos.AT_LEAST_ONCE)
                .send()
                .whenComplete((Mqtt5PublishResult mqttPublishResult, Throwable throwable) -> {
                    if (throwable != null) {
                        logger.error("publish; ", throwable);
                    } else {
                        logger.info("publish {}", mqttPublishResult);
                    }
                });
    }

}