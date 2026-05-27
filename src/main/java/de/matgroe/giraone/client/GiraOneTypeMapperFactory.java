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
package de.matgroe.giraone.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.matgroe.giraone.client.commands.AuthenticateSession;
import de.matgroe.giraone.client.commands.GetConfiguration;
import de.matgroe.giraone.client.commands.GetDeviceConfig;
import de.matgroe.giraone.client.commands.GetDiagnosticDeviceList;
import de.matgroe.giraone.client.commands.GetGiraOneDevices;
import de.matgroe.giraone.client.commands.GetPasswordSalt;
import de.matgroe.giraone.client.commands.GetProcessView;
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
          GetProcessView.class,
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
