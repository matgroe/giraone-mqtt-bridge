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

import com.google.gson.JsonObject;

/**
 * This interface represents a command response as received from the Gira One Sever.
 * It offers access to the raw {@link JsonObject} and the deserialized Object as well.
 *
 * @author Matthias Gröger - Initial contribution
 */

public interface GiraOneCommandResponse {

    /**
     * @return returns the raw {@link JsonObject} as received from Gira One Server.
     */
    JsonObject getResponseBody();

    /**
     * @param <T> The typed response
     * @param classOfT The class, ths response should get deserializes into
     *
     * @return The deserialized response
     */
    default <T> T getReply(Class<T> classOfT) {
        return GiraOneTypeMapperFactory.createGson().fromJson(getResponseBody(), classOfT);
    }
}
