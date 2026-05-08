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
package de.matgroe.giraone.client;

import java.io.Serial;
import java.util.Arrays;
import java.util.Objects;

/**
 * Generic Exception with Gira One Domain.
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneClientException extends RuntimeException {
  public static final String UNKNOWN_ERROR = "@text/giraone.client.unkown-error";
  public static final String UNEXPECTED_CONNECTION_STATE =
      "@text/giraone.client.unexpected-connection-state";
  public static final String CONNECT_CONFIGURATION = "@text/giraone.client.websocket.configuration";
  public static final String CONNECT_REFUSED = "@text/giraone.client.websocket.connect-refused";
  public static final String WEBSOCKET_COMMUNICATION =
      "@text/giraone.client.websocket.communication";
  public static final String MESSAGE_TOO_LARGE = "@text/giraone.client.websocket.message-too-large";
  public static final String DISCONNECT_FAILED = "@text/giraone.client.websocket.disconnect";
  public static final String WEBSERVICE_COMMUNICATION =
      "@text/giraone.client.webservice.communication";

  @Serial private static final long serialVersionUID = 1L;
  private final String[] placeholders;

  public GiraOneClientException(String message) {
    this(message, (Throwable) null);
  }

  public GiraOneClientException(String message, Throwable t) {
    super(message, t);
    this.placeholders = new String[0];
  }

  public GiraOneClientException(String message, String... placeholders) {
    super(message);
    this.placeholders = placeholders;
  }

  private String formatMessagePlaceholder(String message) {
    return String.format(" [\"%s\"]", message);
  }

  @Override
  public String getMessage() {
    StringBuilder sb =
        new StringBuilder(Objects.requireNonNullElse(super.getMessage(), UNKNOWN_ERROR));

    Throwable cause = getCause();
    if (cause != null) {
      sb.append(formatMessagePlaceholder(cause.getMessage()));
    }
    Arrays.stream(this.placeholders).map(this::formatMessagePlaceholder).forEach(sb::append);
    return sb.toString();
  }
}
