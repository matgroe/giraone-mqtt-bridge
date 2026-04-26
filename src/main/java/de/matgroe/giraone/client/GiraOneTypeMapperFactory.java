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
package de.matgroe.giraone.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.matgroe.giraone.client.commands.AuthenticateSession;
import de.matgroe.giraone.client.commands.GetConfiguration;
import de.matgroe.giraone.client.commands.GetDeviceConfig;
import de.matgroe.giraone.client.commands.GetDiagnosticDeviceList;
import de.matgroe.giraone.client.commands.GetGiraOneDevices;
import de.matgroe.giraone.client.commands.GetPasswordSalt;
import de.matgroe.giraone.client.commands.GetUIConfiguration;
import de.matgroe.giraone.client.commands.GetValue;
import de.matgroe.giraone.client.commands.RegisterApplication;
import de.matgroe.giraone.client.commands.SetValue;
import de.matgroe.giraone.client.typeadapters.GiraOneChannelCollectionDeserializer;
import de.matgroe.giraone.client.typeadapters.GiraOneChannelDeserializer;
import de.matgroe.giraone.client.typeadapters.GiraOneChannelTypeDeserializer;
import de.matgroe.giraone.client.typeadapters.GiraOneChannelTypeIdDeserializer;
import de.matgroe.giraone.client.typeadapters.GiraOneCommandDeserializer;
import de.matgroe.giraone.client.typeadapters.GiraOneComponentCollectionDeserializer;
import de.matgroe.giraone.client.typeadapters.GiraOneComponentTypeDeserializer;
import de.matgroe.giraone.client.typeadapters.GiraOneDataPointDeserializer;
import de.matgroe.giraone.client.typeadapters.GiraOneEventDeserializer;
import de.matgroe.giraone.client.typeadapters.GiraOneFunctionTypeDeserializer;
import de.matgroe.giraone.client.typeadapters.GiraOneMessageTypeDeserializer;
import de.matgroe.giraone.client.typeadapters.GiraOneValueDeserializer;
import de.matgroe.giraone.client.typeadapters.GiraOneWebserviceCommandRequestSerializer;
import de.matgroe.giraone.client.typeadapters.GiraOneWebsocketResponseDeserializer;
import de.matgroe.giraone.client.types.GiraOneChannel;
import de.matgroe.giraone.client.types.GiraOneChannelCollection;
import de.matgroe.giraone.client.types.GiraOneChannelType;
import de.matgroe.giraone.client.types.GiraOneChannelTypeId;
import de.matgroe.giraone.client.types.GiraOneComponentCollection;
import de.matgroe.giraone.client.types.GiraOneComponentType;
import de.matgroe.giraone.client.types.GiraOneDataPoint;
import de.matgroe.giraone.client.types.GiraOneEvent;
import de.matgroe.giraone.client.types.GiraOneFunctionType;
import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.giraone.client.webservice.GiraOneWebserviceRequest;
import de.matgroe.giraone.client.websocket.GiraOneWebsocketResponse;
import java.util.Set;

/**
 * This class offers creation functions for a pre-configured {@link GsonBuilder} that references all
 * required {@link com.google.gson.JsonDeserializer} instances and for the concerning {@link Gson}
 * object as well. *
 *
 * @author Matthias Gröger - Initial contribution
 */
public abstract class GiraOneTypeMapperFactory {
  private static final Set<Class<?>> COMMAND_CLASSES =
      Set.of(
          AuthenticateSession.class,
          GetConfiguration.class,
          GetDeviceConfig.class,
          GetDiagnosticDeviceList.class,
          GetGiraOneDevices.class,
          GetPasswordSalt.class,
          GetUIConfiguration.class,
          GetValue.class,
          RegisterApplication.class,
          SetValue.class);

  private GiraOneTypeMapperFactory() {}

  /**
   * @return pre-configured {@link GsonBuilder}that references all required {@link
   *     com.google.gson.JsonDeserializer} instances within the giraone domain.
   */
  public static GsonBuilder createGsonBuilder() {
    GsonBuilder gsonBuilder = new GsonBuilder();

    gsonBuilder.registerTypeAdapter(GiraOneMessageType.class, new GiraOneMessageTypeDeserializer());
    gsonBuilder.registerTypeAdapter(GiraOneEvent.class, new GiraOneEventDeserializer());
    gsonBuilder.registerTypeAdapter(
        GiraOneWebsocketResponse.class, new GiraOneWebsocketResponseDeserializer());
    gsonBuilder.registerTypeAdapter(
        GiraOneWebserviceRequest.class, new GiraOneWebserviceCommandRequestSerializer());
    gsonBuilder.registerTypeAdapter(GiraOneChannel.class, new GiraOneChannelDeserializer());
    gsonBuilder.registerTypeAdapter(GiraOneDataPoint.class, new GiraOneDataPointDeserializer());
    gsonBuilder.registerTypeAdapter(
        GiraOneChannelTypeId.class, new GiraOneChannelTypeIdDeserializer());
    gsonBuilder.registerTypeAdapter(GiraOneChannelType.class, new GiraOneChannelTypeDeserializer());
    gsonBuilder.registerTypeAdapter(
        GiraOneFunctionType.class, new GiraOneFunctionTypeDeserializer());
    gsonBuilder.registerTypeAdapter(
        GiraOneCommand.class, new GiraOneCommandDeserializer(COMMAND_CLASSES));
    gsonBuilder.registerTypeAdapter(
        GiraOneComponentCollection.class, new GiraOneComponentCollectionDeserializer());
    gsonBuilder.registerTypeAdapter(
        GiraOneComponentType.class, new GiraOneComponentTypeDeserializer());
    gsonBuilder.registerTypeAdapter(
        GiraOneChannelCollection.class, new GiraOneChannelCollectionDeserializer());
    gsonBuilder.registerTypeAdapter(GiraOneValue.class, new GiraOneValueDeserializer());
    return gsonBuilder;
  }

  public static Gson createGson() {
    return createGsonBuilder().create();
  }
}
