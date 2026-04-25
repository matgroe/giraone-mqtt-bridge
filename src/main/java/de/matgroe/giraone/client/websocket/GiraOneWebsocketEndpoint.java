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

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/**
 * The abstract class {@link GiraOneWebsocketEndpoint} ... TODO
 *
 * @author Matthias Groeger - Initial contribution
 */
abstract class GiraOneWebsocketEndpoint {
    private final Logger logger = LoggerFactory.getLogger(GiraOneWebsocketEndpoint.class);

    private final Subject<GiraOneWebsocketConnectionState> endpointConnectionState = PublishSubject.create();
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
    };

    /**
     * The derived class notifies about received messages by using this method.
     * 
     * @param message The received message
     */
    protected void onMessageReceived(final String message) {
        logger.trace("received message :: {}", message);
        this.receiverQueue.onNext(message);
    };

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
     *
     * @return A {@link Disposable}
     */
    protected final Disposable subscribeOnSendingQueue(Consumer<String> messageConsumer) {
        return this.subscribeOnSendingQueue(messageConsumer, this::defaultErrorHandler);
    }

    /**
     * Register a Consumer<String> for getting received messages.
     *
     * @param messageConsumer The Consumer Callback.
     *
     * @return A {@link Disposable}
     */
    protected final Disposable subscribeOnSendingQueue(Consumer<String> messageConsumer,
            Consumer<? super Throwable> errorConsumer) {
        return this.senderQueue.subscribe(messageConsumer, errorConsumer);
    }

    /**
     * Register a Consumer<String> for getting received messages.
     *
     * @param messageConsumer The Consumer Callback.
     *
     * @return A {@link Disposable}
     */
    public final Disposable subscribeOnMessages(Consumer<String> messageConsumer) {
        return this.subscribeOnMessages(messageConsumer, this::defaultErrorHandler);
    }

    /**
     * The default error handler for
     *
     * @param throwable The Consumer Callback.
     *
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
     *
     * @return A {@link Disposable}
     */
    public final Disposable subscribeOnMessages(Consumer<String> messageConsumer,
            Consumer<? super Throwable> errorConsumer) {
        return this.receiverQueue.subscribe(messageConsumer, errorConsumer);
    }

    /**
     * Register a Consumer<GiraOneWebsocketConnectionState> for
     * getting updates on the websocket connection state.
     *
     * @param stateConsumer The Consumer Callback.
     *
     * @return A {@link Disposable}
     */
    public final Disposable subscribeOnConnectionState(Consumer<GiraOneWebsocketConnectionState> stateConsumer) {
        return this.endpointConnectionState.distinctUntilChanged().subscribe(stateConsumer);
    }

    /**
     * Register a Consumer<closeReasonConsumer> for getting
     * the reason about a closed websocket.
     *
     * @param closeReasonConsumer The Consumer Callback.
     *
     * @return A {@link Disposable}
     */
    public final Disposable subscribeOnWebsocketCloseReason(Consumer<GiraOneWebsocketCloseCode> closeReasonConsumer) {
        return this.websocketCloseReason.subscribe(closeReasonConsumer);
    }

    /**
     * Register a Consumer<Throwable> for getting websocket errors.
     *
     * @param throwableConsumer The Consumer Callback.
     *
     * @return A {@link Disposable}
     */
    public final Disposable subscribeOnThrowable(Consumer<Throwable> throwableConsumer) {
        return this.queueThrowable.subscribe(throwableConsumer);
    }
}
