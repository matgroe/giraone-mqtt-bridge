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

import static org.awaitility.Awaitility.await;
import static org.awaitility.Duration.ONE_MINUTE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.matgroe.giraone.GiraOneClientProperties;
import de.matgroe.giraone.client.GiraOneClientConnectionState;
import de.matgroe.giraone.client.types.GiraOneDataPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link GiraOneWebsocketClient}
 *
 * @author Matthias Groeger - Initial contribution
 */
@Disabled
public class GiraOneClientConnectionTest {
  private GiraOneClientProperties configuration = new GiraOneClientProperties();
  private GiraOneWebsocketClient giraClient = new GiraOneWebsocketClient(configuration);

  @BeforeEach
  void setUp() {
    configuration.username = "User";
    configuration.password = "!Ncc1701D";
    configuration.hostname = "192.168.178.38";
    configuration.maxTextMessageSize = 350000;
    configuration.defaultTimeoutSeconds = 45;
  }

  @Test
  void testConnectWithInvalidCredentials() {
    configuration.password = "_invalid_";
    giraClient = new GiraOneWebsocketClient(configuration);
    giraClient.connect();
  }

  @Test
  void testConnectWithInvalidHostname() {
    configuration.hostname = "127.0.0.1";
    giraClient = new GiraOneWebsocketClient(configuration);
    giraClient.connect();
  }

  @Test
  void testConnectWithInvalidTextMessageSize() {
    configuration.maxTextMessageSize = 20;
    giraClient = new GiraOneWebsocketClient(configuration);
    giraClient.connect();
  }

  @DisplayName("Test Connect, Register and Disconnect against Gira One Server Websocket")
  @Test
  void testConnectRegisterAndDisconnect() throws Exception {
    GiraOneWebsocketClient giraOneWebsocketClient = new GiraOneWebsocketClient(configuration);

    giraOneWebsocketClient.subscribeOnConnectionState(
        c -> {
          if (c == GiraOneWebsocketConnectionState.Connected) {
            // GiraOneDataPoint dp = GiraOneTestDataProvider.dataPointBuilder("slat-position", 0,
            // "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxSwitchingActuator24-gang2C16A2FBlindActuator12-gang-1.Curtain-4:Slat-Position");
            GiraOneDataPoint dp =
                new GiraOneDataPoint(
                    "urn:gds:dp:GiraOneServer.GIOSRVKX03:GDS-Device-Channel:Ready");
            // giraOneWebsocketClient.lookupGiraOneValue(dp);
            giraOneWebsocketClient.lookupGiraOneDeviceConfiguration();
            giraOneWebsocketClient.lookupGiraOneChannels();
            giraOneWebsocketClient.lookupGiraOneDataPointValue(dp);
          }
        });

    giraOneWebsocketClient.connect();
    for (int i = 0; i < 10; i++) {
      Thread.sleep(500);
    }
    await()
        .atMost(ONE_MINUTE)
        .untilAsserted(
            () ->
                assertEquals(
                    GiraOneClientConnectionState.Connected,
                    giraOneWebsocketClient.connectionState.getValue()));
  }
}
