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
package de.matgroe.hassio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import de.matgroe.GiraOneMqttApplicationProperties;
import de.matgroe.SpringTestConfiguration;
import de.matgroe.bridge.GiraOneChannelMqttTopicMapper;
import de.matgroe.giraone.GiraOneClientProperties;
import de.matgroe.giraone.client.GiraOneClient;
import de.matgroe.giraone.client.types.GiraOneDeviceConfiguration;
import de.matgroe.hassio.types.Device;
import de.matgroe.hassio.types.Origin;
import de.matgroe.mqtt.MqttClientProperties;
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
}
