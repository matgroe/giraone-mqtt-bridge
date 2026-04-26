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
import static org.junit.jupiter.api.Assertions.fail;

import de.matgroe.giraone.GiraOneTestDataProvider;
import de.matgroe.giraone.client.types.GiraOneChannel;
import de.matgroe.giraone.client.types.GiraOneProject;
import de.matgroe.hassio.types.Component;
import de.matgroe.hassio.types.Sensor;
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
        new HassioComponentFactory(new HassioTopicNameMapper("junit", project));
  }

  @Test
  @DisplayName("Should generate mqtt.Sensor for Temperature")
  void testTemperatureStatusChannel() {
    Optional<GiraOneChannel> channel =
        project.lookupChannelByUrn("urn:gds:chv:NumericFloatingPointStatus-Float-16");
    channel.ifPresentOrElse(
        (ch) -> {
          Component component = hassioComponentFactory.from(ch);
          assertInstanceOf(Sensor.class, component);
          assertEquals("temperature", component.getDeviceClass());
        },
        () -> fail("Channel not found in project"));
  }

  @Test
  @DisplayName("Should generate mqtt.Sensor for Humidity")
  void testHumidityStatusChannel() {
    Optional<GiraOneChannel> channel =
        project.lookupChannelByUrn("urn:gds:chv:NumericFloatingPointStatus-Float-1");
    channel.ifPresentOrElse(
        ch -> {
          Component component = hassioComponentFactory.from(ch);
          assertInstanceOf(Sensor.class, component);
          assertEquals("humidity", component.getDeviceClass());
        },
        () -> fail("Channel not found in project"));
  }
}
