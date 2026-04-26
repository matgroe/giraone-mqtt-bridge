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
import de.matgroe.giraone.client.types.GiraOneValue;
import java.lang.reflect.Type;

/**
 * Deserializes a Json Element to {@link GiraOneValue} within context of Gson parsing.
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneValueDeserializer extends GiraOneMessageJsonTypeAdapter
    implements JsonDeserializer<GiraOneValue> {

  private JsonObject getValueAsJsonObject(JsonElement jsonElement) {
    if (jsonElement != null && jsonElement.isJsonObject()) {
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      if (jsonObject.has("valueState")
          && "Value".equals(jsonObject.get("valueState").getAsString())) {
        return jsonObject;
      }
    }
    return null;
  }

  @Override
  public GiraOneValue deserialize(
      JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {
    JsonObject jsonObject = getValueAsJsonObject(jsonElement);
    if (jsonObject != null) {
      return new GiraOneValue(
          jsonObject.get("urn").getAsString(), jsonObject.get("value").getAsString());
    }
    throw new JsonParseException("Cannot parse JsonElement as GiraOneValue.");
  }
}
