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

import java.util.Arrays;

/**
 * Websocket Close Codes as defined in
 * https://www.iana.org/assignments/websocket/websocket.xhtml#close-code-number
 *
 * @author Matthias Gröger - Initial contribution
 */
public enum GiraOneWebsocketCloseCode {
  NORMAL_CLOSURE(1000),
  GOING_AWAY(1001),
  PROTOCOL_ERROR(1002),
  CANNOT_ACCEPT(1003),
  RESERVED(1004),
  NO_STATUS_CODE(1005),
  CLOSED_ABNORMALLY(1006),
  NOT_CONSISTENT(1007),
  VIOLATED_POLICY(1008),
  TOO_BIG(1009),
  NO_EXTENSION(1010),
  UNEXPECTED_CONDITION(1011),
  SERVICE_RESTART(1012),
  TRY_AGAIN_LATER(1013),
  TLS_HANDSHAKE_FAILURE(1015);

  private final int code;

  private GiraOneWebsocketCloseCode(int code) {
    this.code = code;
  }

  public int getCode() {
    return this.code;
  }

  public static GiraOneWebsocketCloseCode fromCode(int value) throws IllegalArgumentException {
    return Arrays.stream(GiraOneWebsocketCloseCode.values())
        .filter(f -> value == f.getCode())
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}
