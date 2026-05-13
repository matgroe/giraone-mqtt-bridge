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
package de.matgroe.giraone;

import static de.matgroe.Constants.LOCATION_BRIDGE;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.gson.Gson;
import de.matgroe.giraone.client.GiraOneTypeMapperFactory;
import de.matgroe.giraone.client.commands.GetUIConfiguration;
import de.matgroe.giraone.client.types.GiraOneChannel;
import de.matgroe.giraone.client.types.GiraOneChannelCollection;
import de.matgroe.giraone.client.types.GiraOneChannelType;
import de.matgroe.giraone.client.types.GiraOneChannelTypeId;
import de.matgroe.giraone.client.types.GiraOneComponentCollection;
import de.matgroe.giraone.client.types.GiraOneComponentType;
import de.matgroe.giraone.client.types.GiraOneDataPoint;
import de.matgroe.giraone.client.types.GiraOneDeviceConfiguration;
import de.matgroe.giraone.client.types.GiraOneFunctionType;
import de.matgroe.giraone.client.types.GiraOneProject;
import de.matgroe.giraone.client.types.GiraOneURN;
import de.matgroe.giraone.client.webservice.GiraOneWebserviceResponse;
import de.matgroe.giraone.client.websocket.GiraOneWebsocketResponse;
import de.matgroe.util.GenericBuilder;
import de.matgroe.util.ResourceLoader;

/**
 * Utility provides test data for various unit tests.
 *
 * @author Matthias Groeger - Initial contribution
 */
public class GiraOneTestDataProvider {

  public static GiraOneProject createGiraOneProject() {
    Gson gson = GiraOneTypeMapperFactory.createGson();

    String message =
        ResourceLoader.loadStringResource("/giraone/2.GetUIConfiguration/001-resp.json");
    GiraOneWebsocketResponse response = gson.fromJson(message, GiraOneWebsocketResponse.class);
    assertNotNull(response);
    assertInstanceOf(GetUIConfiguration.class, response.getRequestServerCommand().getCommand());
    GiraOneChannelCollection uiChannels = response.getReply(GiraOneChannelCollection.class);

    GiraOneProject project = new GiraOneProject();
    uiChannels.getChannels().forEach(project::addChannel);

    GiraOneWebserviceResponse wsresponse =
        gson.fromJson(
            ResourceLoader.loadStringResource("/giraone/9.GetDiagnosticDeviceList/001-resp.json"),
            GiraOneWebserviceResponse.class);
    assertNotNull(wsresponse);
    GiraOneComponentCollection componentCollection =
        wsresponse.getReply(GiraOneComponentCollection.class);
    assertNotNull(componentCollection);
    componentCollection.getAllChannels(GiraOneComponentType.KnxButton).forEach(project::addChannel);

    project.addDiagnosticChannel(
        "urn:de:matgroe:giraone-bridge", "Bridge Uptime", "urn:de:matgroe:giraone-bridge:Uptime");

    return project;
  }

  private static GiraOneChannel createInternalDiagnosticChannel(
      String channelUrn, String name, String datapointUrn) {
    GiraOneChannel channel = new GiraOneChannel();
    channel.setUrn(channelUrn);
    channel.setLocation(LOCATION_BRIDGE);
    channel.setName(name);
    channel.setChannelType(GiraOneChannelType.Diagnostic);
    channel.addDataPoint(new GiraOneDataPoint(GiraOneURN.of(datapointUrn)));
    return channel;
  }

  public static GiraOneDeviceConfiguration createGiraOneDeviceConfiguration() {
    Gson g1GsonMapper = GiraOneTypeMapperFactory.createGson();
    return g1GsonMapper
        .fromJson(
            ResourceLoader.loadStringResource("/giraone/4.GetDeviceConfig/001-resp.json"),
            GiraOneWebsocketResponse.class)
        .getReply(GiraOneDeviceConfiguration.class);
  }

  public static GiraOneDataPoint dataPointStepUpDown() {
    return new GiraOneDataPoint(
        "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxSwitchingActuator24-gang2C16A2FBlindActuator12-gang-1.Curtain-5:Step-Up-Down");
  }

  public static GiraOneDataPoint dataPointUpDown() {
    return new GiraOneDataPoint(
        "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxSwitchingActuator24-gang2C16A2FBlindActuator12-gang-1.Curtain-5:Up-Down");
  }

  public static GiraOneDataPoint dataPointMovement() {
    return new GiraOneDataPoint(
        "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxSwitchingActuator24-gang2C16A2FBlindActuator12-gang-1.Curtain-5:Movement");
  }

  public static GiraOneDataPoint dataPointPosition() {
    return new GiraOneDataPoint(
        "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxSwitchingActuator24-gang2C16A2FBlindActuator12-gang-1.Curtain-5:Position");
  }

  public static GiraOneChannel createGiraOneChannel(final String urn) {
    return GenericBuilder.of(GiraOneChannel::new)
        .with(GiraOneChannel::setUrn, urn)
        .with(GiraOneChannel::setChannelType, GiraOneChannelType.Covering)
        .with(GiraOneChannel::setChannelTypeId, GiraOneChannelTypeId.VenetianBlind)
        .with(GiraOneChannel::setFunctionType, GiraOneFunctionType.Covering)
        .with(GiraOneChannel::addDataPoint, dataPointUpDown())
        .with(GiraOneChannel::addDataPoint, dataPointMovement())
        .with(GiraOneChannel::addDataPoint, dataPointMovement())
        .with(GiraOneChannel::addDataPoint, dataPointStepUpDown())
        .build();
  }
}
