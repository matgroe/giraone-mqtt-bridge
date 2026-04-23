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
import de.matgroe.giraone.client.types.GiraOneDataPoint;
import de.matgroe.giraone.client.types.GiraOneURN;

import java.lang.reflect.Type;

/**
 * Deserializes a Json Element to {@link GiraOneDataPoint} within context of Gson parsing.
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneDataPointDeserializer extends GiraOneMessageJsonTypeAdapter
        implements JsonDeserializer<GiraOneDataPoint> {

    @Override
    public GiraOneDataPoint deserialize(JsonElement jsonElement, Type type,
             JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement != null && jsonElement.isJsonObject()) {
            try {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                if (jsonObject.has(GiraOneJsonPropertyNames.PROPERTY_URN)) {
                    return new GiraOneDataPoint(jsonObject.get(GiraOneJsonPropertyNames.PROPERTY_URN).getAsString());
                }
                return new GiraOneDataPoint(GiraOneURN.INVALID);
            } catch (IllegalArgumentException e) {
                throw new JsonParseException("Cannot parse JsonElement as GiraOneDataPoint.", e);
            }
        }
        throw new JsonParseException("Cannot parse empty JsonElement as GiraOneDataPoint.");
    }
}
