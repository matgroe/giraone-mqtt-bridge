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
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.matgroe.giraone.client.websocket.GiraOneWebsocketRequest;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Serializes a {@link GiraOneWebsocketRequest} into it's Json Representation.
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneWebsocketRequestSerializer implements JsonSerializer<GiraOneWebsocketRequest> {
  static final String PROPERTY_REQUEST = "request";

  @Override
  public JsonElement serialize(
      GiraOneWebsocketRequest serverCommand,
      Type type,
      JsonSerializationContext jsonSerializationContext) {
    Gson gson = new Gson();
    Map<String, GiraOneWebsocketRequest> wrapper = new HashMap<>();
    if (serverCommand != null) {
      wrapper.put(PROPERTY_REQUEST, serverCommand);
    }
    return gson.toJsonTree(serverCommand);
  }
}
