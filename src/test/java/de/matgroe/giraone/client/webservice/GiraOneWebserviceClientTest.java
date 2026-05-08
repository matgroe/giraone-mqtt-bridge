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
package de.matgroe.giraone.client.webservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.google.gson.JsonParser;
import de.matgroe.giraone.GiraOneClientProperties;
import de.matgroe.giraone.client.GiraOneCommunicationException;
import de.matgroe.giraone.client.types.GiraOneComponentCollection;
import de.matgroe.giraone.client.types.GiraOneComponentType;
import de.matgroe.giraone.client.websocket.GiraOneWebsocketSequence;
import de.matgroe.util.ResourceLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * Unit Tests for {@link GiraOneWebserviceClient}.
 *
 * @author Matthias Gröger - Initial contribution
 */
class GiraOneWebserviceClientTest {
  private GiraOneWebserviceClient giraOneWebserviceClient;
  private GiraOneClientProperties configuration;

  @BeforeEach
  void setUp() {
    GiraOneWebsocketSequence.reset();

    configuration = new GiraOneClientProperties();
    configuration.username = "User";
    configuration.password = "passowrd";
    configuration.hostname = "localhost";
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

    assertEquals(
        "getPasswordSalt",
        JsonParser.parseString(args[0]).getAsJsonObject().get("command").getAsString());
    assertEquals(
        configuration.username,
        JsonParser.parseString(args[0])
            .getAsJsonObject()
            .getAsJsonObject("data")
            .get("username")
            .getAsString());

    assertEquals(
        "doAuthenticateSession",
        JsonParser.parseString(args[1]).getAsJsonObject().get("command").getAsString());
    assertEquals(
        "CADCD53C53A6BF8D3DECB15BEC310EA1C98614A5960E156C922D707FBDF7E84E",
        JsonParser.parseString(args[1])
            .getAsJsonObject()
            .getAsJsonObject("data")
            .get("token")
            .getAsString());
  }

  @DisplayName("Should fail with GiraOneCommunicationException")
  @Test
  void testWebserviceAuthenticationFails() throws Exception {
    String response = ResourceLoader.loadStringResource("/giraone/7.GetPasswordSalt/002-resp.json");

    Mockito.doReturn(response).when(giraOneWebserviceClient).doPost(Mockito.any());
    GiraOneCommunicationException thrown =
        assertThrows(
            GiraOneCommunicationException.class,
            () -> giraOneWebserviceClient.connect(),
            "Expected giraOneWebserviceClient.connect() to throw GiraOneCommunicationException, but it didn't");
    assertEquals("getPasswordSalt", thrown.getCausingCommand().getCommand());
    assertEquals("ERR_COMMUNICATION.235", thrown.getMessage());
  }

  @DisplayName("Should provide GiraOneComponentCollection")
  @Test
  void testLookupGiraOneComponentCollection() throws Exception {
    String response =
        ResourceLoader.loadStringResource("/giraone/9.GetDiagnosticDeviceList/001-resp.json");
    Mockito.doReturn(response).when(giraOneWebserviceClient).doPost(Mockito.any());

    GiraOneComponentCollection components =
        giraOneWebserviceClient.lookupGiraOneComponentCollection();
    assertNotNull(components);

    components.getAllChannels(GiraOneComponentType.KnxButton);
  }
}
