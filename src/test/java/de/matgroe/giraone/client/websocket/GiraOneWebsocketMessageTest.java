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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.gson.Gson;
import de.matgroe.giraone.client.GiraOneCommand;
import de.matgroe.giraone.client.GiraOneTypeMapperFactory;
import de.matgroe.giraone.client.commands.GetUIConfiguration;
import de.matgroe.giraone.client.commands.RegisterApplication;
import de.matgroe.giraone.client.types.GiraOneChannelCollection;
import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.util.ResourceLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author Matthias Groeger - Initial contribution
 */
class GiraOneWebsocketMessageTest {
  Gson gson;

  @BeforeEach
  void setUp() {
    gson = GiraOneTypeMapperFactory.createGson();
  }

  @DisplayName("Should deserialize websocket response for GetUIConfiguration")
  @Test
  void shouldSerialzeGetUIConfigurationRequest() {
    GiraOneCommand cmd =
        GetUIConfiguration.builder()
            .with(GetUIConfiguration::setGuid, "guid")
            .with(GetUIConfiguration::setInstanceId, "instanceId")
            .build();
    GiraOneWebsocketRequest req = new GiraOneWebsocketRequest(cmd);
    String request = gson.toJson(req);

    GiraOneWebsocketRequest req2 = gson.fromJson(request, GiraOneWebsocketRequest.class);
    req2.getCommand();
  }

  @DisplayName("Should deserialize websocket response for GetUIConfiguration")
  @Test
  void shouldDeserializeGetUIConfiguration() {
    String message =
        ResourceLoader.loadStringResource("/giraone/2.GetUIConfiguration/001-resp.json");
    GiraOneWebsocketResponse response = gson.fromJson(message, GiraOneWebsocketResponse.class);
    assertNotNull(response);
    assertNotNull(response.responseBody);

    GiraOneChannelCollection uiChannels = response.getReply(GiraOneChannelCollection.class);
    assertNotNull(uiChannels);
    assertFalse(uiChannels.getChannels().isEmpty());
  }

  @Test
  void shouldSerializeObjectOfRegisterApplication() {
    GiraOneWebsocketRequest request =
        new GiraOneWebsocketRequest(RegisterApplication.builder().build());

    RegisterApplication registerApplication =
        gson.fromJson(
            gson.toJson(request, GiraOneWebsocketRequest.class), RegisterApplication.class);
    assertNotNull(registerApplication);

    assertInstanceOf(RegisterApplication.class, request.getCommand());
    assertEquals(
        ((RegisterApplication) request.getCommand()).getApplicationId(),
        registerApplication.getApplicationId());
  }

  @Test
  void shouldDeserializeObjectOfSetValue() {
    String message = ResourceLoader.loadStringResource("/giraone/2.SetValue/001-resp.json");
    GiraOneWebsocketResponse response = gson.fromJson(message, GiraOneWebsocketResponse.class);
    assertNotNull(response);
    assertNotNull(response.responseBody);

    GiraOneValue value = response.getReply(GiraOneValue.class);
    assertNotNull(value);
    assertEquals("0", value.getValue());
    assertEquals(
        "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxSwitchingActuator24-gang2C16A2FBlindActuator12-gang-1.Switching-19:OnOff",
        value.getDatapointUrn());
  }
}
