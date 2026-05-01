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
package de.matgroe.bridge;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import de.matgroe.GiraOneMqttApplicationProperties;
import de.matgroe.giraone.GiraOneTestDataProvider;
import de.matgroe.giraone.client.GiraOneClient;
import de.matgroe.mqtt.MqttClient;
import de.matgroe.mqtt.MqttClientConnectionState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GiraOneMqttBridgeTest {
  GiraOneClient giraOneClient = mock(GiraOneClient.class);
  MqttClient mqttClient = mock(MqttClient.class);

  GiraOneMqttBridge theBridge;

  @BeforeEach
  void setup() {
    GiraOneMqttApplicationProperties applicationProperties =
        mock(GiraOneMqttApplicationProperties.class);
    when(applicationProperties.getName()).thenReturn("appname");
    when(applicationProperties.getUrl()).thenReturn("http://localhost");

    when(giraOneClient.getGiraOneProject())
        .thenReturn(GiraOneTestDataProvider.createGiraOneProject());
    when(giraOneClient.lookupGiraOneDeviceConfiguration())
        .thenReturn(GiraOneTestDataProvider.createGiraOneDeviceConfiguration());
    theBridge = spy(new GiraOneMqttBridge(applicationProperties, giraOneClient, mqttClient));
  }

  @Test
  void testInit() {
    theBridge.onMqttClientConnectionStateChanged(MqttClientConnectionState.Connected);
  }
}
