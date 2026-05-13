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

import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_CHANNEL_TYPE;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_CHANNEL_TYPE_ID;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_CHANNEL_URN;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_CHANNEL_VIEW_URN;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_DATAPOINTS;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_DATA_POINTS_CC;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_FUNCTION_TYPE;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_LOCATION;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_NAME;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_PARAMETER;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_URN;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import de.matgroe.giraone.client.types.GiraOneChannel;
import de.matgroe.giraone.client.types.GiraOneChannelParameter;
import de.matgroe.giraone.client.types.GiraOneChannelType;
import de.matgroe.giraone.client.types.GiraOneChannelTypeId;
import de.matgroe.giraone.client.types.GiraOneDataPoint;
import de.matgroe.giraone.client.types.GiraOneFunctionType;
import de.matgroe.giraone.client.types.GiraOneURN;
import java.lang.reflect.Type;
import java.util.Arrays;
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
          case PROPERTY_URN, PROPERTY_CHANNEL_URN, PROPERTY_CHANNEL_VIEW_URN:
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
          case PROPERTY_PARAMETER:
            Arrays.stream(
                    (GiraOneChannelParameter[])
                        jsonDeserializationContext.deserialize(
                            entry.getValue(), GiraOneChannelParameter[].class))
                .toList()
                .forEach(channel::addParameter);

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
              elem -> {
                GiraOneDataPoint dataPoint =
                    jsonDeserializationContext.deserialize(elem, GiraOneDataPoint.class);
                if (!dataPoint.getUrn().equals(GiraOneURN.INVALID)) {
                  channel.addDataPoint(dataPoint);
                }
              });
    }
  }
}
