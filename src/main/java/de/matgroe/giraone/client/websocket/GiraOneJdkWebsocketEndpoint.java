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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.net.ssl.SSLContext;
import nl.altindag.ssl.SSLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The handles the raw websocket communication with the Gira One Server
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneJdkWebsocketEndpoint extends GiraOneWebsocketEndpoint {
  private final Logger logger = LoggerFactory.getLogger(GiraOneJdkWebsocketEndpoint.class);
  private WebSocket websocket;
  private HttpClient httpClient;
  private Disposable sendingQueueDisposable = Disposable.empty();

  private class WebSocketClient implements WebSocket.Listener {
    private final Logger logger = LoggerFactory.getLogger(WebSocketClient.class);

    private StringBuilder messageBuilder = new StringBuilder();
    private CompletableFuture<?> completedMessageFuture = new CompletableFuture<>();

    @Override
    public void onOpen(WebSocket webSocket) {
      logger.trace("onOpen using subprotocol {}", webSocket.getSubprotocol());
      webSocket.request(1);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
      CompletableFuture<?> returnValue = completedMessageFuture;
      messageBuilder.append(data);
      webSocket.request(1);

      if (last) {
        logger.trace("message is complete :: {}", messageBuilder.toString());
        onMessageReceived(messageBuilder.toString());
        returnValue.complete(null);
        messageBuilder = new StringBuilder();
        completedMessageFuture = new CompletableFuture<>();
      } else {
        logger.trace("onText received message part:: {}", data);
      }
      return returnValue;
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
      logger.error("Bad day! {}", webSocket.toString(), error);
      onWebsocketError(error);
    }
  }

  public GiraOneJdkWebsocketEndpoint() {
    this.onWebsocketState(GiraOneWebsocketConnectionState.Disconnected);
    this.subscribeOnConnectionState(this::subscribeOnConnectionState);
  }

  private void subscribeOnConnectionState(GiraOneWebsocketConnectionState connectionState) {
    if (connectionState == GiraOneWebsocketConnectionState.Connected) {
      sendingQueueDisposable =
          this.subscribeOnSendingQueue(
              this::processSendingQueueItem, this::handleSendingQueueError);
    } else {
      sendingQueueDisposable.dispose();
    }
  }

  private void handleSendingQueueError(Throwable throwable) {
    logger.error("handleSendingQueueError :: {}", throwable.getMessage(), throwable);
    try {
      this.websocket.abort();
      this.httpClient.close();
      this.onWebsocketState(GiraOneWebsocketConnectionState.Disconnected);
    } catch (Exception e) {
      logger.error(
          "handleSendingQueueError :: exception terminating websocket :: {}",
          throwable.getMessage(),
          throwable);
    }
  }

  private void processSendingQueueItem(String item) {
    logger.trace("processSendingQueueItem {}", item);
    try {
      this.websocket.sendText(item, true).join().request(1);
    } catch (Exception e) {
      onWebsocketError(e);
    }
  }

  private SSLContext createSSLContext() {
    return SSLFactory.builder()
        .withUnsafeTrustMaterial()
        .withUnsafeHostnameVerifier()
        .build()
        .getSslContext();
  }

  @Override
  void connectTo(URI endpoint) throws GiraOneWebsocketException {
    onWebsocketState(GiraOneWebsocketConnectionState.Connecting);
    try {
      this.httpClient = HttpClient.newBuilder().sslContext(createSSLContext()).build();
      this.websocket =
          this.httpClient
              .newWebSocketBuilder()
              .buildAsync(endpoint, new WebSocketClient())
              .get(60, TimeUnit.SECONDS);
      onWebsocketState(GiraOneWebsocketConnectionState.Connected);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      throw new GiraOneWebsocketException(e);
    }
  }

  @Override
  void disconnect(GiraOneWebsocketCloseCode reason) throws GiraOneWebsocketException {
    if (this.websocket != null) {
      this.websocket.sendClose(reason.getCode(), reason.name());
      this.httpClient.close();
    }
    this.sendingQueueDisposable.dispose();
    onWebsocketState(GiraOneWebsocketConnectionState.Disconnected);
  }
}
