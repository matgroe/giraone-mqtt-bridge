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
package de.matgroe.giraone;

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

    return project;
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
