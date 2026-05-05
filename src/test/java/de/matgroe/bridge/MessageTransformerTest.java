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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.matgroe.giraone.GiraOneTestDataProvider;
import de.matgroe.giraone.client.types.GiraOneProject;
import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.giraone.client.types.GiraOneValueChange;
import de.matgroe.hassio.types.Cover;
import de.matgroe.mqtt.MqttMessage;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MessageTransformerTest {

  MessageTransformer transformer;

  @BeforeEach
  void init() {
    GiraOneProject project = GiraOneTestDataProvider.createGiraOneProject();
    transformer =
        new MessageTransformer(new GiraOneChannelMqttTopicMapper("g1-junit", project), project);
  }

  private static Stream<Arguments> provideDatapointUrntoStrategy() {
    return Stream.of(
        Arguments.of(
            "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxSwitchingActuator24-gang2C16A2FBlindActuator12-gang-1.Curtain-2:Up-Down",
            "g1-junit/state/knxswitchingactuator24-gang2c16a2fblindactuator12-gang-1/curtain-2/up-down",
            MessageTransformerStrategyCover.class),
        Arguments.of(
            "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxHvacActuator6-gang-2.Heatingactuator-1:Heating",
            "g1-junit/state/knxhvacactuator6-gang-2/heatingactuator-1/heating",
            MessageTransformerStrategyHVAC.class),
        Arguments.of(
            "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxButton4Comfort2CSystem55Rocker3-gang-13.Dimming-1:OnOff",
            "g1-junit/state/knxbutton4comfort2csystem55rocker3-gang-13/dimming-1/onoff",
            MessageTransformerStrategyDefault.class));
  }

  @DisplayName("should derive MessageTransformerStrategy from topic and GiraOneDataPoint")
  @ParameterizedTest
  @MethodSource("provideDatapointUrntoStrategy")
  void testUrnStrategyMapping(String datapointUrn, String topicName, Class<?> expected) {
    MqttMessage mqtt = new MqttMessage(topicName, "X");
    GiraOneValue g1Value = new GiraOneValue(datapointUrn, "1");

    assertInstanceOf(expected, transformer.from(mqtt));
    assertInstanceOf(expected, transformer.from(g1Value));
  }

  @Test
  @DisplayName("should transform default MqttMessage to GiraOneValue")
  void transformDefaultMqttMessageToGiraOneValue() {
    MqttMessage m =
        new MqttMessage("g1-junit/state/knxdimmingactuator4-gang-1/dimmingactuator-4/onoff", "X");
    List<GiraOneValue> list = transformer.from(m).toGiraOneValue();
    assertFalse(list.isEmpty());
    assertEquals("X", list.getFirst().getValue());
    assertEquals(
        "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxDimmingActuator4-gang-1.DimmingActuator-4:OnOff",
        list.getFirst().getDatapointUrn());
  }

  @Test
  @DisplayName("should not transform default MqttMessage to GiraOneValue")
  void dontTransformDefaultMqttMessageToGiraOneValue() {
    MqttMessage m =
        new MqttMessage(
            "g1-junit/state/knxdimmingactuator4-gang-1/dimmingactuator-4/onoffxxx", "X");
    List<GiraOneValue> list = transformer.from(m).toGiraOneValue();
    assertTrue(list.isEmpty());
  }

  @Test
  @DisplayName("should transform default GiraOneValue to MqttMessage")
  void transformDefaultGiraOneValueToMqttMessage() {
    GiraOneValue v =
        new GiraOneValue(
            "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxDimmingActuator4-gang-1.DimmingActuator-4:OnOff",
            "1");
    List<MqttMessage> list = transformer.from(v).toMqttMessage();
    assertFalse(list.isEmpty());
    assertEquals("1", list.getFirst().payload());
    assertEquals(
        "g1-junit/state/knxdimmingactuator4-gang-1/dimmingactuator-4/onoff",
        list.getFirst().topic());
  }

  private static Stream<Arguments> provideStepUpDown() {
    return Stream.of(
        Arguments.of(
            "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxSwitchingActuator24-gang2C16A2FBlindActuator12-gang-1.Curtain-4:Step-Up-Down",
            "0",
            "1",
            1,
            Cover.STATE_STOPPED),
        Arguments.of(
            "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxSwitchingActuator24-gang2C16A2FBlindActuator12-gang-1.Curtain-4:Step-Up-Down",
            "1",
            "0",
            1,
            Cover.STATE_OPENING),
        Arguments.of(
            "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxSwitchingActuator24-gang2C16A2FBlindActuator12-gang-1.Curtain-4:Step-Up-Down",
            "1",
            "1",
            1,
            Cover.STATE_CLOSING),
        Arguments.of(
            "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxSwitchingActuator24-gang2C16A2FBlindActuator12-gang-1.Curtain-4:Movement",
            "0",
            "1",
            0,
            Cover.STATE_OPENING),
        Arguments.of(
            "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxSwitchingActuator24-gang2C16A2FBlindActuator12-gang-1.Curtain-4:Movement",
            "1",
            "0",
            1,
            Cover.STATE_STOPPED));
  }

  @ParameterizedTest
  @DisplayName("should transform datapoint urn to Cover moving direction")
  @MethodSource("provideStepUpDown")
  void transformStepUpDownToMovingState(
      String urn, String oldValue, String newValue, int expectedMessages, String expectedState) {
    GiraOneValueChange change = new GiraOneValueChange(urn, newValue, oldValue);
    List<MqttMessage> list = transformer.from(change).toMqttMessage();
    assertEquals(expectedMessages, list.size());
    if (expectedMessages > 0) {
      assertEquals(
          "g1-junit/state/knxswitchingactuator24-gang2c16a2fblindactuator12-gang-1/curtain-4/up-down",
          list.getFirst().topic());
      assertEquals(expectedState, list.getFirst().payload());
    }
  }
}
