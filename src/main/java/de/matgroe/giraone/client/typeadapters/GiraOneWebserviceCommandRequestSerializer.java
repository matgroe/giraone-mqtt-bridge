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
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.matgroe.giraone.client.webservice.GiraOneWebserviceRequest;
import java.lang.reflect.Type;

/**
 * Serializes a {@link GiraOneWebserviceRequest} into it's Json Representation.
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneWebserviceCommandRequestSerializer
    implements JsonSerializer<GiraOneWebserviceRequest> {
  private static final String PROPERTY_COMMAND_NAME = "command";
  private static final String PROPERTY_KEEP_ALIVE = "keepAlive";
  private static final String PROPERTY_DATA = "data";

  @Override
  public JsonElement serialize(
      GiraOneWebserviceRequest serverCommand,
      Type type,
      JsonSerializationContext jsonSerializationContext) {
    JsonObject json = new JsonObject();
    assert serverCommand != null;

    json.addProperty(PROPERTY_COMMAND_NAME, serverCommand.getCommand().getCommand());
    json.addProperty(PROPERTY_KEEP_ALIVE, true);
    if (jsonSerializationContext != null) {
      json.add(PROPERTY_DATA, jsonSerializationContext.serialize(serverCommand.getCommand()));
    }
    return json;
  }
}
