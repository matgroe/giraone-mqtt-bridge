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
package de.matgroe.giraone.client.websocket;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import de.matgroe.giraone.client.GiraOneCommand;
import de.matgroe.giraone.client.GiraOneTypeMapperFactory;
import java.util.Objects;

/**
 * Defines the command message to be sent out to Gira One Server. It contains the {@link
 * GiraOneCommand}, which defines the command name and the property name within the command response
 * json. The unique commandId is built from command.name and some timestamp information to map the
 * received response to the requested server command.
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneWebsocketRequest {
  private static final String PROPERTY_COMMAND_ID = "_gdsqueryId";
  private static final String PROPERTY_COMMAND_NAME = "command";

  @SerializedName(value = "request")
  private final JsonObject request;

  public GiraOneWebsocketRequest(GiraOneCommand command) {
    request = (JsonObject) GiraOneTypeMapperFactory.createGson().toJsonTree(command);
    request.addProperty(PROPERTY_COMMAND_ID, GiraOneWebsocketSequence.next());
    request.addProperty(PROPERTY_COMMAND_NAME, command.getCommand());
  }

  public GiraOneCommand getCommand() {
    return Objects.requireNonNullElse(
        GiraOneTypeMapperFactory.createGson().fromJson(request, GiraOneCommand.class),
        new GiraOneCommand());
  }

  public Integer getCommandId() {
    return request.getAsJsonPrimitive(PROPERTY_COMMAND_ID).getAsInt();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }

    if (this == o) {
      return true;
    }

    if (!(o instanceof GiraOneWebsocketRequest that)) {
      return false;
    }
    return Objects.equals(getCommandId(), that.getCommandId())
        && Objects.equals(getCommand(), that.getCommand());
  }

  @Override
  public int hashCode() {
    return Objects.hash(request);
  }
}
