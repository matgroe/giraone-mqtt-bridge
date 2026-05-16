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

import static de.matgroe.Constants.DATAPOINT_STEP_UP_DOWN;
import static de.matgroe.Constants.DATAPOINT_UP_DOWN;

import de.matgroe.bridge.GiraOneChannelMqttTopicMapper;
import de.matgroe.giraone.client.types.GiraOneDataPoint;
import de.matgroe.giraone.client.types.GiraOneProject;
import de.matgroe.giraone.client.types.GiraOneURN;
import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.hassio.types.Cover;
import de.matgroe.mqtt.MqttMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * This {@link MqttMessageTranslator} is reponsible for aplplying special needs on converting from
 * {@link MqttMessage} to concerning {@link GiraOneValue} messages for Cover/Shutter/Window devices.
 *
 * @author Matthias Gröger - Initial contribution
 */
@Slf4j
class MqttMessageCoveringTranslator extends MqttMessageDefaultTranslator {
  public MqttMessageCoveringTranslator(
      GiraOneChannelMqttTopicMapper giraOneChannelMqttTopicMapper,
      GiraOneProject giraOneProject,
      MqttMessage mqttMessage) {
    super(giraOneChannelMqttTopicMapper, giraOneProject, mqttMessage);
  }

  public List<GiraOneValue> toGiraOneValue() {
    List<GiraOneValue> list = new ArrayList<>();
    Optional<GiraOneDataPoint> dataPoint =
        giraOneChannelMqttTopicMapper.giraOneDataPointOf(mqttMessage.topic());
    if (dataPoint.isPresent()) {
      GiraOneURN urn = dataPoint.get().getUrn();
      if (DATAPOINT_UP_DOWN.equals(urn.getResourceName())) {
        switch (mqttMessage.payload()) {
          case Cover.PAYLOAD_CLOSE -> list.add(new GiraOneValue(urn, "1"));
          case Cover.PAYLOAD_OPEN -> list.add(new GiraOneValue(urn, "0"));
          case Cover.PAYLOAD_STOP ->
              list.add(new GiraOneValue(urn.makeSibling(DATAPOINT_STEP_UP_DOWN), "0"));
        }
      } else {
        list.addAll(super.toGiraOneValue());
      }
    }
    return list;
  }
}
