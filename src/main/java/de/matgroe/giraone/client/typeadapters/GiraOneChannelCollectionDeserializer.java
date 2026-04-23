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
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_CHANNEL_VIEW_ID;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_CHANNEL_VIEW_URN;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_CONTENT;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_LOCATION;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_MAINTYPE;
import static de.matgroe.giraone.client.typeadapters.GiraOneJsonPropertyNames.PROPERTY_NAME;
import de.matgroe.giraone.client.types.GiraOneChannel;
import de.matgroe.giraone.client.types.GiraOneChannelCollection;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Stream;

/**
 * Uses the Gson parsing to deserializes the response of
 * {@link de.matgroe.giraone.client.GiraOneCommand} command
 * {@link GetDiagnosticDeviceList} into
 * {@link GiraOneChannelCollection}.
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneChannelCollectionDeserializer implements JsonDeserializer<GiraOneChannelCollection> {

    @Override
    public GiraOneChannelCollection deserialize(JsonElement jsonElement, Type type,
            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        assert jsonElement != null;
        assert jsonDeserializationContext != null;

        GiraOneChannelCollection channelCollection = new GiraOneChannelCollection();
        if (jsonElement.isJsonArray()) {
            // extract the channel -> location mapping from json
            List<JsonObject> channelLocations = jsonElement.getAsJsonArray().asList().stream()
                    .filter(JsonElement::isJsonObject).map(JsonElement::getAsJsonObject)
                    .filter(e -> e.has(PROPERTY_MAINTYPE) && "Root".equals(e.get(PROPERTY_MAINTYPE).getAsString()))
                    .findFirst().stream().map(this::streamChannelLocationInformation).map(Stream::toList)
                    .flatMap(this::makeFlat).toList();

            // and convert into GiraOneChannel objects
            jsonElement.getAsJsonArray().asList().stream().filter(JsonElement::isJsonObject)
                    .map(JsonElement::getAsJsonObject).filter(e -> e.has(PROPERTY_CHANNEL_VIEW_URN))
                    .map(e -> enrichLocation(jsonDeserializationContext, e, channelLocations))
                    .map(e -> createGiraOneChannel(jsonDeserializationContext, e)).forEach(channelCollection::add);
        }
        return channelCollection;
    }

    private Stream<JsonObject> makeFlat(List<JsonElement> jsonElements) {
        return jsonElements.stream().map(JsonElement::getAsJsonObject);
    }

    private JsonObject enrichLocation(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject,
            List<JsonObject> channelLocations) {
        JsonElement viewId = jsonObject.get(PROPERTY_CHANNEL_VIEW_ID);
        if (viewId != null) {
            channelLocations.stream().filter(f -> f.has(PROPERTY_CHANNEL_VIEW_ID))
                    .filter(f -> f.get(PROPERTY_CHANNEL_VIEW_ID).getAsString().equals(viewId.getAsString())).findFirst()
                    .ifPresent(object -> jsonObject.add(PROPERTY_LOCATION, object.get(PROPERTY_LOCATION)));
        }
        return jsonObject;
    }

    private GiraOneChannel createGiraOneChannel(JsonDeserializationContext jsonDeserializationContext,
            JsonElement jsonElement) {
        return jsonDeserializationContext.deserialize(jsonElement, GiraOneChannel.class);
    }

    private Stream<JsonElement> streamChannelLocationInformation(JsonObject jsonObject) {
        Stream<JsonElement> jsonElements = Stream.empty();
        if (jsonObject.has(PROPERTY_CONTENT)) {
            jsonElements = Stream.concat(jsonElements,
                    streamChannelLocationInformation(jsonObject, jsonObject.getAsJsonArray(PROPERTY_CONTENT)));
        }
        return jsonElements;
    }

    private Stream<JsonElement> streamChannelLocationInformation(JsonObject jsonParentObject, JsonArray jsonArray) {
        Stream<JsonElement> jsonElements = Stream.empty();
        for (JsonElement e : jsonArray.asList()) {
            if (e.isJsonObject()) {
                JsonObject jsonObject = e.getAsJsonObject();
                if (jsonObject.has(PROPERTY_MAINTYPE)) {
                    jsonElements = Stream.concat(jsonElements, streamChannelLocationInformation(jsonObject));
                } else if (jsonObject.has(PROPERTY_CHANNEL_VIEW_ID)) {
                    if (jsonParentObject.has(PROPERTY_NAME)) {
                        jsonObject.addProperty(PROPERTY_LOCATION, jsonParentObject.get(PROPERTY_NAME).getAsString());
                    }
                    jsonElements = Stream.concat(jsonElements, Stream.of(jsonObject));
                }
            }
        }
        return jsonElements;
    }
}
