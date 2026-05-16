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
package de.matgroe.bridge.translators.mqtt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.matgroe.bridge.GiraOneChannelMqttTopicMapper;
import de.matgroe.giraone.GiraOneTestDataProvider;
import de.matgroe.giraone.client.types.GiraOneProject;
import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.mqtt.MqttMessage;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MqttMessageTranslatorsTest {

  MqttTranslatorFactory transformer;

  @BeforeEach
  void init() {
    GiraOneProject project = GiraOneTestDataProvider.createGiraOneProject();

    transformer =
        new MqttTranslatorFactory(new GiraOneChannelMqttTopicMapper("g1-junit", project), project);
  }

  private static Stream<Arguments> provideDatapointUrntoStrategy() {
    return Stream.of(
        Arguments.of(
            "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxSwitchingActuator24-gang2C16A2FBlindActuator12-gang-1.Curtain-2:Up-Down",
            "g1-junit/state/buro/covering/d5a8f603_buro_raffstore_eckfenster_tur/up-down",
            MqttMessageCoveringTranslator.class),
        Arguments.of(
            "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxHvacActuator6-gang-2.Heatingactuator-1:Heating",
            "g1-junit/state/diele/heating/bbecb629_diele_heizung/heating",
            MqttMessageHeatingTranslator.class),
        Arguments.of(
            "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxButton4Comfort2CSystem55Rocker3-gang-13.Dimming-1:OnOff",
            "ankleide/trigger/dbdf5f47_ankleide_taster_dimmen_1/onoff",
            MqttMessageDefaultTranslator.class));
  }

  @DisplayName("should derive MqttMessageTranslator from topic and GiraOneDataPoint")
  @ParameterizedTest
  @MethodSource("provideDatapointUrntoStrategy")
  void testUrnStrategyMapping(String datapointUrn, String topicName, Class<?> expected) {
    MqttMessage mqtt = new MqttMessage(topicName, "X");
    GiraOneValue g1Value = new GiraOneValue(datapointUrn, "1");
    assertInstanceOf(expected, transformer.from(mqtt));
  }

  @Test
  @DisplayName("should transform default MqttMessage to GiraOneValue")
  void transformDefaultMqttMessageToGiraOneValue() {
    MqttMessage m =
        new MqttMessage(
            "g1-junit/command/gast/covering/af40fdc5_gast_luftung_dachfenster/movement", "X");
    List<GiraOneValue> list = transformer.from(m).toGiraOneValue();
    assertFalse(list.isEmpty());
    assertEquals("X", list.getFirst().getValue());
    assertEquals(
        "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxSwitchingActuator16-gang2C16A2FBlindActuator8-gang-1.Curtain-4:Movement",
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
}
