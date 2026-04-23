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

import com.google.gson.JsonObject;
import de.matgroe.giraone.client.GiraOneCommandResponse;
import de.matgroe.giraone.client.types.GiraOneMessageError;
import de.matgroe.giraone.client.GiraOneTypeMapperFactory;

import java.util.Objects;

/**
 * This class represents a command response as received from the Gira One Sever
 * as to an received {@link GiraOneWebsocketRequest}.
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneWebsocketResponse implements GiraOneCommandResponse {
    static final String PROPERTY_ERROR = "error";

    public final JsonObject responseBody;

    public GiraOneWebsocketResponse(final JsonObject responseBody) {
        this.responseBody = responseBody;
    }

    @Override
    public JsonObject getResponseBody() {
        return this.responseBody.deepCopy();
    }

    public GiraOneWebsocketRequest getRequestServerCommand() {
        return Objects
                .requireNonNull(GiraOneTypeMapperFactory.createGson().fromJson(responseBody, GiraOneWebsocketRequest.class));
    }

    public boolean isInitiatedBy(GiraOneWebsocketRequest other) {
        return getRequestServerCommand().equals(other);
    }

    public GiraOneMessageError getGiraMessageError() {
        return Objects.requireNonNullElse(
                GiraOneTypeMapperFactory.createGson().fromJson(responseBody.get(PROPERTY_ERROR), GiraOneMessageError.class),
                new GiraOneMessageError());
    }

    public <T> T getReply(Class<T> classOfT) {
        String responseProperty = getRequestServerCommand().getCommand().getResponsePropertyName();
        if (responseProperty.isEmpty()) {
            return GiraOneTypeMapperFactory.createGson().fromJson(responseBody, classOfT);
        } else {
            return GiraOneTypeMapperFactory.createGson().fromJson(responseBody.get(responseProperty), classOfT);
        }
    }
}
