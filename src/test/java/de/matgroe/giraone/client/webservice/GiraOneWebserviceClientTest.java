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
package de.matgroe.giraone.client.webservice;

import com.google.gson.JsonParser;
import de.matgroe.giraone.GiraOneClientConfiguration;
import de.matgroe.giraone.client.GiraOneCommunicationException;
import de.matgroe.giraone.client.types.GiraOneComponentCollection;
import de.matgroe.giraone.client.types.GiraOneComponentType;
import de.matgroe.giraone.client.types.GiraOneDataPoint;
import de.matgroe.giraone.client.websocket.GiraOneWebsocketSequence;
import de.matgroe.util.ResourceLoader;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit Tests for {@link GiraOneWebserviceClient}.
 *
 * @author Matthias Gröger - Initial contribution
 */
class GiraOneWebserviceClientTest {
    private GiraOneWebserviceClient giraOneWebserviceClient;
    private GiraOneClientConfiguration configuration;

    @BeforeEach
    void setUp() {
        GiraOneWebsocketSequence.reset();

        configuration = new GiraOneClientConfiguration();
        configuration.username = "User";
        configuration.password = "!Ncc1701D";
        configuration.hostname = "192.168.178.38";
        configuration.maxTextMessageSize = 350000;
        configuration.defaultTimeoutSeconds = 45;

        giraOneWebserviceClient = Mockito.spy(new GiraOneWebserviceClient(configuration));
    }

    @DisplayName("Should authenticate against gira one server")
    @Test
    void testWebserviceAuthentication() throws Exception {
        String response = ResourceLoader.loadStringResource("/giraone/7.GetPasswordSalt/001-resp.json");

        Mockito.doReturn(response).when(giraOneWebserviceClient).doPost(Mockito.any());
        giraOneWebserviceClient.connect();

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(giraOneWebserviceClient, times(2)).doPost(argumentCaptor.capture());
        String[] args = argumentCaptor.getAllValues().toArray(new String[0]);

        assertEquals("getPasswordSalt", JsonParser.parseString(args[0]).getAsJsonObject().get("command").getAsString());
        assertEquals(configuration.username, JsonParser.parseString(args[0]).getAsJsonObject().getAsJsonObject("data")
                .get("username").getAsString());

        assertEquals("doAuthenticateSession",
                JsonParser.parseString(args[1]).getAsJsonObject().get("command").getAsString());
        assertEquals("EC1D6D0C2CCEAD3C3A6CD0536FE1E4968AF3BAE59DBF9730FE36338A7B03DB7D",
                JsonParser.parseString(args[1]).getAsJsonObject().getAsJsonObject("data").get("token").getAsString());
    }

    @DisplayName("Should fail with GiraOneCommunicationException")
    @Test
    void testWebserviceAuthenticationFails() throws Exception {
        String response = ResourceLoader.loadStringResource("/giraone/7.GetPasswordSalt/002-resp.json");

        Mockito.doReturn(response).when(giraOneWebserviceClient).doPost(Mockito.any());
        GiraOneCommunicationException thrown = assertThrows(GiraOneCommunicationException.class,
                () -> giraOneWebserviceClient.connect(),
                "Expected giraOneWebserviceClient.connect() to throw GiraOneCommunicationException, but it didn't");
        assertEquals("getPasswordSalt", thrown.getCausingCommand().getCommand());
        assertEquals("ERR_COMMUNICATION.235", thrown.getMessage());
    }

    @DisplayName("Should provide GiraOneComponentCollection")
    @Test
    void testLookupGiraOneComponentCollection() throws Exception {
        String response = ResourceLoader.loadStringResource("/giraone/9.GetDiagnosticDeviceList/001-resp.json");
        Mockito.doReturn(response).when(giraOneWebserviceClient).doPost(Mockito.any());

        GiraOneComponentCollection components = giraOneWebserviceClient.lookupGiraOneComponentCollection();
        assertNotNull(components);

        components.getAllChannels(GiraOneComponentType.KnxButton);
    }

    @DisplayName("Should provide GiraOneComponentCollection")
    @Test
    void testChangeGiraOneDataPointValue() throws Exception {
        GiraOneDataPoint dp = new GiraOneDataPoint(
                "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxDimmingActuator4-gang-1.DimmingActuator-1:Brightness");
        giraOneWebserviceClient.connect();
        giraOneWebserviceClient.changeGiraOneDataPointValue(dp, "0");

        Thread.sleep(2000);
        giraOneWebserviceClient.readGiraOneDataPointValue(dp);
    }
}
