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
      if (jsonObject.has("urn") && jsonObject.has("value")) {
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
