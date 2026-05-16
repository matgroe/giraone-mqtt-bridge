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

import static de.matgroe.Constants.DATAPOINT_HEATING;
import static de.matgroe.Constants.DATAPOINT_MODE;

import de.matgroe.bridge.GiraOneChannelMqttTopicMapper;
import de.matgroe.giraone.client.types.GiraOneProject;
import de.matgroe.giraone.client.types.GiraOneURN;
import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.hassio.types.ClimateHVAC;
import de.matgroe.mqtt.MqttMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * This {@link GiraOneValueTranslator} is reponsible for aplplying special needs on converting from
 * {@link GiraOneValue} to concerning {@link MqttMessage} for Heating/Climate/HVAC devices.
 *
 * @author Matthias Gröger - Initial contribution
 */
class GiraOneHeatingTanslator extends GiraOneDefaultTranslator {

  GiraOneHeatingTanslator(
      GiraOneChannelMqttTopicMapper giraOneChannelMqttTopicMapper,
      GiraOneProject giraOneProject,
      GiraOneValue giraOneValue) {
    super(giraOneChannelMqttTopicMapper, giraOneProject, giraOneValue);
  }

  @Override
  public List<MqttMessage> toMqttMessage() {
    List<MqttMessage> list = new ArrayList<>();
    GiraOneURN urn = GiraOneURN.of(giraOneValue.getDatapointUrn());
    String topic = giraOneChannelMqttTopicMapper.stateTopicNameOf(urn);
    if (DATAPOINT_MODE.equals(urn.getResourceName())) {
      switch (giraOneValue.getValue()) {
        case "1" -> list.add(new MqttMessage(topic, ClimateHVAC.MODE_COMFORT));
        case "2" -> list.add(new MqttMessage(topic, ClimateHVAC.MODE_STANDBY));
        case "3" -> list.add(new MqttMessage(topic, ClimateHVAC.MODE_NIGHT));
        case "4" -> list.add(new MqttMessage(topic, ClimateHVAC.MODE_FROST_CONTROL));
      }
    } else if (DATAPOINT_HEATING.equals(urn.getResourceName())) {
      switch (giraOneValue.getValue()) {
        case "0" -> list.add(new MqttMessage(topic, ClimateHVAC.MODE_OFF));
        case "1" -> list.add(new MqttMessage(topic, ClimateHVAC.MODE_HEATING));
      }
    } else {
      return super.toMqttMessage();
    }
    return list;
  }
}
