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

package de.matgroe.mqtt;

import de.matgroe.giraone.client.GiraOneClient;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Duration.ONE_MINUTE;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test class for {@link MqttClient}
 *
 * @author Matthias Groeger - Initial contribution
 */
@Disabled
public class MqttClientTest {

    MqttClient mqttClient;

    @BeforeEach
    void setUp() {
        MqttClientProperties mqttClientProperties = new MqttClientProperties();
        mqttClientProperties.mqttBroker = "192.168.178.69";
        mqttClientProperties.mqttPort = 1883;
        mqttClientProperties.username = "mqtt-dev";
        mqttClientProperties.password = "mqtt-pass";

        GiraOneClient giraOneClient = Mockito.mock(GiraOneClient.class);
        mqttClient = new MqttClient(mqttClientProperties);
    }

    @Test
    void createRegistrationMessage(){
        mqttClient.connect("junit");
        await().atMost(ONE_MINUTE).untilAsserted(() -> assertTrue(true));
       // mqttBridge.register();
    }

}
