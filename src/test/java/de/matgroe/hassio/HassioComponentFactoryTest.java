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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import de.matgroe.bridge.GiraOneChannelMqttTopicMapper;
import de.matgroe.giraone.GiraOneTestDataProvider;
import de.matgroe.giraone.client.types.GiraOneChannel;
import de.matgroe.giraone.client.types.GiraOneProject;
import de.matgroe.hassio.types.Component;
import de.matgroe.hassio.types.Cover;
import de.matgroe.hassio.types.Light;
import de.matgroe.hassio.types.Sensor;
import de.matgroe.hassio.types.Switch;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
              "junit/command/knxswitchingactuator24-gang2c16a2fblindactuator12-gang-2/curtain-4/position",
              cover.getPositionCommandTopic());
          assertEquals(
              "junit/state/knxswitchingactuator24-gang2c16a2fblindactuator12-gang-2/curtain-4/position",
              cover.getPositionStateTopic());
          assertEquals(
              "junit/command/knxswitchingactuator24-gang2c16a2fblindactuator12-gang-2/curtain-4/slat-position",
              cover.getTiltCommandTopic());
          assertEquals(
              "junit/state/knxswitchingactuator24-gang2c16a2fblindactuator12-gang-2/curtain-4/slat-position",
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

  @Disabled
  @Test
  @DisplayName("Should generate de.matgroe.hassio.types.ClimateHVAC")
  void testHeatingCoolingSwitchable() {
    Optional<GiraOneChannel> channel =
        project.lookupChannelByUrn("urn:gds:chv:KNXheating2Fcooling-Heating-Cooling-Switchable-5");
    fail("not implemented yet");
  }
}
