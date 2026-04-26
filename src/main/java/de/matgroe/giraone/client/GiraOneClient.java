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
package de.matgroe.giraone.client;

import de.matgroe.giraone.GiraOneClientProperties;
import de.matgroe.giraone.client.types.GiraOneComponentType;
import de.matgroe.giraone.client.types.GiraOneDataPoint;
import de.matgroe.giraone.client.types.GiraOneDeviceConfiguration;
import de.matgroe.giraone.client.types.GiraOneProject;
import de.matgroe.giraone.client.types.GiraOneURN;
import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.giraone.client.webservice.GiraOneWebserviceClient;
import de.matgroe.giraone.client.websocket.GiraOneWebsocketClient;
import de.matgroe.giraone.client.websocket.GiraOneWebsocketConnectionState;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.ReplaySubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client for interacting with the Gira One Server. It delegates different commands to the
 * concerning websocket or webservice interface.
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneClient {
  private final Logger logger = LoggerFactory.getLogger(GiraOneClient.class);

  /** GiraOneClient via websocket API */
  private final GiraOneWebsocketClient websocketClient;

  /** Observe GiraOneClient via webservice API */
  private final GiraOneWebserviceClient webserviceClient;

  /** Observe this subject for Gira Server connection state */
  private final ReplaySubject<GiraOneClientConnectionState> clientConnectionState =
      ReplaySubject.createWithSize(1);

  /** Observe this subject for occured {@link GiraOneClientException} */
  private final Subject<GiraOneClientException> clientExceptions = PublishSubject.create();

  private GiraOneProject giraOneProject = new GiraOneProject();

  private final GiraOneClientProperties configuration;

  /**
   * Constructor.
   *
   * @param config The {@link GiraOneClientProperties}
   */
  public GiraOneClient(final GiraOneClientProperties config) {
    this(config, new GiraOneWebsocketClient(config), new GiraOneWebserviceClient(config));
  }

  /**
   * Constructor.
   *
   * @param config The {@link GiraOneClientProperties}
   * @param websocketClient The {@link GiraOneWebsocketClient} to use
   * @param webserviceClient The {@link GiraOneWebserviceClient} to use
   */
  public GiraOneClient(
      final GiraOneClientProperties config,
      GiraOneWebsocketClient websocketClient,
      GiraOneWebserviceClient webserviceClient) {
    this.websocketClient = websocketClient;
    this.webserviceClient = webserviceClient;
    this.configuration = config;
    this.websocketClient.subscribeOnConnectionState(this::onWebsocketConnectionState);
  }

  private void onWebsocketConnectionState(
      GiraOneWebsocketConnectionState giraOneWebsocketConnectionState) {
    logger.info("GiraOneWebsocketConnectionState changed to {}", giraOneWebsocketConnectionState);
    switch (giraOneWebsocketConnectionState) {
      case Connected -> this.loadGiraOneProject();
      case Error -> clientConnectionState.onNext(GiraOneClientConnectionState.Error);
      case Connecting -> clientConnectionState.onNext(GiraOneClientConnectionState.Connecting);
      case Disconnected -> clientConnectionState.onNext(GiraOneClientConnectionState.Disconnected);
    }
  }

  /**
   * Register's a listener for changes on {@link GiraOneClientConnectionState}.
   *
   * @param consumer The Consumer for {@link GiraOneClientConnectionState} changes.
   * @return a {@link Disposable}
   */
  public Disposable observeGiraOneConnectionState(Consumer<GiraOneClientConnectionState> consumer) {
    return clientConnectionState.distinctUntilChanged().subscribe(consumer);
  }

  /**
   * Register's a listener for any {@link GiraOneClientException} within on communicating with Gira
   * One Server.
   *
   * @param consumer The Consumer for {@link GiraOneClientException} changes.
   * @return a {@link Disposable}
   */
  public Disposable observeOnGiraOneClientExceptions(Consumer<GiraOneClientException> consumer) {
    return clientExceptions.subscribe(consumer);
  }

  /**
   * Initiates a connection to Gira One Server. The current connection state is reported
   * through the {@link GiraOneClientConnectionState} observer. Register on
   * {@link GiraOneClient#observeGiraOneConnectionState(Consumer)
   * to get informed about connection state changes.
   */
  public void connect() throws GiraOneClientException {
    logger.info("Initiating a server connect via webservice");
    try {
      this.clientConnectionState.onNext(GiraOneClientConnectionState.Connecting);
      this.webserviceClient.connect();
      this.websocketClient.connect();
    } catch (GiraOneCommunicationException commExp) {
      this.clientConnectionState.onNext(GiraOneClientConnectionState.Error);
      throw new GiraOneClientException(GiraOneClientException.CONNECT_REFUSED, commExp);
    }
  }

  private void loadGiraOneProject() {
    giraOneProject = new GiraOneProject();
    try {
      if (configuration.discoverButtons) {
        this.webserviceClient
            .lookupGiraOneComponentCollection()
            .getAllChannels(GiraOneComponentType.KnxButton)
            .forEach(giraOneProject::addChannel);
      }
      this.websocketClient
          .lookupGiraOneChannels()
          .getChannels()
          .forEach(giraOneProject::addChannel);
      clientConnectionState.onNext(GiraOneClientConnectionState.Connected);
    } catch (GiraOneCommunicationException e) {
      this.clientConnectionState.onNext(GiraOneClientConnectionState.Error);
      giraOneProject = new GiraOneProject();
      this.clientExceptions.onNext(
          new GiraOneClientException(GiraOneClientException.WEBSERVICE_COMMUNICATION, e));
    }
  }

  public GiraOneProject getGiraOneProject() {
    return this.giraOneProject;
  }

  /** Terminate the connection to Gira One Server. */
  public void disconnect() {
    this.websocketClient.disconnect();
  }

  public GiraOneDeviceConfiguration lookupGiraOneDeviceConfiguration() {
    return this.websocketClient.lookupGiraOneDeviceConfiguration();
  }

  /**
   * Initiates the value lookup for the given {@link GiraOneDataPoint}. The determined value will be
   * available through a registered consumer on {@link GiraOneClient#observeGiraOneValues(Consumer)}
   *
   * @param dataPoint The Datapoint to lookup it's value.
   */
  public void lookupGiraOneDatapointValue(GiraOneDataPoint dataPoint) {
    if (!dataPoint.getUrn().equals(GiraOneURN.INVALID)) {
      this.websocketClient.lookupGiraOneDataPointValue(dataPoint);
    } else {
      logger.warn(
          "lookupGiraOneDatapointValue :: skipping lookup for GiraOneDataPoint '{}'", dataPoint);
    }
  }

  /**
   * Changes the value for a {@link GiraOneDataPoint}.
   *
   * @param dataPoint The {@link GiraOneDataPoint} to change.
   * @param newValue The new value.
   */
  public void changeGiraOneDataPointValue(GiraOneDataPoint dataPoint, String newValue) {
    this.websocketClient.changeGiraOneDataPointValue(dataPoint, newValue);
  }

  /**
   * Register's a listener for {@link GiraOneValue}. A value is getting reported on receiving an
   * event from gira one server. This may be initiated by invoking {@link
   * #lookupGiraOneDatapointValue(GiraOneDataPoint)} or by any value event as received from the Gira
   * One Server.
   *
   * @param consumer The Consumer for {@link GiraOneValue} changes.
   * @return a {@link Disposable}
   */
  public Disposable observeGiraOneValues(Consumer<GiraOneValue> consumer) {
    return this.websocketClient.subscribeOnGiraOneValues(consumer);
  }
}
