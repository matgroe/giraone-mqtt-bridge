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

import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.message.connect.Mqtt5ConnectBuilderBase;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import de.matgroe.giraone.client.GiraOneClient;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Duration.ONE_MINUTE;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link MqttClient}
 *
 * @author Matthias Groeger - Initial contribution
 */
@Disabled
class MqttClientTest {

    MqttClient mqttClient;
    Mqtt5AsyncClient mqtt5ClientMock = mock(Mqtt5AsyncClient.class);

    @BeforeEach
    void setUp() {
        MqttClientProperties mqttClientProperties = new MqttClientProperties();
        mqttClientProperties.mqttBroker = "192.168.178.69";
        mqttClientProperties.mqttPort = 1883;
        mqttClientProperties.username = "mqtt-dev";
        mqttClientProperties.password = "mqtt-pass";

        mqttClient = spy(new MqttClient(mqttClientProperties));
        when(mqttClient.buildMqtt5Client()).thenReturn(mqtt5ClientMock);
    }

    @Test
    void createRegistrationMessage(){
        mqttClient.connect("junit");
        await().atMost(ONE_MINUTE).untilAsserted(() -> assertTrue(true));
       // mqttBridge.register();
    }

}
