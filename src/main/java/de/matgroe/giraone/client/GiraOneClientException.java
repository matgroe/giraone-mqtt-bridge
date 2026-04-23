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
    public static final String UNEXPECTED_CONNECTION_STATE = "@text/giraone.client.unexpected-connection-state";
    public static final String CONNECT_CONFIGURATION = "@text/giraone.client.websocket.configuration";
    public static final String CONNECT_REFUSED = "@text/giraone.client.websocket.connect-refused";
    public static final String WEBSOCKET_COMMUNICATION = "@text/giraone.client.websocket.communication";
    public static final String MESSAGE_TOO_LARGE = "@text/giraone.client.websocket.message-too-large";
    public static final String DISCONNECT_FAILED = "@text/giraone.client.websocket.disconnect";
    public static final String WEBSERVICE_COMMUNICATION = "@text/giraone.client.webservice.communication";

    @Serial
    private static final long serialVersionUID = 1L;
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
        StringBuilder sb = new StringBuilder(Objects.requireNonNullElse(super.getMessage(), UNKNOWN_ERROR));

        Throwable cause = getCause();
        if (cause != null) {
            sb.append(formatMessagePlaceholder(cause.getMessage()));
        }
        Arrays.stream(this.placeholders).map(this::formatMessagePlaceholder).forEach(sb::append);
        return sb.toString();
    }
}
