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
package de.matgroe.giraone.client.websocket;

import com.google.gson.Gson;
import de.matgroe.giraone.client.GiraOneCommand;
import de.matgroe.giraone.client.GiraOneTypeMapperFactory;
import de.matgroe.giraone.client.commands.GetUIConfiguration;
import de.matgroe.giraone.client.commands.RegisterApplication;
import de.matgroe.giraone.client.types.GiraOneChannelCollection;
import de.matgroe.util.ResourceLoader;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *
 *
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
        GiraOneCommand cmd = GetUIConfiguration.builder().with(GetUIConfiguration::setGuid, "guid")
                .with(GetUIConfiguration::setInstanceId, "instanceId").build();
        GiraOneWebsocketRequest req = new GiraOneWebsocketRequest(cmd);
        String request = gson.toJson(req);

        GiraOneWebsocketRequest req2 = gson.fromJson(request, GiraOneWebsocketRequest.class);
        req2.getCommand();
    }

    @DisplayName("Should deserialize websocket response for GetUIConfiguration")
    @Test
    void shouldDeserializeGetUIConfiguration() {
        String message = ResourceLoader.loadStringResource("/giraone/2.GetUIConfiguration/001-resp.json");
        GiraOneWebsocketResponse response = gson.fromJson(message, GiraOneWebsocketResponse.class);
        assertNotNull(response);
        assertNotNull(response.responseBody);

        GiraOneChannelCollection uiChannels = response.getReply(GiraOneChannelCollection.class);
        assertNotNull(uiChannels);
        assertFalse(uiChannels.getChannels().isEmpty());
    }

    @Test
    void shouldSerializeObjectOfRegisterApplication() {
        GiraOneWebsocketRequest request = new GiraOneWebsocketRequest(RegisterApplication.builder().build());

        RegisterApplication registerApplication = gson.fromJson(gson.toJson(request, GiraOneWebsocketRequest.class),
                RegisterApplication.class);
        assertNotNull(registerApplication);

        assertInstanceOf(RegisterApplication.class, request.getCommand());
        assertEquals(((RegisterApplication) request.getCommand()).getApplicationId(),
                registerApplication.getApplicationId());
    }
}
