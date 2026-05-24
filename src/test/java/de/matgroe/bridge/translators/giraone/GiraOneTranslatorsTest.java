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
package de.matgroe.bridge.translators.giraone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import de.matgroe.bridge.GiraOneChannelMqttTopicMapper;
import de.matgroe.giraone.GiraOneTestDataProvider;
import de.matgroe.giraone.client.types.GiraOneProject;
import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.giraone.client.types.GiraOneValueChange;
import de.matgroe.hassio.types.Cover;
import de.matgroe.mqtt.MqttMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class GiraOneTranslatorsTest {

  GiraOneTranslatorFactory translatorFactory;

  @BeforeEach
  void init() {
    GiraOneProject project = GiraOneTestDataProvider.createGiraOneProject();

    translatorFactory =
        new GiraOneTranslatorFactory(
            new GiraOneChannelMqttTopicMapper("g1-junit", project), project);
  }

  private static Stream<Arguments> provideDatapointUrntoStrategy() {
    return Stream.of(
        Arguments.of(
            "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxSwitchingActuator24-gang2C16A2FBlindActuator12-gang-1.Curtain-2:Up-Down",
            "g1-junit/state/buro/covering/d5a8f603_buro_raffstore_eckfenster_tur/up-down",
            GiraOneCoveringTranslator.class),
        Arguments.of(
            "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxHvacActuator6-gang-2.Heatingactuator-1:Heating",
            "g1-junit/state/diele/heating/bbecb629_diele_heizung/heating",
            GiraOneHeatingTanslator.class),
        Arguments.of(
            "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxButton4Comfort2CSystem55Rocker3-gang-13.Dimming-1:OnOff",
            "ankleide/trigger/dbdf5f47_ankleide_taster_dimmen_1/onoff",
            GiraOneDefaultTranslator.class));
  }

  @DisplayName("should derive MqttMessageTranslator from topic and GiraOneDataPoint")
  @ParameterizedTest
  @MethodSource("provideDatapointUrntoStrategy")
  void testUrnStrategyMapping(String datapointUrn, String topicName, Class<?> expected) {
    MqttMessage mqtt = new MqttMessage(topicName, "X");
    GiraOneValue g1Value = new GiraOneValue(datapointUrn, "1");
    assertInstanceOf(expected, translatorFactory.from(g1Value));
  }

  @Test
  @DisplayName("should transform default GiraOneValue to MqttMessage")
  void transformDefaultGiraOneValueToMqttMessage() {
    GiraOneValue v =
        new GiraOneValue(
            "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxDimmingActuator4-gang-1.DimmingActuator-4:OnOff",
            "1");
    List<MqttMessage> list = translatorFactory.from(v).toMqttMessage();
    assertFalse(list.isEmpty());
    assertEquals("1", list.getFirst().payload());
    assertEquals(
        "g1-junit/state/kuche/dimmer/1a0f7ac2_kuche_dimmen_geratewand/onoff",
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
    List<MqttMessage> list = translatorFactory.from(change).toMqttMessage();
    assertEquals(expectedMessages, list.size());
    if (expectedMessages > 0) {
      assertEquals(
          "g1-junit/state/buro/covering/0d237ef6_buro_raffstore_terasse/up-down",
          list.getFirst().topic());
      assertEquals(expectedState, list.getFirst().payload());
    }
  }

  @Test
  @DisplayName("should transform local time to time with timezone")
  void transformLocalTime() {
    GiraOneValueChange change =
        new GiraOneValueChange(
            "urn:gds:dp:GiraOneServer.GIOSRVKX03:GDS-Device-Channel:Local-Time",
            "2026-05-07 10:24:12",
            "2026-05-07 10:23:00");
    List<MqttMessage> list = translatorFactory.from(change).toMqttMessage();
    assertEquals(1, list.size());
    assertEquals(
        "g1-junit/state/giraone_mqtt_bridge/diagnostic/68862d0d_gira_one_server_zeit/local-time",
        list.getFirst().topic());
    assertEquals("2026-05-07T08:24:12Z", list.getFirst().payload());
  }

  @Test
  @DisplayName("should transform local time to time with timezone")
  void transformUptime() {
    GiraOneValue change = new GiraOneValue("urn:de:matgroe:giraone-bridge:Uptime", "now");
    List<MqttMessage> list = translatorFactory.from(change).toMqttMessage();
    assertEquals(1, list.size());
    assertEquals(
        "g1-junit/state/giraone_mqtt_bridge/diagnostic/50ece54b_bridge_uptime/uptime",
        list.getFirst().topic());
  }
}
