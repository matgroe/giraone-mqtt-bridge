/*
 * MIT License
 *
 * Copyright (c) 2026 Matthias Gröger
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.matgroe.bridge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.matgroe.GiraOneMqttApplicationProperties;
import de.matgroe.bridge.translators.giraone.GiraOneTranslatorFactory;
import de.matgroe.bridge.translators.mqtt.MqttTranslatorFactory;
import de.matgroe.giraone.client.GiraOneClient;
import de.matgroe.giraone.client.GiraOneClientConnectionState;
import de.matgroe.giraone.client.GiraOneClientException;
import de.matgroe.giraone.client.types.GiraOneChannel;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** This class is responsible for dispatching .. */
@Component
public class GiraOneMqttBridge {
  private final String BRIDGE_DATAPOINT_UPTIME = "urn:de:matgroe:giraone-bridge:Uptime";

  private final Logger logger = LoggerFactory.getLogger(GiraOneMqttBridge.class);
  private final CompositeDisposable giraOneClientDisposables = new CompositeDisposable();
  private final CompositeDisposable mqttClientDiposables = new CompositeDisposable();
  private final GiraOneMqttApplicationProperties applicationProperties;
  private final GiraOneClient giraOneClient;
  private final MqttClient mqttClient;

  private Disposable giraoneValueDiposable = Disposable.empty();
  private Disposable bridgeStateDiposable = Disposable.empty();
  private MqttTranslatorFactory mqttTranslatorFactory;
  private GiraOneTranslatorFactory giraOneTranslatorFactory;
  private HassioDiscoveryMessageFactory hassioDiscoveryMessageFactory;
  private HassioComponentFactory hassioComponentFactory;
  private GiraOneChannelMqttTopicMapper giraOneChannelMqttTopicMapper;

  final ReplaySubject<GiraOneMqttBridgeState> bridgeState = ReplaySubject.createWithSize(1);

  public GiraOneMqttBridge(
      GiraOneMqttApplicationProperties applicationProperties,
      GiraOneClient giraOneClient,
      MqttClient mqttClient) {
    this.giraOneClient = giraOneClient;
    this.mqttClient = mqttClient;
    this.applicationProperties = applicationProperties;

    bridgeState.onNext(GiraOneMqttBridgeState.Stopped);
  }

  /**
   * @return
   */
  public boolean isExecuteable() {
    return (this.bridgeState.getValue() != GiraOneMqttBridgeState.Error);
  }

  /**
   * Initializes the {@link GiraOneMqttBridge} by subscribing all related Observables. It also
   * initiates the connection to the GiraOneServer. This must be the first call on the newly created
   * instance.
   */
  public void initialize() {
    // register for MqttClient state changes
    mqttClientDiposables.add(
        mqttClient.observeMqttConnectionState(this::onMqttClientConnectionStateChanged));

    // register for incoming MqttClient messages
    mqttClientDiposables.add(
        mqttClient.observeInboundQueue(this::onMqttMessage, this::onMqttMessageProcessingError));

    // Register at GiraOneClient for all Exceptions
    giraOneClientDisposables.add(
        giraOneClient.observeOnGiraOneClientExceptions(this::onGiraOneClientException));

    // Register for GiraOneClient ConnectionState changes
    giraOneClientDisposables.add(
        this.giraOneClient.observeGiraOneConnectionState(
            this::onGiraOneClientConnectionStateChanged));

    // Register for own state changes
    bridgeStateDiposable = bridgeState.subscribe(this::onGiraOneMqttBridgeStateChanged);
    bridgeState.onNext(GiraOneMqttBridgeState.ConnectingGiraOneClient);
  }

  /**
   * Observing function for (internal) {@link GiraOneMqttBridgeState} changes
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
    this.mqttTranslatorFactory =
        new MqttTranslatorFactory(giraOneChannelMqttTopicMapper, giraOneClient.getGiraOneProject());

    this.giraOneTranslatorFactory =
        new GiraOneTranslatorFactory(
            giraOneChannelMqttTopicMapper, giraOneClient.getGiraOneProject());
  }

  /** Handler for GiraOneMqttBridgeState changed to {@link GiraOneMqttBridgeState#Connected} */
  void handleBridgeStateConnected() {
    sendDiscoveryMessage();
    this.onGiraOneValue(new GiraOneValue(BRIDGE_DATAPOINT_UPTIME, "now"));
    giraoneValueDiposable =
        giraOneClient.observeGiraOneValues(
            this::onGiraOneValue, this::onGiraOneValueProcessingError);
    this.lookupGiraOneDataPoints();
  }

  void lookupGiraOneDataPoints() {
    Thread.ofVirtual()
        .start(
            () -> {
              giraOneClient.getGiraOneProject().lookupGiraOneDataPoints().stream()
                  .filter(this::mapsToSupportedComponent)
                  .forEach(giraOneClient::lookupGiraOneDatapointValue);
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
  void onMqttMessage(MqttMessage mqttMessage) {
    logger.info("Received MqttMessage:: {}", mqttMessage);
    mqttTranslatorFactory.from(mqttMessage).toGiraOneValue().stream()
        .map(giraOneClient::changeGiraOneDataValue)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(this::onGiraOneValue);
  }

  void onMqttMessageProcessingError(Throwable throwable) {
    logger.error("Caught Exception on proseccing MqttMessage.", throwable);
    this.bridgeState.onNext(GiraOneMqttBridgeState.Error);
  }

  private boolean mapsToSupportedComponent(GiraOneDataPoint datapoint) {
    Optional<GiraOneChannel> channel =
        giraOneClient.getGiraOneProject().lookupChannelByDataPoint(datapoint);
    return channel
        .filter(
            giraOneChannel ->
                !(hassioComponentFactory.from(giraOneChannel) instanceof UnsupportedComponent))
        .isPresent();
  }

  /**
   * This method handles incoming {@link GiraOneValue } messages and forwards them to the {@link
   * MqttClient}
   *
   * @param giraOneValue
   */
  void onGiraOneValue(GiraOneValue giraOneValue) {
    if (this.mapsToSupportedComponent(giraOneValue.getGiraOneDataPoint())) {
      logger.info("Received giraOneValue :: {}", giraOneValue);
      giraOneTranslatorFactory.from(giraOneValue).toMqttMessage().forEach(mqttClient::publish);
    } else {
      logger.info("Ignoring giraOneValue :: {}", giraOneValue);
    }
  }

  void onGiraOneValueProcessingError(Throwable throwable) {
    logger.error("Caught Exception on proseccing GiraOneValue.", throwable);
  }

  private void sendDiscoveryMessage() {
    logger.info("Create and send DiscoveryMessage");
    DiscoveryMessage dm = hassioDiscoveryMessageFactory.createDiscoveryMessage();
    GiraOneProject project = this.giraOneClient.getGiraOneProject();
    project.addDiagnosticChannel(
        "urn:de:matgroe:giraone-bridge:uptime", "Startzeit", BRIDGE_DATAPOINT_UPTIME);
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
