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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.matgroe.GiraOneMqttApplicationProperties;
import de.matgroe.giraone.client.GiraOneClient;
import de.matgroe.giraone.client.GiraOneClientConnectionState;
import de.matgroe.giraone.client.GiraOneClientException;
import de.matgroe.giraone.client.types.GiraOneDataPoint;
import de.matgroe.giraone.client.types.GiraOneDeviceConfiguration;
import de.matgroe.giraone.client.types.GiraOneProject;
import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.hassio.HassioComponentFactory;
import de.matgroe.hassio.HassioDiscoveryMessageFactory;
import de.matgroe.hassio.types.DiscoveryMessage;
import de.matgroe.hassio.types.UnsupportedComponent;
import de.matgroe.mqtt.MqttClient;
import de.matgroe.mqtt.MqttClientConnectionState;
import de.matgroe.mqtt.MqttMessage;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.ReplaySubject;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** This class is responsible for dispatching .. */
@Component
public class GiraOneMqttBridge {
  private static final String GDS_DEVICE_CHANNEL_URN =
      "urn:gds:dp:GiraOneServer.GIOSRVKX03:GDS-Device-Channel";
  private static final String GDS_DEVICE_DATAPOINT_READY = "Ready";
  private static final String GDS_DEVICE_DATAPOINT_LOCAL_TIME = "Local-Time";

  private final ReplaySubject<GiraOneMqttBridgeState> bridgeState = ReplaySubject.createWithSize(1);
  private final Logger logger = LoggerFactory.getLogger(GiraOneMqttBridge.class);
  private final CompositeDisposable giraOneClientDisposables = new CompositeDisposable();
  private final CompositeDisposable mqttClientDiposables = new CompositeDisposable();

  private final GiraOneMqttApplicationProperties applicationProperties;
  private final GiraOneClient giraOneClient;
  private final MqttClient mqttClient;

  private Disposable giraoneValueDiposable = Disposable.empty();
  private Disposable bridgeStateDiposable = Disposable.empty();

  private MessageTransformer messageTransformer;

  private HassioDiscoveryMessageFactory hassioDiscoveryMessageFactory;

  private HassioComponentFactory hassioComponentFactory;

  private GiraOneChannelMqttTopicMapper giraOneChannelMqttTopicMapper;

  public GiraOneMqttBridge(
      GiraOneMqttApplicationProperties applicationProperties,
      GiraOneClient giraOneClient,
      MqttClient mqttClient) {
    this.giraOneClient = giraOneClient;
    this.mqttClient = mqttClient;
    this.applicationProperties = applicationProperties;

    bridgeState.onNext(GiraOneMqttBridgeState.Stopped);
  }

  public boolean isExecuteable() {
    return (this.bridgeState.getValue() != GiraOneMqttBridgeState.Error);
  }

  public void run(String... args) throws Exception {
    // Register for own state changes
    bridgeStateDiposable = bridgeState.subscribe(this::onGiraOneMqttBridgeStateChanged);

    // register for MqttClient state changes
    mqttClientDiposables.add(
        mqttClient.observeMqttConnectionState(this::onMqttClientConnectionStateChanged));

    // register for MqttClient messages
    mqttClientDiposables.add(mqttClient.observeInboundQueue(this::onMqttMessage));

    // Register at GiraOneClient for all Exceptions
    giraOneClientDisposables.add(
        giraOneClient.observeOnGiraOneClientExceptions(this::onGiraOneClientException));

    // Register for ConnectionState changes
    giraOneClientDisposables.add(
        this.giraOneClient.observeGiraOneConnectionState(
            this::onGiraOneClientConnectionStateChanged));

    // Register for GiraServer State Updates like time
    giraOneClientDisposables.add(
        subscribeOnGiraOneDataPointValues(
            String.format("%s:.*", GDS_DEVICE_CHANNEL_URN), this::onDeviceChannelEvent));

    bridgeState.onNext(GiraOneMqttBridgeState.ConnectingGiraOneClient);
  }

  /**
   * Observing function for {@link GiraOneMqttBridgeState} changes
   *
   * @param bridgeState The {@link GiraOneMqttBridge}'s connection state.
   */
  void onGiraOneMqttBridgeStateChanged(GiraOneMqttBridgeState bridgeState) {
    logger.info("GiraOneMqttBridgeState changed to '{}'", bridgeState);
    switch (bridgeState) {
      case Stopped -> handleBridgeStateStopped();
      case ConnectingGiraOneClient -> handleBridgeStateConnectingGiraOneClient();
      case ConnectingMqttClient -> handleBridgeStateConnectingMqttClient();
      case Connected -> handleBridgeStateConnected();
      case Disconnected -> handleBridgeStateDisconnected();
      case Error -> handleBridgeStateError();
    }
  }

  /** Handler for GiraOneMqttBridgeState changed to {@link GiraOneMqttBridgeState#Stopped} */
  void handleBridgeStateStopped() {
    giraOneClient.disconnect();
    mqttClient.disconnect();
  }

  /**
   * Handler for GiraOneMqttBridgeState changed to {@link
   * GiraOneMqttBridgeState#ConnectingGiraOneClient}
   */
  void handleBridgeStateConnectingGiraOneClient() {
    try {
      this.giraOneClient.connect();
    } catch (GiraOneClientException exp) {
      logger.error("Failed connect to GiraOneClient.", exp);
      this.bridgeState.onNext(GiraOneMqttBridgeState.Error);
    }
  }

  /**
   * Handler for GiraOneMqttBridgeState changed to {@link
   * GiraOneMqttBridgeState#ConnectingMqttClient}
   */
  void handleBridgeStateConnectingMqttClient() {
    GiraOneDeviceConfiguration cfg = giraOneClient.lookupGiraOneDeviceConfiguration();
    String topicNamePrefix =
        String.format(
            "%s/%s",
            cfg.get(GiraOneDeviceConfiguration.DEVICE_NAME),
            cfg.get(GiraOneDeviceConfiguration.DEVICE_ID));
    mqttClient.connect(topicNamePrefix);

    this.hassioDiscoveryMessageFactory =
        new HassioDiscoveryMessageFactory(
            applicationProperties, giraOneClient.lookupGiraOneDeviceConfiguration());
    this.giraOneChannelMqttTopicMapper =
        new GiraOneChannelMqttTopicMapper(topicNamePrefix, giraOneClient.getGiraOneProject());
    this.hassioComponentFactory = new HassioComponentFactory(this.giraOneChannelMqttTopicMapper);
    this.messageTransformer =
        new MessageTransformer(giraOneChannelMqttTopicMapper, giraOneClient.getGiraOneProject());
  }

  /** Handler for GiraOneMqttBridgeState changed to {@link GiraOneMqttBridgeState#Connected} */
  void handleBridgeStateConnected() {
    sendDiscoveryMessage();
    giraoneValueDiposable = giraOneClient.observeGiraOneValues(this::onGiraOneValue);
    this.lookupGiraOneDataPoints();
  }

  void lookupGiraOneDataPoints() {
    Thread.ofVirtual()
        .start(
            () -> {
              giraOneClient
                  .getGiraOneProject()
                  .lookupGiraOneDataPoints()
                  .forEach(
                      (GiraOneDataPoint dataPoint) -> {
                        giraOneClient.lookupGiraOneDatapointValue(dataPoint);
                        try {
                          Thread.sleep(300);
                        } catch (InterruptedException e) {
                          throw new RuntimeException(e);
                        }
                      });
            });
  }

  /** Handler for GiraOneMqttBridgeState changed to {@link GiraOneMqttBridgeState#Disconnected} */
  void handleBridgeStateDisconnected() {}

  /** Handler for GiraOneMqttBridgeState changed to {@link GiraOneMqttBridgeState#Error} */
  void handleBridgeStateError() {
    try {
      this.mqttClient.disconnect();
      this.giraOneClient.disconnect();
    } finally {
      giraOneClientDisposables.dispose();
      bridgeStateDiposable.dispose();
    }
  }

  /**
   * This callback handles the received messages from the GiraOneServer.
   *
   * @param value The Received {@link GiraOneValue}
   */
  private void onDeviceChannelEvent(GiraOneValue value) {
    logger.info("onDeviceChannelEvent:: {}", value);
  }

  private void onGiraOneClientException(GiraOneClientException clientException) {
    logger.error("GiraOneClientException {}", clientException.getMessage(), clientException);
  }

  /**
   * Observing function for {@link GiraOneClientConnectionState} changes
   *
   * @param connectionState The {@link GiraOneClient}'s connection state.
   */
  void onGiraOneClientConnectionStateChanged(GiraOneClientConnectionState connectionState) {
    logger.info("GiraOneClientConnectionState changed to {}", connectionState);
    switch (connectionState) {
      case Connected -> bridgeState.onNext(GiraOneMqttBridgeState.ConnectingMqttClient);
      case Disconnected -> bridgeState.onNext(GiraOneMqttBridgeState.Disconnected);
      case Error -> bridgeState.onNext(GiraOneMqttBridgeState.Error);
    }
  }

  /**
   * Observing function for {@link GiraOneClientConnectionState} changes
   *
   * @param connectionState The {@link GiraOneClient}'s connection state.
   */
  void onMqttClientConnectionStateChanged(MqttClientConnectionState connectionState) {
    logger.info("onMqttClientConnectionStateChanged changed to {}", connectionState);
    switch (connectionState) {
      case Connected -> bridgeState.onNext(GiraOneMqttBridgeState.Connected);
      case Disconnected -> bridgeState.onNext(GiraOneMqttBridgeState.Disconnected);
      case Error -> bridgeState.onNext(GiraOneMqttBridgeState.Error);
    }
  }

  /**
   * This method handles incoming mqtt messages and forwards them to the {@link GiraOneClient}
   *
   * @param mqttMessage
   */
  private void onMqttMessage(MqttMessage mqttMessage) {
    logger.info("Received MqttMessage:: {}", mqttMessage);
    Optional<GiraOneValue> value = messageTransformer.from(mqttMessage).toGiraOneValue();
    value.ifPresent(dataPoint -> giraOneClient.changeGiraOneDataValue(value.get()));
  }

  /**
   * This method handles incoming {@link GiraOneValue } messages and forwards them to the {@link
   * MqttClient}
   *
   * @param giraOneValue
   */
  void onGiraOneValue(GiraOneValue giraOneValue) {
    Optional<MqttMessage> mqttMessage = messageTransformer.from(giraOneValue).toMqttMessage();
    mqttMessage.ifPresent(dataPoint -> mqttClient.publish(mqttMessage.get()));
  }

  public Disposable subscribeOnGiraOneDataPointValues(
      final String deviceUrnPattern, Consumer<GiraOneValue> consumer) {
    // return this.datapointValues.filter(f ->
    // f.getDatapointUrn().matches(deviceUrnPattern)).subscribe(consumer);
    return Disposable.empty();
  }

  private void sendDiscoveryMessage() {
    logger.info("Create and send DiscoveryMessage");
    DiscoveryMessage dm = hassioDiscoveryMessageFactory.createDiscoveryMessage();
    GiraOneProject project = this.giraOneClient.getGiraOneProject();

    project.lookupChannels().stream()
        .map(hassioComponentFactory::from)
        .filter(u -> u.getClass() != UnsupportedComponent.class)
        .toList()
        .forEach(dm::addComponent);

    Gson gson = new GsonBuilder().create();
    MqttMessage discoveryMessage =
        new MqttMessage(hassioDiscoveryMessageFactory.createDiscoveryTopic(), gson.toJson(dm));
    logger.info("Publishing MqttDiscoveryMessage:: {}", discoveryMessage);
    this.mqttClient.publish(discoveryMessage);
  }
}
