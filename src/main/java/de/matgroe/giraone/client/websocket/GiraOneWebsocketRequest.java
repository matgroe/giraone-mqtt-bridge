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
