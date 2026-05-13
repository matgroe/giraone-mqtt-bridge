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
package de.matgroe.giraone.client.websocket;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The abstract class {@link GiraOneWebsocketEndpoint} ... TODO
 *
 * @author Matthias Groeger - Initial contribution
 */
abstract class GiraOneWebsocketEndpoint {
  private final Logger logger = LoggerFactory.getLogger(GiraOneWebsocketEndpoint.class);

  private final Subject<GiraOneWebsocketConnectionState> endpointConnectionState =
      PublishSubject.create();
  private final Subject<Throwable> queueThrowable = PublishSubject.create();
  private final Subject<String> receiverQueue = PublishSubject.create();
  private final Subject<String> senderQueue = PublishSubject.create();
  private final Subject<GiraOneWebsocketCloseCode> websocketCloseReason = PublishSubject.create();

  abstract void connectTo(URI endpoint) throws GiraOneWebsocketException;

  abstract void disconnect(GiraOneWebsocketCloseCode reason) throws GiraOneWebsocketException;

  /**
   * Enqueues the given message to be sent via websocket
   *
   * @param message The message to send
   */
  public void send(final String message) {
    logger.trace("enqueue message :: {}", message);
    this.senderQueue.onNext(message);
  }
  ;

  /**
   * The derived class notifies about received messages by using this method.
   *
   * @param message The received message
   */
  protected void onMessageReceived(final String message) {
    logger.trace("received message :: {}", message);
    this.receiverQueue.onNext(message);
  }
  ;

  protected void onWebsocketState(GiraOneWebsocketConnectionState state) {
    this.endpointConnectionState.onNext(state);
  }

  protected void onWebsocketClosed(GiraOneWebsocketCloseCode closeCode) {
    onWebsocketState(GiraOneWebsocketConnectionState.Disconnected);
    this.websocketCloseReason.onNext(closeCode);
  }

  protected void onWebsocketError(Throwable throwable) {
    onWebsocketState(GiraOneWebsocketConnectionState.Error);
    this.queueThrowable.onNext(throwable);
  }

  /**
   * Register a Consumer<String> for getting received messages.
   *
   * @param messageConsumer The Consumer Callback.
   * @return A {@link Disposable}
   */
  protected final Disposable subscribeOnSendingQueue(Consumer<String> messageConsumer) {
    return this.subscribeOnSendingQueue(messageConsumer, this::defaultErrorHandler);
  }

  /**
   * Register a Consumer<String> for getting received messages.
   *
   * @param messageConsumer The Consumer Callback.
   * @return A {@link Disposable}
   */
  protected final Disposable subscribeOnSendingQueue(
      Consumer<String> messageConsumer, Consumer<? super Throwable> errorConsumer) {
    return this.senderQueue.subscribe(messageConsumer, errorConsumer);
  }

  /**
   * Register a Consumer<String> for getting received messages.
   *
   * @param messageConsumer The Consumer Callback.
   * @return A {@link Disposable}
   */
  public final Disposable subscribeOnMessages(Consumer<String> messageConsumer) {
    return this.subscribeOnMessages(messageConsumer, this::defaultErrorHandler);
  }

  /**
   * The default error handler for
   *
   * @param throwable The Consumer Callback.
   * @return A {@link Disposable}
   */
  protected void defaultErrorHandler(Throwable throwable) {
    logger.error("defaultErrorHandler :: {}", throwable.getMessage(), throwable);
  }

  /**
   * Register a Consumer<String> for getting received messages.
   *
   * @param messageConsumer The message consumer Callback.
   * @param errorConsumer The error Consumer callback
   * @return A {@link Disposable}
   */
  public final Disposable subscribeOnMessages(
      Consumer<String> messageConsumer, Consumer<? super Throwable> errorConsumer) {
    return this.receiverQueue.subscribe(messageConsumer, errorConsumer);
  }

  /**
   * Register a Consumer<GiraOneWebsocketConnectionState> for getting updates on the websocket
   * connection state.
   *
   * @param stateConsumer The Consumer Callback.
   * @return A {@link Disposable}
   */
  public final Disposable subscribeOnConnectionState(
      Consumer<GiraOneWebsocketConnectionState> stateConsumer) {
    return this.endpointConnectionState.distinctUntilChanged().subscribe(stateConsumer);
  }

  /**
   * Register a Consumer<closeReasonConsumer> for getting the reason about a closed websocket.
   *
   * @param closeReasonConsumer The Consumer Callback.
   * @return A {@link Disposable}
   */
  public final Disposable subscribeOnWebsocketCloseReason(
      Consumer<GiraOneWebsocketCloseCode> closeReasonConsumer) {
    return this.websocketCloseReason.subscribe(closeReasonConsumer);
  }

  /**
   * Register a Consumer<Throwable> for getting websocket errors.
   *
   * @param throwableConsumer The Consumer Callback.
   * @return A {@link Disposable}
   */
  public final Disposable subscribeOnThrowable(Consumer<Throwable> throwableConsumer) {
    return this.queueThrowable.subscribe(throwableConsumer);
  }
}
