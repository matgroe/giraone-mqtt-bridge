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

import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_CHANNEL_TYPE;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_CHANNEL_TYPE_ID;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_CHANNEL_VIEW_URN;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_DATAPOINTS;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_DATA_POINTS_CC;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_FUNCTION_TYPE;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_LOCATION;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_NAME;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_URN;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import de.matgroe.giraone.client.types.GiraOneChannel;
import de.matgroe.giraone.client.types.GiraOneChannelType;
import de.matgroe.giraone.client.types.GiraOneChannelTypeId;
import de.matgroe.giraone.client.types.GiraOneDataPoint;
import de.matgroe.giraone.client.types.GiraOneFunctionType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Deserializes a Json Element to {@link GiraOneChannel} within context of Gson parsing.
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneChannelDeserializer extends GiraOneMessageJsonTypeAdapter
    implements JsonDeserializer<GiraOneChannel> {

  @Override
  public GiraOneChannel deserialize(
      JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {
    if (jsonElement != null && jsonElement.isJsonObject()) {
      assert jsonDeserializationContext != null;

      GiraOneChannel channel = new GiraOneChannel();
      for (Map.Entry<String, JsonElement> entry : jsonElement.getAsJsonObject().entrySet()) {
        switch (entry.getKey()) {
          case PROPERTY_LOCATION:
            channel.setLocation(entry.getValue().getAsString());
            break;
          case PROPERTY_NAME:
            channel.setName(entry.getValue().getAsString());
            break;
          case PROPERTY_CHANNEL_VIEW_URN, PROPERTY_URN:
            channel.setUrn(entry.getValue().getAsString());
            break;
          case PROPERTY_FUNCTION_TYPE:
            channel.setFunctionType(
                jsonDeserializationContext.deserialize(
                    entry.getValue(), GiraOneFunctionType.class));
            break;
          case PROPERTY_CHANNEL_TYPE:
            channel.setChannelType(
                jsonDeserializationContext.deserialize(entry.getValue(), GiraOneChannelType.class));
            break;
          case PROPERTY_CHANNEL_TYPE_ID:
            channel.setChannelTypeId(
                jsonDeserializationContext.deserialize(
                    entry.getValue(), GiraOneChannelTypeId.class));
            break;
          case PROPERTY_DATAPOINTS, PROPERTY_DATA_POINTS_CC:
            addDatapoints(channel, jsonDeserializationContext, entry.getValue());
            break;
          default:
            break;
        }
      }
      return channel;
    }
    throw new JsonParseException("Cannot parse JsonElement as GiraOneChannel.");
  }

  private void addDatapoints(
      GiraOneChannel channel,
      JsonDeserializationContext jsonDeserializationContext,
      JsonElement jsonElement) {
    if (jsonDeserializationContext != null && jsonElement.isJsonArray()) {
      jsonElement
          .getAsJsonArray()
          .asList()
          .forEach(
              elem ->
                  channel.addDataPoint(
                      jsonDeserializationContext.deserialize(elem, GiraOneDataPoint.class)));
    }
  }
}
