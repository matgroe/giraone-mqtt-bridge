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

import java.io.Serial;

/**
 * Generic Exception with Gira One Domain.
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneWebsocketException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public GiraOneWebsocketException(String message) {
        this(message, (Throwable) null);
    }

    public GiraOneWebsocketException(String message, Throwable t) {
        super(message, t);
    }

    public GiraOneWebsocketException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
