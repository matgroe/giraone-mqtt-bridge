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

package de.matgroe.hassio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import de.matgroe.GiraOneMqttApplicationProperties;
import de.matgroe.SpringTestConfiguration;
import de.matgroe.bridge.GiraOneChannelMqttTopicMapper;
import de.matgroe.giraone.GiraOneClientProperties;
import de.matgroe.giraone.client.GiraOneClient;
import de.matgroe.giraone.client.types.GiraOneChannel;
import de.matgroe.giraone.client.types.GiraOneDeviceConfiguration;
import de.matgroe.hassio.types.Device;
import de.matgroe.hassio.types.DiscoveryMessage;
import de.matgroe.hassio.types.Origin;
import de.matgroe.mqtt.MqttClientProperties;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** Testclass for HassioDiscoveryMessageFactory */
@ExtendWith(SpringExtension.class)
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.NEVER)
@ComponentScan("de.matgroe")
@ContextConfiguration(
    classes = {
      SpringTestConfiguration.class,
      GiraOneClientProperties.class,
      MqttClientProperties.class,
      GiraOneMqttApplicationProperties.class
    })
public class HassioDiscoveryMessageFactoryTest {

  @Autowired GiraOneMqttApplicationProperties applicationProperties;

  @Autowired GiraOneClient giraOneClient;

  HassioDiscoveryMessageFactory factory;
  HassioComponentFactory hassioComponentFactory;

  Gson gson = new Gson();

  @BeforeEach
  void setUp() {

    factory =
        new HassioDiscoveryMessageFactory(
            applicationProperties, giraOneClient.lookupGiraOneDeviceConfiguration());
    hassioComponentFactory =
        new HassioComponentFactory(
            new GiraOneChannelMqttTopicMapper("junit", giraOneClient.getGiraOneProject()));
  }

  @Test
  @DisplayName("Should map applications MqttClientProperties  to MQTT-Origin")
  void testCreateOrigin() {
    Origin o = factory.createOrigin(applicationProperties);
    assertEquals(o.getName(), applicationProperties.getName());
    assertEquals(o.getSupportUrl(), applicationProperties.getUrl());
  }

  @Test
  @DisplayName("Should map GiraOneDeviceConfiguration to MQTT-Device")
  void testCreateDevice() {
    GiraOneDeviceConfiguration cfg = giraOneClient.lookupGiraOneDeviceConfiguration();
    Device d = factory.createDevice(cfg);
    assertEquals(cfg.get(GiraOneDeviceConfiguration.DEVICE_NAME), d.getName());
    assertEquals(cfg.get(GiraOneDeviceConfiguration.SERIAL_NUMBER), d.getSerialNumber());
    assertEquals(cfg.get(GiraOneDeviceConfiguration.APP_DEVICE_NAME), d.getModel());
    assertEquals("Gira", d.getManufacturer());
    assertTrue(d.getIdentifiers().contains(cfg.get(GiraOneDeviceConfiguration.SERIAL_NUMBER)));
  }

  @Test
  @DisplayName("Should generate correct discovery topic name")
  void testCreateConfigurationTopicName() {
    GiraOneDeviceConfiguration cfg = giraOneClient.lookupGiraOneDeviceConfiguration();
    assertEquals("homeassistant/device/GIOSRVKX0340073A/config", factory.createDiscoveryTopic());
  }

  @Test
  @DisplayName("Should generate correct discovery topic name")
  void testCreateDiscoveryMessage() {
    DiscoveryMessage dm = factory.createDiscoveryMessage();

    Optional<GiraOneChannel> channel =
        giraOneClient.getGiraOneProject().lookupChannelByUrn("urn:gds:chv:KNXlight-KNX-Dimmer-1");
    dm.addComponent(hassioComponentFactory.from(channel.get()));

    System.out.println(gson.toJson(dm));
  }
}
