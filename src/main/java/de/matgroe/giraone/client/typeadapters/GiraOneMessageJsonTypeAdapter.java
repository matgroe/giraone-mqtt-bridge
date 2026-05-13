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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Base class offers some functionalities for deserializing reseived messages from Gira One Server.
 * *
 *
 * @author Matthias Gröger - Initial contribution
 */
class GiraOneMessageJsonTypeAdapter {
  protected static final String PROPERTY_RESPONSE = "response";
  protected static final String PROPERTY_EVENT = "event";
  protected static final String PROPERTY_ERROR = "error";

  boolean isResponse(JsonElement jsonElement) {
    return isResponse(jsonElement.getAsJsonObject());
  }

  boolean isResponse(JsonObject jsonObject) {
    return jsonObject.has(PROPERTY_RESPONSE);
  }

  JsonObject getResponse(JsonElement jsonElement) {
    return jsonElement.getAsJsonObject().get(PROPERTY_RESPONSE).getAsJsonObject();
  }

  boolean isEvent(JsonElement jsonElement) {
    return isEvent(jsonElement.getAsJsonObject());
  }

  boolean isEvent(JsonObject jsonObject) {
    return jsonObject.has(PROPERTY_EVENT);
  }

  JsonObject getEvent(JsonElement jsonElement) {
    return jsonElement.getAsJsonObject().get(PROPERTY_EVENT).getAsJsonObject();
  }
}
