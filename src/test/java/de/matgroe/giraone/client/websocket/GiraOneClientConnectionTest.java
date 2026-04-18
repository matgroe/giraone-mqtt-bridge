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

import de.matgroe.giraone.GiraOneClientConfiguration;
import de.matgroe.giraone.client.GiraOneClientConnectionState;
import de.matgroe.giraone.client.types.GiraOneDataPoint;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Duration.ONE_MINUTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private GiraOneClientConfiguration configuration = new GiraOneClientConfiguration();
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

        giraOneWebsocketClient.subscribeOnConnectionState(c -> {
            if (c == GiraOneWebsocketConnectionState.Connected) {
                // GiraOneDataPoint dp = GiraOneTestDataProvider.dataPointBuilder("slat-position", 0,
                // "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxSwitchingActuator24-gang2C16A2FBlindActuator12-gang-1.Curtain-4:Slat-Position");
                GiraOneDataPoint dp = new GiraOneDataPoint(
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
        await().atMost(ONE_MINUTE).untilAsserted(() -> assertEquals(GiraOneClientConnectionState.Connected, giraOneWebsocketClient.connectionState.getValue()));
    }
}
