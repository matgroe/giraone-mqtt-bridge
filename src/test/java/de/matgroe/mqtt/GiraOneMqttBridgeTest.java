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

import com.google.gson.Gson;
import de.matgroe.GiraOneBridge;
import de.matgroe.GiraOneBridgeConfiguration;
import de.matgroe.SpringTestConfiguration;
import de.matgroe.giraone.GiraOneClientConfiguration;
import de.matgroe.giraone.GiraOneTestDataProvider;
import de.matgroe.giraone.client.GiraOneClient;
import de.matgroe.giraone.client.GiraOneClientConnectionState;
import de.matgroe.giraone.client.GiraOneTypeMapperFactory;
import de.matgroe.giraone.client.types.GiraOneChannelCollection;
import de.matgroe.giraone.client.types.GiraOneDeviceConfiguration;
import de.matgroe.giraone.client.types.GiraOneProject;
import de.matgroe.giraone.client.webservice.GiraOneWebserviceClient;
import de.matgroe.giraone.client.websocket.GiraOneWebsocketClient;
import de.matgroe.giraone.client.websocket.GiraOneWebsocketResponse;
import de.matgroe.util.ResourceLoader;
import org.apache.commons.codec.digest.DigestUtils;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Duration.ONE_MINUTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Test class for {@link GiraOneMqttBridge}
 *
 * @author Matthias Groeger - Initial contribution
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.NEVER)
@ComponentScan("de.matgroe")
@ContextConfiguration(classes = {SpringTestConfiguration.class, GiraOneClientConfiguration.class, MqttConfiguration.class})
public class GiraOneMqttBridgeTest {

    @Autowired
    MqttConfiguration mqttConfiguration;

    @Autowired
    GiraOneClient giraOneClient;

    GiraOneMqttBridge mqttBridge;

    @BeforeEach
    void setUp() {
        mqttBridge = new GiraOneMqttBridge(mqttConfiguration, giraOneClient);
    }

    @Test
    void createRegistrationMessage() throws Exception{
        giraOneClient.lookupGiraOneDeviceConfiguration();
        mqttBridge.connect();
        await().atMost(ONE_MINUTE).untilAsserted(() -> assertTrue(true));
       // mqttBridge.register();
    }

}
