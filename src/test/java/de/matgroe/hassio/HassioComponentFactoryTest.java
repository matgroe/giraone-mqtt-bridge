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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import de.matgroe.bridge.GiraOneChannelMqttTopicMapper;
import de.matgroe.giraone.GiraOneTestDataProvider;
import de.matgroe.giraone.client.types.GiraOneChannel;
import de.matgroe.giraone.client.types.GiraOneProject;
import de.matgroe.hassio.types.ClimateHVAC;
import de.matgroe.hassio.types.Component;
import de.matgroe.hassio.types.Cover;
import de.matgroe.hassio.types.Light;
import de.matgroe.hassio.types.Sensor;
import de.matgroe.hassio.types.Switch;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link HassioComponentFactory}
 *
 * @author Matthias Groeger - Initial contribution
 */
public class HassioComponentFactoryTest {

  private GiraOneProject project;

  private HassioComponentFactory hassioComponentFactory;

  @BeforeEach
  void setUp() {
    project = GiraOneTestDataProvider.createGiraOneProject();
    hassioComponentFactory =
        new HassioComponentFactory(new GiraOneChannelMqttTopicMapper("junit", project));
  }

  @Test
  @DisplayName("Should generate de.matgroe.hassio.types.Sensor for Temperature")
  void testTemperatureStatusChannel() {
    Optional<GiraOneChannel> channel =
        project.lookupChannelByUrn("urn:gds:chv:NumericFloatingPointStatus-Float-16");
    channel.ifPresentOrElse(
        (ch) -> {
          Component component = hassioComponentFactory.from(ch);
          assertInstanceOf(Sensor.class, component);
          assertNotNull(component.getUniqueId());
          assertEquals("sensor", component.getPlatform());
          assertEquals("temperature", component.getDeviceClass());
        },
        () -> fail("Channel not found in project"));
  }

  @Test
  @DisplayName("Should generate de.matgroe.hassio.types.Sensor for Humidity")
  void testHumidityStatusChannel() {
    Optional<GiraOneChannel> channel =
        project.lookupChannelByUrn("urn:gds:chv:NumericFloatingPointStatus-Float-1");
    channel.ifPresentOrElse(
        ch -> {
          Component component = hassioComponentFactory.from(ch);
          assertInstanceOf(Sensor.class, component);
          assertNotNull(component.getUniqueId());
          assertEquals("sensor", component.getPlatform());
          assertEquals("humidity", component.getDeviceClass());
          assertEquals(ch.getName(), component.getName());
        },
        () -> fail("Channel not found in project"));
  }

  @Test
  @DisplayName("Should generate de.matgroe.hassio.types.Switch(switch)")
  void testSwitchChannel() {
    Optional<GiraOneChannel> channel = project.lookupChannelByUrn("urn:gds:chv:Switch-Switch-1");
    channel.ifPresentOrElse(
        ch -> {
          Component component = hassioComponentFactory.from(ch);
          assertInstanceOf(Light.class, component);
          assertNotNull(component.getUniqueId());
          assertEquals("light", component.getPlatform());
          assertNull(component.getDeviceClass());
          assertEquals(ch.getName(), component.getName());
        },
        () -> fail("Channel not found in project"));
  }

  @Test
  @DisplayName("Should generate de.matgroe.hassio.types.Switch(outlet)")
  void testPowerOutletChannel() {
    Optional<GiraOneChannel> channel = project.lookupChannelByUrn("urn:gds:chv:Switch-Switch-4");
    channel.ifPresentOrElse(
        ch -> {
          Component component = hassioComponentFactory.from(ch);
          assertInstanceOf(Switch.class, component);
          assertNotNull(component.getUniqueId());
          assertEquals("switch", component.getPlatform());
          assertEquals("outlet", component.getDeviceClass());
          assertEquals(ch.getName(), component.getName());
        },
        () -> fail("Channel not found in project"));
  }

  @Test
  @DisplayName("Should generate de.matgroe.hassio.types.Light")
  void testDimmerChannel() {
    Optional<GiraOneChannel> channel =
        project.lookupChannelByUrn("urn:gds:chv:KNXlight-KNX-Dimmer-8");
    channel.ifPresentOrElse(
        ch -> {
          Component component = hassioComponentFactory.from(ch);
          assertInstanceOf(Light.class, component);
          assertNotNull(component.getUniqueId());
          assertEquals("light", component.getPlatform());
          assertNull(component.getDeviceClass());
          assertEquals(ch.getName(), component.getName());

          assertNotNull(((Light) component).getOnCommandType());
        },
        () -> fail("Channel not found in project"));
  }

  @Test
  @DisplayName("Should generate de.matgroe.hassio.types.Light")
  void testLightChannel() {
    Optional<GiraOneChannel> channel = project.lookupChannelByUrn("urn:gds:chv:Switch-Switch-1");
    channel.ifPresentOrElse(
        ch -> {
          Component component = hassioComponentFactory.from(ch);
          assertInstanceOf(Light.class, component);
          assertNotNull(component.getUniqueId());
          assertEquals("light", component.getPlatform());
          assertNull(component.getDeviceClass());
          assertEquals(ch.getName(), component.getName());
          assertNull(((Light) component).getOnCommandType());
        },
        () -> fail("Channel not found in project"));
  }

  @Test
  @DisplayName("Should generate de.matgroe.hassio.types.Cover(Covering.VenetianBlind)")
  void testCoveringVenetianBlind() {
    Optional<GiraOneChannel> channel =
        project.lookupChannelByUrn("urn:gds:chv:Covering-Blind-With-Position-10");
    channel.ifPresentOrElse(
        ch -> {
          Component component = hassioComponentFactory.from(ch);
          assertInstanceOf(Cover.class, component);
          assertNotNull(component.getUniqueId());
          assertEquals("cover", component.getPlatform());
          assertEquals("shutter", component.getDeviceClass());
          assertEquals(ch.getName(), component.getName());

          Cover cover = (Cover) component;
          assertEquals(
              "junit/command/schlafen/covering/61900bc1_schlafen_raffstore_kl_fenster/position",
              cover.getPositionCommandTopic());
          assertEquals(
              "junit/state/schlafen/covering/61900bc1_schlafen_raffstore_kl_fenster/position",
              cover.getPositionStateTopic());
          assertEquals(
              "junit/command/schlafen/covering/61900bc1_schlafen_raffstore_kl_fenster/slat-position",
              cover.getTiltCommandTopic());
          assertEquals(
              "junit/state/schlafen/covering/61900bc1_schlafen_raffstore_kl_fenster/slat-position",
              cover.getTiltStatusTopic());
        },
        () -> fail("Channel not found in project"));
  }

  @Test
  @DisplayName("Should generate de.matgroe.hassio.types.Cover(Covering.RoofWindow)")
  void testCoveringRoofWindow() {
    Optional<GiraOneChannel> channel =
        project.lookupChannelByUrn("urn:gds:chv:Covering-Blind-With-Position-16");
    channel.ifPresentOrElse(
        ch -> {
          Component component = hassioComponentFactory.from(ch);
          assertInstanceOf(Cover.class, component);
          assertNotNull(component.getUniqueId());
          assertEquals("cover", component.getPlatform());
          assertEquals("window", component.getDeviceClass());
          assertEquals(ch.getName(), component.getName());

          Cover cover = (Cover) component;
          assertNull(cover.getPositionCommandTopic());
          assertNull(cover.getPositionStateTopic());
          assertNull(cover.getTiltCommandTopic());
          assertNull(cover.getTiltStatusTopic());
        },
        () -> fail("Channel not found in project"));
  }

  @Test
  @DisplayName("Should generate de.matgroe.hassio.types.ClimateHVAC")
  void testHeatingCoolingSwitchable() {
    Optional<GiraOneChannel> channel =
        project.lookupChannelByUrn("urn:gds:chv:KNXheating2Fcooling-Heating-Cooling-Switchable-5");
    channel.ifPresentOrElse(
        ch -> {
          Component component = hassioComponentFactory.from(ch);
          assertInstanceOf(ClimateHVAC.class, component);
          assertNotNull(component.getUniqueId());
          assertEquals("climate", component.getPlatform());
          assertNull(component.getDeviceClass());
          assertEquals(ch.getName(), component.getName());

          ClimateHVAC hvac = (ClimateHVAC) component;
        },
        () -> fail("Channel not found in project"));
  }

  @Test
  @DisplayName("Should generate de.matgroe.hassio.types.Sensor")
  void testGdsChannelLocalTime() {
    Optional<GiraOneChannel> channel =
        project.lookupChannelByUrn("urn:gds:ch:GiraOneServer.GIOSRVKX03:GDS-Device-Channel");

    channel.ifPresentOrElse(
        ch -> {
          Component component = hassioComponentFactory.from(ch);
          assertInstanceOf(Sensor.class, component);
          assertNotNull(component.getUniqueId());
          assertEquals("diagnostic", component.getEntityCategory());
          assertEquals("sensor", component.getPlatform());
          assertEquals("timestamp", component.getDeviceClass());
          assertEquals(ch.getName(), component.getName());
        },
        () -> fail("Channel not found in project"));
  }

  @Test
  @DisplayName("Should generate de.matgroe.hassio.types.Sensor")
  void testGiraOneBridgeUptime() {
    Optional<GiraOneChannel> channel = project.lookupChannelByUrn("urn:de:matgroe:giraone-bridge");

    channel.ifPresentOrElse(
        ch -> {
          Component component = hassioComponentFactory.from(ch);
          assertInstanceOf(Sensor.class, component);
          assertNotNull(component.getUniqueId());
          assertEquals("diagnostic", component.getEntityCategory());
          assertEquals("sensor", component.getPlatform());
          assertEquals("timestamp", component.getDeviceClass());
          assertEquals(ch.getName(), component.getName());
        },
        () -> fail("Channel not found in project"));
  }
}
