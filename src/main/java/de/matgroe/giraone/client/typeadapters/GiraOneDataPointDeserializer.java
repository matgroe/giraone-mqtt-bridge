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
  public GiraOneDataPoint deserialize(
      JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {
    if (jsonElement != null && jsonElement.isJsonObject()) {
      try {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (jsonObject.has(GiraOneJsonPropertyNames.PROPERTY_URN)) {
          GiraOneDataPoint dp =
              new GiraOneDataPoint(
                  jsonObject.get(GiraOneJsonPropertyNames.PROPERTY_URN).getAsString());
          if (jsonObject.has(GiraOneJsonPropertyNames.PROPERTY_ID)) {
            dp.setId(jsonObject.get(GiraOneJsonPropertyNames.PROPERTY_ID).getAsInt());
          }
          return dp;
        }
        return new GiraOneDataPoint(GiraOneURN.INVALID);
      } catch (IllegalArgumentException e) {
        throw new JsonParseException("Cannot parse JsonElement as GiraOneDataPoint.", e);
      }
    }
    throw new JsonParseException("Cannot parse empty JsonElement as GiraOneDataPoint.");
  }
}
