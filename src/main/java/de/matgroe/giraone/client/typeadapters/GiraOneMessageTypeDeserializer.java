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
package de.matgroe.giraone.client.typeadapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.matgroe.giraone.client.GiraOneMessageType;
import de.matgroe.giraone.client.types.GiraOneMessageError;

import java.lang.reflect.Type;

/**
 * Deserializes a Json Element to {@link GiraOneMessageType} within context of Gson parsing.
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneMessageTypeDeserializer extends GiraOneMessageJsonTypeAdapter
        implements JsonDeserializer<GiraOneMessageType> {

    @Override
    public GiraOneMessageType deserialize(JsonElement jsonElement, Type type,
            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement != null && jsonDeserializationContext != null) {
            if (isResponse(jsonElement)) {
                return isError(getResponse(jsonElement), jsonDeserializationContext) ? GiraOneMessageType.Error
                        : GiraOneMessageType.Response;
            } else if (isEvent(jsonElement)) {
                return isError(getEvent(jsonElement), jsonDeserializationContext) ? GiraOneMessageType.Error
                        : GiraOneMessageType.Event;
            }
        }
        return GiraOneMessageType.Invalid;
    }

    private boolean isError(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        GiraOneMessageError error = jsonDeserializationContext.deserialize(jsonObject.getAsJsonObject(PROPERTY_ERROR),
                GiraOneMessageError.class);
        if (error != null) {
            return error.isErrorState();
        }
        return false;
    }
}
