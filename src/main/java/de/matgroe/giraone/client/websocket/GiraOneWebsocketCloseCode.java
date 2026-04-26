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
