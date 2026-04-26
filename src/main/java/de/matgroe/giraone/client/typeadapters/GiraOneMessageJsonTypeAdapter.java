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
