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
package de.matgroe.giraone.client.websocket;

import com.google.gson.Gson;
import de.matgroe.giraone.GiraOneClientProperties;
import de.matgroe.giraone.client.GiraOneClientConnectionState;
import de.matgroe.giraone.client.GiraOneClientException;
import de.matgroe.giraone.client.GiraOneCommand;
import de.matgroe.giraone.client.GiraOneCommandResponse;
import de.matgroe.giraone.client.GiraOneMessageType;
import de.matgroe.giraone.client.GiraOneTypeMapperFactory;
import de.matgroe.giraone.client.commands.GetDeviceConfig;
import de.matgroe.giraone.client.commands.GetUIConfiguration;
import de.matgroe.giraone.client.commands.GetValue;
import de.matgroe.giraone.client.commands.RegisterApplication;
import de.matgroe.giraone.client.commands.SetValue;
import de.matgroe.giraone.client.types.GiraOneChannelCollection;
import de.matgroe.giraone.client.types.GiraOneDataPoint;
import de.matgroe.giraone.client.types.GiraOneDeviceConfiguration;
import de.matgroe.giraone.client.types.GiraOneEvent;
import de.matgroe.giraone.client.types.GiraOneURN;
import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.giraone.client.types.GiraOneValueChange;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.ReplaySubject;
import io.reactivex.rxjava3.subjects.Subject;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * The class acts as client for the Gira One Server and handles the communication via Websocket.
 *
 * @author Matthias Gröger - Initial contribution
 */
@Component
public class GiraOneWebsocketClient {

  private static final String TEMPLATE_WEBSOCKET_URL = "wss://%s:4432/gds/api?%s";
  private static final int DEFAULT_TIMEOUT_SECONDS = 10;

  private final CompositeDisposable websocketEndpointDisposables = new CompositeDisposable();
  private final Logger logger = LoggerFactory.getLogger(GiraOneWebsocketClient.class);
  private final Gson gson;
  private final String giraOneWssEndpoint;

  private int timeoutSeconds = DEFAULT_TIMEOUT_SECONDS;

  private Disposable dataPointDisposable = Disposable.empty();

  private final GiraOneWebsocketEndpoint websocketConnection;

  /** Observe this subject for received Command-Responses from Gira Server */
  final Subject<GiraOneWebsocketResponse> responses = PublishSubject.create();

  /** Observe this subject for received events from Gira Server */
  final Subject<GiraOneEvent> events = PublishSubject.create();

  /** Observe this subject for occuring {@link GiraOneClientException} */
  final Subject<GiraOneClientException> clientExceptions = PublishSubject.create();

  /**
   * Observe this subject for received values from Gira Server. It combines value {@link
   * GiraOneEvent} and {@link GetValue} responses as well.
   */
  final Subject<GiraOneValue> values = PublishSubject.create();

  /** Observe this subject for Gira Server connection state */
  final ReplaySubject<GiraOneWebsocketConnectionState> connectionState =
      ReplaySubject.createWithSize(1);

  /**
   * Constructor
   *
   * @param config A {@link GiraOneClientProperties} object
   */
  public GiraOneWebsocketClient(final GiraOneClientProperties config) {
    Objects.requireNonNull(config.hostname, "GiraOneClientProperties 'hostname' must not be null");
    Objects.requireNonNull(config.username, "GiraOneClientProperties 'username' must not be null");
    Objects.requireNonNull(config.password, "GiraOneClientProperties 'password' must not be null");

    this.gson = GiraOneTypeMapperFactory.createGson();
    this.giraOneWssEndpoint =
        String.format(
            TEMPLATE_WEBSOCKET_URL,
            config.hostname,
            computeWebsocketAuthToken(config.username, config.password));
    this.connectionState.onNext(GiraOneWebsocketConnectionState.Disconnected);
    this.timeoutSeconds = config.defaultTimeoutSeconds;
    this.websocketConnection = createGiraOneWebsocketConnection();
  }

  String computeWebsocketAuthToken(String username, String password) {
    String auth = String.format("%s:%s", username, password);
    return "ui" + new String(Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8)));
  }

  GiraOneWebsocketEndpoint createGiraOneWebsocketConnection() throws GiraOneClientException {
    return new GiraOneJdkWebsocketEndpoint();
  }

  void initiateWebsocketSession() {
    try {
      this.websocketEndpointDisposables.clear();

      // register callbacks
      websocketEndpointDisposables.add(
          this.websocketConnection.subscribeOnMessages(this::onWebSocketText));
      websocketEndpointDisposables.add(
          this.websocketConnection.subscribeOnThrowable(this::onWebSocketError));
      websocketEndpointDisposables.add(
          this.websocketConnection.subscribeOnConnectionState(this::onWebSocketConnectionState));
      websocketEndpointDisposables.add(
          this.websocketConnection.subscribeOnWebsocketCloseReason(this::onWebSocketClosed));

      this.websocketConnection.connectTo(URI.create(giraOneWssEndpoint));
    } catch (GiraOneClientException exp) {
      this.clientExceptions.onNext(
          new GiraOneClientException(GiraOneClientException.CONNECT_REFUSED, exp.getCause()));
      this.connectionState.onNext(GiraOneWebsocketConnectionState.Disconnected);
    }
  }

  /** Establish a new Websocket connection to the Gira One Server. */
  public void connect() {
    if (connectionState.getValue() != GiraOneWebsocketConnectionState.Disconnected) {
      this.disconnect();
    }
    logger.info("Connecting to {}", this.giraOneWssEndpoint);
    observeAndEmitDataPointValues();
    this.initiateWebsocketSession();
  }

  /** Terminate the websocket connection. */
  public void disconnect() {
    this.disconnect(GiraOneWebsocketCloseCode.NORMAL_CLOSURE);
  }

  private void disconnect(GiraOneWebsocketCloseCode closeReason) {
    logger.debug("Disconnecting with {}/{}", closeReason.getCode(), closeReason.toString());
    try {
      this.websocketConnection.disconnect(closeReason);
    } catch (Exception e) {
      throw new GiraOneClientException(GiraOneClientException.DISCONNECT_FAILED, e);
    } finally {
      this.connectionState.onNext(GiraOneWebsocketConnectionState.Disconnected);
      this.dataPointDisposable.dispose();
    }
  }

  void observeAndEmitDataPointValues() {
    // dispose existing observable
    dataPointDisposable.dispose();
    // and create a new one
    dataPointDisposable =
        Observable.merge(
                this.responses.filter(this::isGetValueResponse).map(this::createGiraOneValue),
                this.events.map(this::createGiraOneValue))
            .retry()
            .subscribe(this.values::onNext, this::onSubscriptionError);
  }

  private boolean isGetValueResponse(GiraOneWebsocketResponse response) {
    return response.getRequestServerCommand().getCommand() instanceof GetValue;
  }

  private void onSubscriptionError(Throwable throwable) {
    logger.error("onSubscriptionError :: {}", throwable.getMessage(), throwable);
  }

  private GiraOneValue createGiraOneValue(GiraOneCommandResponse response) {
    return response.getReply(GiraOneValue.class);
  }

  private GiraOneValue createGiraOneValue(GiraOneEvent event) {
    return new GiraOneValueChange(event.getUrn(), event.getNewValue(), event.getOldValue());
  }

  private void emitConnectionStateException(GiraOneClientConnectionState expected) {
    this.clientExceptions.onNext(
        new GiraOneClientException(
            GiraOneClientException.UNEXPECTED_CONNECTION_STATE,
            expected.toString(),
            connectionState.getValue().toString()));
  }

  public GiraOneDeviceConfiguration lookupGiraOneDeviceConfiguration() {
    if (connectionState.getValue() == GiraOneWebsocketConnectionState.Connected) {
      return execute(GetDeviceConfig.builder().build()).getReply(GiraOneDeviceConfiguration.class);
    }
    throw new GiraOneClientException(
        GiraOneClientException.UNEXPECTED_CONNECTION_STATE,
        GiraOneClientConnectionState.Connected.toString(),
        Objects.requireNonNull(connectionState.getValue()).toString());
  }

  public GiraOneChannelCollection lookupGiraOneChannels() {
    if (connectionState.getValue() == GiraOneWebsocketConnectionState.Connected) {
      return execute(GetUIConfiguration.builder().build()).getReply(GiraOneChannelCollection.class);
    }
    throw new GiraOneClientException(
        GiraOneClientException.UNEXPECTED_CONNECTION_STATE,
        GiraOneClientConnectionState.Connected.toString(),
        Objects.requireNonNull(connectionState.getValue()).toString());
  }

  /**
   * Emits as {@link GetValue} server command to lookup the current value for a datapoint.
   *
   * @param dataPoint The {@link GiraOneDataPoint} to lookup.
   */
  public void lookupGiraOneDataPointValue(final GiraOneDataPoint dataPoint) {
    if (connectionState.getValue() == GiraOneWebsocketConnectionState.Connected) {
      if (dataPoint.getUrn() != null && !GiraOneURN.INVALID.equals(dataPoint.getUrn())) {
        send(GetValue.builder().with(GetValue::setUrn, dataPoint.getUrn()).build());
      }
    } else {
      emitConnectionStateException(GiraOneClientConnectionState.Connected);
    }
  }

  /**
   * Emits as {@link SetValue} server command to change the value for a datapoint.
   *
   * @param dataPoint The {@link GiraOneDataPoint} to lookup.
   * @param value The new value to be set.
   */
  public void changeGiraOneDataPointValue(final GiraOneDataPoint dataPoint, Object value) {
    if (connectionState.getValue() == GiraOneWebsocketConnectionState.Connected) {
      send(
          SetValue.builder()
              .with(SetValue::setUrn, dataPoint.getUrn())
              .with(SetValue::setValue, value)
              .build());
    } else {
      this.logger.warn(
          "should changeGiraOneDataPointValue for dataPoint='{}' to '{}', but connectionState is {}",
          dataPoint,
          value,
          connectionState.getValue());
      emitConnectionStateException(GiraOneClientConnectionState.Connected);
    }
  }

  public Disposable subscribeOnConnectionState(Consumer<GiraOneWebsocketConnectionState> onNext) {
    return this.connectionState.distinctUntilChanged().subscribe(onNext, this::onSubscriptionError);
  }

  public Disposable subscribeOnGiraOneValues(Consumer<GiraOneValue> onNext) {
    return this.values.subscribe(onNext);
  }

  public Disposable subscribeOnGiraOneClientExceptions(Consumer<GiraOneClientException> onNext) {
    return this.clientExceptions.subscribe(onNext);
  }

  /**
   * Sends a {@link GiraOneCommand} to Gira One Server. This method is getting used as "fire and
   * forget".
   *
   * @param command The command to send
   */
  void send(GiraOneCommand command) {
    this.send(new GiraOneWebsocketRequest(command));
  }

  /**
   * Sends a {@link GiraOneWebsocketRequest} to Gira One Server. This method is getting used as
   * "fire and forget".
   *
   * @param command The command to send
   */
  void send(GiraOneWebsocketRequest command) {
    String message = gson.toJson(command, GiraOneWebsocketRequest.class);
    logger.trace("send GiraOneWebsocketRequest '{}' :: {}", command.getCommand(), message);
    this.websocketConnection.send(message);
  }

  /**
   * Sends a {@link GiraOneCommand} to Gira One Server and waits for the server's command response.
   *
   * @param command The command to send
   */
  GiraOneCommandResponse execute(GiraOneCommand command) {
    return this.execute(new GiraOneWebsocketRequest(command));
  }

  /**
   * Sends a {@link GiraOneWebsocketRequest} to Gira One Server and waits for the server's command
   * response.
   *
   * @param command The command to send
   */
  GiraOneCommandResponse execute(GiraOneWebsocketRequest command) {
    final CompletableFuture<GiraOneWebsocketResponse> promise = new CompletableFuture<>();
    logger.info("Executing GiraOneWebsocketRequest : {}", command.getCommand());
    Disposable disposable = Disposable.empty();
    try {
      disposable =
          this.responses.filter(f -> f.isInitiatedBy(command)).take(1).subscribe(promise::complete);
      // send out command
      send(command);
      // and wait for response
      return promise.get(timeoutSeconds, TimeUnit.SECONDS);
    } catch (TimeoutException | ExecutionException | InterruptedException exp) {
      throw new GiraOneClientException("Got exception on waiting for command response.", exp);
    } finally {
      disposable.dispose();
    }
  }

  public void onWebSocketText(String message) {
    logger.debug("Received Message :: {}", message);
    GiraOneMessageType type =
        Objects.requireNonNullElse(
            gson.fromJson(message, GiraOneMessageType.class), GiraOneMessageType.Invalid);
    switch (type) {
      case Event ->
          this.events.onNext(Objects.requireNonNull(gson.fromJson(message, GiraOneEvent.class)));
      case Response ->
          this.responses.onNext(
              Objects.requireNonNull(gson.fromJson(message, GiraOneWebsocketResponse.class)));
      case Invalid -> this.logger.warn("invalid message received :: {}", message);
      case Error ->
          this.handleErroneousMessage(
              Objects.requireNonNull(gson.fromJson(message, GiraOneWebsocketResponse.class)));
    }
  }

  private void handleErroneousMessage(GiraOneWebsocketResponse giraOneCommandResponse) {
    this.logger.error(
        "{} :: {}",
        giraOneCommandResponse.getGiraMessageError(),
        giraOneCommandResponse.getResponseBody());
  }

  public void onWebSocketClosed(GiraOneWebsocketCloseCode reason) {
    logger.info(
        "WebSocket is closed with code={} and reason={}", reason.getCode(), reason.toString());
    this.connectionState.onNext(GiraOneWebsocketConnectionState.Disconnected);
  }

  public void onWebSocketError(Throwable throwable) {
    logger.error("Received WebSocketError :: ", throwable);
    this.clientExceptions.onNext(
        new GiraOneClientException(GiraOneClientException.WEBSOCKET_COMMUNICATION, throwable));
    this.connectionState.onNext(GiraOneWebsocketConnectionState.Error);
  }

  public void onWebSocketConnectionState(GiraOneWebsocketConnectionState connectionState) {
    logger.trace("onWebSocketConnectionState:: {}", connectionState);
    if (connectionState == GiraOneWebsocketConnectionState.Connected) {
      this.registerApplication();
    }
    this.connectionState.onNext(connectionState);
  }

  private void registerApplication() {
    CompletableFuture.runAsync(
        () -> {
          logger.debug("Registering GiraOneMqttBridge");
          execute(
              RegisterApplication.builder()
                  .with(RegisterApplication::setApplicationId, UUID.randomUUID().toString())
                  .with(RegisterApplication::setApplicationType, "api")
                  .build());
        });
  }
}
