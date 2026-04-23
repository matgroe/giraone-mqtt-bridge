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

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.matgroe.giraone.client.types.GiraOneEvent;

import java.lang.reflect.Type;

/**
 * Deserializes a Json Element to {@link GiraOneEvent} within context of Gson parsing.
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneEventDeserializer extends GiraOneMessageJsonTypeAdapter implements JsonDeserializer<GiraOneEvent> {

    private JsonObject getValue(JsonElement jsonElement) {
        JsonObject event = getEvent(jsonElement);
        if (event.has("value")) {
            return event.getAsJsonObject("value");
        }
        return new JsonObject();
    }

    @Override
    public GiraOneEvent deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement != null && isEvent(jsonElement)) {
            JsonObject value = getValue(jsonElement);
            return new Gson().fromJson(value, GiraOneEvent.class);
        }
        throw new JsonParseException("Cannot parse JsonElement as GiraOneEvent.");
    }
}
