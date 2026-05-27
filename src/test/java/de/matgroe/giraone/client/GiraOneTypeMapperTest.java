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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import com.google.gson.JsonPrimitive;
import de.matgroe.giraone.client.types.GiraOneChannel;
import de.matgroe.giraone.client.types.GiraOneChannelCollection;
import de.matgroe.giraone.client.types.GiraOneChannelType;
import de.matgroe.giraone.client.types.GiraOneChannelTypeId;
import de.matgroe.giraone.client.types.GiraOneComponentCollection;
import de.matgroe.giraone.client.types.GiraOneComponentType;
import de.matgroe.giraone.client.types.GiraOneDataPoint;
import de.matgroe.giraone.client.types.GiraOneDeviceConfiguration;
import de.matgroe.giraone.client.types.GiraOneEvent;
import de.matgroe.giraone.client.types.GiraOneFunctionType;
import de.matgroe.giraone.client.types.GiraOneProcessView;
import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.giraone.client.webservice.GiraOneWebserviceResponse;
import de.matgroe.giraone.client.websocket.GiraOneWebsocketResponse;
import de.matgroe.util.ResourceLoader;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test class for {@link GiraOneTypeMapperTest}
 *
 * @author Matthias Groeger - Initial contribution
 */
public class GiraOneTypeMapperTest {
  private Gson gson = GiraOneTypeMapperFactory.createGson();

  @BeforeEach
  void setUp() {
    gson = GiraOneTypeMapperFactory.createGson();
  }

  private GiraOneCommandResponse createGiraOneWebsocketResponseFrom(final String message) {
    return Objects.requireNonNull(
        gson.fromJson(ResourceLoader.loadStringResource(message), GiraOneWebsocketResponse.class));
  }

  private GiraOneCommandResponse createGiraOneWebserviceResponseFrom(final String message) {
    return Objects.requireNonNull(
        gson.fromJson(ResourceLoader.loadStringResource(message), GiraOneWebserviceResponse.class));
  }

  private static Stream<Arguments> provideWebsocketMessageTypes() {
    return Stream.of(
        Arguments.of("/giraone/0.Types/001-invalid-message.json", GiraOneMessageType.Invalid),
        Arguments.of("/giraone/0.Types/002-response-error.json", GiraOneMessageType.Error),
        Arguments.of("/giraone/0.Types/002-response-ok.json", GiraOneMessageType.Response),
        Arguments.of("/giraone/0.Types/003-event-error.json", GiraOneMessageType.Error),
        Arguments.of("/giraone/0.Types/003-event-ok.json", GiraOneMessageType.Event));
  }

  private static Stream<Arguments> provideGiraOneComponentUrns() {
    return Stream.of(
        Arguments.of(
            "urn:gds:cmp:GiraOneServer.GIOSRVKX03:KnxButton4Comfort4CSystem55Rocker2-gang-10",
            GiraOneComponentType.KnxButton),
        Arguments.of(
            "urn:gds:cmp:GiraOneServer.GIOSRVKX03:KnxButton4Comfort2CSystem55Rocker3-gang-14",
            GiraOneComponentType.KnxButton),
        Arguments.of(
            "urn:gds:cmp:GiraOneServer.GIOSRVKX03:KnxDimmingActuator6-gang-1",
            GiraOneComponentType.KnxDimmingActuator),
        Arguments.of(
            "urn:gds:cmp:GiraOneServer.GIOSRVKX03:KnxDimmingActuator4-gang-2",
            GiraOneComponentType.KnxDimmingActuator),
        Arguments.of(
            "urn:gds:cmp:GiraOneServer.GIOSRVKX03:KnxHvacActuator12-gang-1",
            GiraOneComponentType.KnxHvacActuator),
        Arguments.of(
            "urn:gds:cmp:GiraOneServer.GIOSRVKX03:KnxHvacActuator6-gang-2",
            GiraOneComponentType.KnxHvacActuator),
        Arguments.of(
            "urn:gds:cmp:GiraOneServer.GIOSRVKX03:KnxSwitchingActuator16-gang2C16A2FBlindActuator8-gang-1",
            GiraOneComponentType.KnxSwitchingActuator),
        Arguments.of(
            "urn:gds:cmp:GiraOneServer.GIOSRVKX03:KnxSwitchingActuator24-gang2C16A2FBlindActuator1-gang-11",
            GiraOneComponentType.KnxSwitchingActuator),
        Arguments.of(
            "urn:gds:cmp:GiraOneServer.GIOSRVKX03:KnxSwitchingActuator24-gang2C16A2FBlindActuator12-gang-2",
            GiraOneComponentType.KnxSwitchingActuator),
        Arguments.of(
            "urn:gds:cmp:GiraOneServer.GIOSRVKX03:KnxUnknownSwitchingActuator6-gang2C16A2FBlindActuator12-gang-2",
            GiraOneComponentType.Unknown));
  }

  @DisplayName("message should deserialize to GiraOneMessageType")
  @ParameterizedTest
  @MethodSource("provideWebsocketMessageTypes")
  void shouldDeserialize2WebsocketMessageType(String resourceName, GiraOneMessageType expected) {
    String message = ResourceLoader.loadStringResource(resourceName);
    assertEquals(expected, gson.fromJson(message, GiraOneMessageType.class));
  }

  @DisplayName("message should deserialize to GiraOneComponentType")
  @ParameterizedTest
  @MethodSource("provideGiraOneComponentUrns")
  void shouldDeserialize2GiraOneComponentType(String urn, GiraOneComponentType expected) {
    assertEquals(expected, gson.fromJson(new JsonPrimitive(urn), GiraOneComponentType.class));
  }

  @DisplayName("message should deserialize a ValueEvent to GiraOneEvent")
  @Test
  void shouldDeserialize2WebsocketValueEvent() {
    String message = ResourceLoader.loadStringResource("/giraone/0.Events/001-evt.json");

    GiraOneEvent event = gson.fromJson(message, GiraOneEvent.class);
    assertNotNull(event);

    assertEquals(220940, event.getId());
    assertEquals("2.3:false", event.getNewInternal());
    assertEquals("2.3:true", event.getOldInternal());
    assertEquals("0", event.getNewValue());
    assertEquals("1", event.getOldValue());
    assertEquals("k:12.0.31", event.getSource());
    assertEquals(
        "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxSwitchingActuator24-gang2C16A2FBlindActuator12-gang-1.Curtain-2:Movement",
        event.getUrn());
    assertEquals("Value", event.getState());
  }

  @DisplayName("message should deserialize to GiraOneCommandResponse")
  @Test
  void shouldDeserialize2GiraOneCommandResponse() {
    GiraOneCommandResponse response =
        createGiraOneWebsocketResponseFrom("/giraone/2.GetUIConfiguration/001-resp.json");
    assertNotNull(response);
    assertNotNull(response.getResponseBody());
  }

  @DisplayName("message should deserialize to GiraOneCommandResponse of GiraOneDeviceConfiguration")
  @Test
  void shouldDeserialize2GiraOneCommandResponseWithGiraOneDeviceConfiguration() {
    GiraOneCommandResponse response =
        createGiraOneWebsocketResponseFrom("/giraone/2.GetUIConfiguration/001-resp.json");
    assertNotNull(response);

    GiraOneChannelCollection channels = response.getReply(GiraOneChannelCollection.class);
    assertNotNull(channels);
    GiraOneChannel g1ch =
        channels.getChannels().stream()
            .filter(f -> "urn:gds:chv:KNXlight-KNX-Dimmer-6".equals(f.getUrn()))
            .findFirst()
            .orElse(null);
    assertNotNull(g1ch);
    Optional<GiraOneDataPoint> onoff = g1ch.getDatapoint("OnOff");
    assertTrue(onoff.isPresent());
    assertEquals(204378, onoff.get().getId());
  }

  @DisplayName("message should deserialize to GiraOneCommandResponse of GiraOneChannelValue")
  @Test
  void shouldDeserialize2GiraOneCommandResponseWithGiraOneValue() {
    GiraOneCommandResponse response =
        createGiraOneWebsocketResponseFrom("/giraone/2.GetValue/001-resp.json");
    assertNotNull(response);
    GiraOneValue state = response.getReply(GiraOneValue.class);
    assertNotNull(state);
  }

  @DisplayName("message should deserialize to GiraOneEvent")
  @Test
  void shouldDeserialize2GiraOneDeviceConfiguration() {
    GiraOneCommandResponse response =
        createGiraOneWebsocketResponseFrom("/giraone/4.GetDeviceConfig/001-resp.json");

    assertNotNull(response);
    GiraOneDeviceConfiguration deviceCfg = response.getReply(GiraOneDeviceConfiguration.class);
    assertNotNull(deviceCfg);

    assertEquals(
        deviceCfg.get(GiraOneDeviceConfiguration.CURRENT_APPLICATION_VERSION), "2.0.108.0");
    assertEquals(deviceCfg.get(GiraOneDeviceConfiguration.CURRENT_FIRMWARE_VERSION), "2.0.108.0");
    assertEquals(deviceCfg.get(GiraOneDeviceConfiguration.CURRENT_SYSTEM), "System B");
    assertEquals(deviceCfg.get(GiraOneDeviceConfiguration.DEVICE_ID), "OSRVKX03");
    assertEquals(deviceCfg.get(GiraOneDeviceConfiguration.DEVICE_NAME), "GiraOneServer");
  }

  @DisplayName("GetDiagnosticDeviceList message should deserialize to GiraOneComponents")
  @Test
  void shouldDeserialize2GiraOneComponents() {
    GiraOneCommandResponse response =
        createGiraOneWebserviceResponseFrom("/giraone/9.GetDiagnosticDeviceList/001-resp.json");
    assertNotNull(response);
    GiraOneComponentCollection componentCollection =
        response.getReply(GiraOneComponentCollection.class);
    assertNotNull(componentCollection);
    Collection<GiraOneChannel> g1channels =
        componentCollection.getAllChannels(GiraOneComponentType.KnxButton);
    assertNotNull(g1channels);

    GiraOneChannel g1ch =
        g1channels.stream()
            .filter(
                f ->
                    "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxButton4Comfort2CSystem55Rocker3-gang-2.Dimming-2"
                        .equals(f.getUrn()))
            .findFirst()
            .orElse(null);
    assertNotNull(g1ch);
  }

  @DisplayName("GetDiagnosticDeviceList message should deserialize to GiraOneComponents")
  @Test
  void shouldDeserialize2GiraOneChannel() {
    GiraOneChannel channel =
        gson.fromJson(
            ResourceLoader.loadStringResource(
                "/giraone/11.Messages/channelViewUrn:urn:gds:chv:KNXlight-KNX-Dimmer-3.json"),
            GiraOneChannel.class);
    assertEquals("urn:gds:chv:KNXlight-KNX-Dimmer-3", channel.getUrn());
    assertEquals(GiraOneChannelType.Dimmer, channel.getChannelType());
    assertEquals(GiraOneChannelTypeId.Light, channel.getChannelTypeId());
    assertEquals(GiraOneFunctionType.Light, channel.getFunctionType());
    assertEquals(Optional.of("0.4"), channel.getParameterValue("ButtonTimeout"));
    assertEquals(Optional.of("Aus"), channel.getParameterValue("OffText"));
  }

  @DisplayName("message should deserialize to GiraOneProcessState")
  @Test
  void shouldDeserialize2GiraOneProcessState() {
    GiraOneCommandResponse response =
        createGiraOneWebsocketResponseFrom("/giraone/3.GetProcessView/001-resp.json");
    assertNotNull(response);
    assertNotNull(response.getResponseBody());
    assertNotNull(response.getReply(GiraOneProcessView.class));
  }
}
