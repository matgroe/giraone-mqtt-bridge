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

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_CHANNELS;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_CHANNEL_TYPE;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_CHANNEL_TYPE_ID;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_COMPONENTS;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_DATAPOINTS;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_FUNCTION_TYPE;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_LOCATION;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_NAME;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_SUBLOCATIONS;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_TYPE;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_URN;
import de.matgroe.giraone.client.types.GiraOneChannelType;
import de.matgroe.giraone.client.types.GiraOneChannelTypeId;
import de.matgroe.giraone.client.types.GiraOneComponent;
import de.matgroe.giraone.client.types.GiraOneComponentCollection;
import de.matgroe.giraone.client.types.GiraOneComponentType;
import de.matgroe.giraone.client.types.GiraOneFunctionType;
import de.matgroe.giraone.client.types.GiraOneURN;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Deserializes a Json Element to {@link GiraOneComponentCollection} within context of Gson parsing.
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneComponentCollectionDeserializer implements JsonDeserializer<GiraOneComponentCollection> {

    @Override
    public GiraOneComponentCollection deserialize(JsonElement jsonElement, Type type,
            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        GiraOneComponentCollection diagDevices = new GiraOneComponentCollection();
        if (jsonDeserializationContext != null && jsonElement != null && jsonElement.isJsonObject()) {
            streamJsonObjectOfGiraOneComponents(jsonElement.getAsJsonObject()).map(JsonElement::getAsJsonObject)
                    .map(e -> this.createGiraOneComponent(jsonDeserializationContext, e)).forEach(diagDevices::add);
        }
        return diagDevices;
    }

    private GiraOneComponent createGiraOneComponent(JsonDeserializationContext jsonDeserializationContext,
            JsonObject jsonObject) {
        GiraOneComponentType cmpType = jsonDeserializationContext.deserialize(jsonObject.get(PROPERTY_URN),
                GiraOneComponentType.class);
        if (cmpType == GiraOneComponentType.KnxButton) {
            addKnxButtonProperties(jsonObject);
        }
        jsonObject.add(PROPERTY_TYPE, jsonObject.get(PROPERTY_URN));
        return jsonDeserializationContext.deserialize(jsonObject, GiraOneComponent.class);
    }

    private void addKnxButtonProperties(JsonObject jsonObject) {
        for (JsonElement channelElement : jsonObject.getAsJsonArray(PROPERTY_CHANNELS)) {
            JsonObject channelObject = channelElement.getAsJsonObject();
            channelObject.addProperty(PROPERTY_FUNCTION_TYPE, GiraOneFunctionType.Trigger.getName());
            channelObject.addProperty(PROPERTY_CHANNEL_TYPE, GiraOneChannelType.Trigger.getName());
            channelObject.addProperty(PROPERTY_CHANNEL_TYPE_ID, GiraOneChannelTypeId.Button.getName());

            JsonArray datapoints = channelObject.getAsJsonArray(PROPERTY_DATAPOINTS);
            channelObject.addProperty(PROPERTY_URN, buildDatapointDeviceUrn(datapoints));

            String channelName = String.format(("%s, %s"), jsonObject.getAsJsonPrimitive(PROPERTY_NAME).getAsString(),
                    channelObject.getAsJsonPrimitive(PROPERTY_NAME).getAsString());
            channelObject.addProperty(PROPERTY_NAME, channelName);
        }
    }

    private String buildDatapointDeviceUrn(JsonArray datapoints) {
        return datapoints.asList().stream().map(JsonElement::getAsJsonObject)
                .map(e -> e.get(PROPERTY_URN).getAsString()).map(GiraOneURN::of).map(GiraOneURN::getParent).distinct()
                .findFirst().orElse(GiraOneURN.INVALID).toString();
    }

    private Stream<JsonElement> streamJsonObjectOfGiraOneComponents(JsonObject jsonObject) {
        Stream<JsonElement> jsonElements = Stream.empty();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            switch (entry.getKey()) {
                case PROPERTY_COMPONENTS:
                    jsonElements = Stream.concat(jsonElements,
                            jsonObject.getAsJsonArray(entry.getKey()).asList().stream());
                case PROPERTY_SUBLOCATIONS:
                    jsonElements = Stream.concat(jsonElements,
                            streamJsonArrayOfSubLocation(jsonObject, entry.getValue().getAsJsonArray()));
                default:
                    break;
            }
        }
        return jsonElements;
    }

    private Stream<JsonElement> streamJsonArrayOfSubLocation(JsonObject parent, JsonArray jsonArray) {
        Stream<JsonElement> jsonElements = Stream.empty();
        for (JsonElement e : jsonArray.asList()) {
            if (e.isJsonObject()) {
                e.getAsJsonObject().add(PROPERTY_LOCATION, parent.get(PROPERTY_NAME));
                jsonElements = Stream.concat(jsonElements, streamJsonObjectOfGiraOneComponents(e.getAsJsonObject()));
            }
        }
        return jsonElements;
    }
}
