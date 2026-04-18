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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAckReasonCode;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishResult;
import de.matgroe.giraone.client.GiraOneClient;
import de.matgroe.giraone.client.GiraOneClientException;
import de.matgroe.giraone.client.types.GiraOneChannelType;
import de.matgroe.giraone.client.types.GiraOneDeviceConfiguration;
import de.matgroe.giraone.client.types.GiraOneProject;
import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.mqtt.types.DiscoveryMessage;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.subjects.ReplaySubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class GiraOneMqttBridge {
    private final Logger logger = LoggerFactory.getLogger(GiraOneMqttBridge.class);

    /** Observe this subject for MQTT Broker connection state */
    private final ReplaySubject<MqttClientConnectionState> connectionState = ReplaySubject.createWithSize(1);

    private final Disposable connectionStateDisposable;

    private final MqttConfiguration mqttConfiguration;

    private final GiraOneClient giraOneClient;

    private MqttDiscoveryMessageFactory mqttDiscoveryMessageFactory;

    private MqttComponentFactory mqttComponentFactory;

    private MqttTopicNameMapper mqttTopicNameMapper;

    private final Gson gson;

    Mqtt5AsyncClient mqttClient;

    private Subject<GiraOneValue> giraInbound;

    private Subject<GiraOneValue> giraOutbound;

    public GiraOneMqttBridge(MqttConfiguration mqttConfiguration, GiraOneClient giraOneClient)  {
                this.mqttConfiguration = mqttConfiguration;
        this.giraOneClient = giraOneClient;
        this.gson = new GsonBuilder().create();
        this.connectionState.onNext(MqttClientConnectionState.Disconnected);
        this.connectionStateDisposable = this.connectionState.subscribe(this::onConnectionStateChanged);
    }

    private void onConnectionStateChanged(MqttClientConnectionState mqttClientConnectionState) {
        logger.debug("MqttClientConnectionState changed to {}", mqttClientConnectionState);
        String topicFilter = String.format("%s/#", formatTopicPrefix());
        if (mqttClientConnectionState == MqttClientConnectionState.Connected) {
            mqttClient.subscribeWith()
                    .topicFilter(topicFilter)
                    .callback(this::onMessageReceived)
                    .send();

            this.sendDiscoveryMessage();
        } else {
            if (mqttClient != null) {
                mqttClient.unsubscribeWith().topicFilter(topicFilter).send();
            }
        }
    }

    /**
     * Disconnect from Broker
     */
    public void disconnect() {
        if (mqttClient != null && mqttClient.getState().isConnected()) {
            mqttClient.disconnect().whenComplete((Void unused, Throwable throwable) -> {
                if (throwable != null) {
                    logger.error("Error on disconnecting from '{}'", mqttConfiguration.getMqttBroker(), throwable);
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
    public Disposable observeGiraOneConnectionState(Consumer<MqttClientConnectionState> consumer) {
        return connectionState.subscribe(consumer);
    }

    private String formatTopicPrefix() {
        try {
            GiraOneDeviceConfiguration cfg = giraOneClient.lookupGiraOneDeviceConfiguration();
            return String.format("%s/%s", cfg.get(GiraOneDeviceConfiguration.DEVICE_NAME), cfg.get(GiraOneDeviceConfiguration.DEVICE_ID));
        } catch (GiraOneClientException exp) {
            logger.warn("Caannot format topic prefix. ", exp);
            return mqttConfiguration.getApplicationName();
        }
    }

    private void initializeFactories() {
        this.mqttDiscoveryMessageFactory = new MqttDiscoveryMessageFactory(mqttConfiguration, giraOneClient);
        this.mqttTopicNameMapper = new MqttTopicNameMapper(formatTopicPrefix());
        this.mqttComponentFactory = new MqttComponentFactory(this.mqttTopicNameMapper);
    }

    /**
     *  Connects to the MQTT Broker as given within the {@link MqttConfiguration} object.
     */
    public void connect() {
        // just to be safe
        initializeFactories();
        disconnect();
        this.connectionState.onNext(MqttClientConnectionState.Connecting);

        mqttClient = MqttClient.builder()
                .useMqttVersion5()
                .identifier(UUID.randomUUID().toString())
                .serverHost(mqttConfiguration.getMqttBroker())
                .serverPort(mqttConfiguration.getMqttPort())
                .buildAsync();

        mqttClient.connectWith()
                .simpleAuth()
                .username(mqttConfiguration.getUsername())
                .password(mqttConfiguration.getPassword().getBytes())
                .applySimpleAuth()
                .send()
                .whenComplete((Mqtt5ConnAck connAck, Throwable throwable) -> {
                    if (connAck != null) {
                        logger.debug("MQTT Connect Completes with :: {}", connAck);
                        if (connAck.getReasonCode() == Mqtt5ConnAckReasonCode.SUCCESS) {
                            this.connectionState.onNext(MqttClientConnectionState.Connected);
                        }
                    }
                });


    }



    private void onMessageReceived(Mqtt5Publish mqtt5Publish) {
        try {
            mqtt5Publish.getPayload().ifPresentOrElse((ByteBuffer byteBuffer) -> {
                String payload = StandardCharsets.UTF_8.decode(byteBuffer).toString();
                logger.debug("'{}' --->> {}", mqtt5Publish.getTopic(), payload);
            }, () -> {
                logger.info("'{}' --->> [empty payload]", mqtt5Publish.getTopic());
            });
        } catch (Throwable throwable) {
            logger.warn("Something went wrong on processing received payload.", throwable);
        }
    }

    public void publish(GiraOneValue giraOneValue) {
        if (this.connectionState.getValue() == MqttClientConnectionState.Connected) {
            String topic = mqttTopicNameMapper.topicNameOf(giraOneValue.getGiraOneDataPoint());
            publish(topic, giraOneValue.getValue());
        } else {
            logger.warn("MQTT is not fully connected, ignoring message {}", giraOneValue);
        }
    }

    void publish(final String topic, final Object object) {
        publish(topic, gson.toJson(object));
    }

    void publish(final String topic, final String payload) {
        if (mqttClient == null || !mqttClient.getState().isConnected()) {
            logger.info("MQTT is not connected, skip publish message '{}' to {}", payload,topic);
            return;
        }
        logger.debug("'{}' <<--- {}", topic, payload);
        mqttClient.publishWith()
                .topic(topic)
                .payload(payload.getBytes())
                .qos(MqttQos.EXACTLY_ONCE)
                .send()
                .whenComplete((Mqtt5PublishResult mqttPublishResult, Throwable throwable) -> {
                    if (throwable != null) {
                        logger.error("publish; ", throwable);
                    } else {
                        logger.info("publish {}", mqttPublishResult);
                    }
                });
    }


    private void sendDiscoveryMessage() {
        logger.info("Create and send DiscoveryMessage");
        DiscoveryMessage dm = mqttDiscoveryMessageFactory.createDiscoveryMessage();
        GiraOneProject project = this.giraOneClient.getGiraOneProject();
        project.lookupChannels().stream().filter(ch -> ch.getChannelType() == GiraOneChannelType.Status).forEach(channel -> {
            dm.addComponent(mqttComponentFactory.from(channel));
        });
        publish(mqttDiscoveryMessageFactory.createDiscoveryTopic(), dm);
    }
}