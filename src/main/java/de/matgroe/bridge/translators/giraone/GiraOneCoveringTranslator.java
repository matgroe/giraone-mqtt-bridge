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

import static de.matgroe.Constants.DATAPOINT_MOVEMENT;
import static de.matgroe.Constants.DATAPOINT_POSITION;
import static de.matgroe.Constants.DATAPOINT_STEP_UP_DOWN;
import static de.matgroe.Constants.DATAPOINT_UP_DOWN;

import de.matgroe.bridge.GiraOneChannelMqttTopicMapper;
import de.matgroe.giraone.client.types.GiraOneProject;
import de.matgroe.giraone.client.types.GiraOneURN;
import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.giraone.client.types.GiraOneValueChange;
import de.matgroe.hassio.types.Cover;
import de.matgroe.mqtt.MqttMessage;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * This {@link GiraOneValueTranslator} is reponsible for aplplying special needs on converting from
 * {@link GiraOneValue} to concerning {@link MqttMessage} for Cover/Shutter/Window devices.
 *
 * @author Matthias Gröger - Initial contribution
 */
@Slf4j
class GiraOneCoveringTranslator extends GiraOneDefaultTranslator {

  GiraOneCoveringTranslator(
      GiraOneChannelMqttTopicMapper giraOneChannelMqttTopicMapper,
      GiraOneProject giraOneProject,
      GiraOneValue giraOneValue) {
    super(giraOneChannelMqttTopicMapper, giraOneProject, giraOneValue);
  }

  @Override
  public List<MqttMessage> toMqttMessage() {
    List<MqttMessage> list = new ArrayList<>();
    if (giraOneValue instanceof GiraOneValueChange valueChange) {
      GiraOneURN srcUrn = GiraOneURN.of(valueChange.getDatapointUrn());
      GiraOneURN dstUrn = srcUrn.makeSibling(DATAPOINT_UP_DOWN);
      String topic = giraOneChannelMqttTopicMapper.stateTopicNameOf(dstUrn);
      String values = String.format("%s%s", valueChange.getPreviousValue(), valueChange.getValue());
      if (DATAPOINT_STEP_UP_DOWN.equals(srcUrn.getResourceName())
          || DATAPOINT_UP_DOWN.equals(srcUrn.getResourceName())) {
        switch (values) {
          case "01" -> list.add(new MqttMessage(topic, Cover.STATE_STOPPED));
          case "10" -> list.add(new MqttMessage(topic, Cover.STATE_OPENING));
          case "11" -> list.add(new MqttMessage(topic, Cover.STATE_CLOSING));
        }
      } else if (DATAPOINT_MOVEMENT.equals(srcUrn.getResourceName())) {
        if ("10".equals(values)) {
          list.add(new MqttMessage(topic, Cover.STATE_STOPPED));
        }
      } else if (DATAPOINT_POSITION.equals(srcUrn.getResourceName())) {
        list.add(
            new MqttMessage(
                topic,
                valueChange.isValueIncreasing() ? Cover.STATE_CLOSING : Cover.STATE_OPENING));
      } else {
        list.addAll(super.toMqttMessage());
      }
      return list;
    } else {
      return super.toMqttMessage();
    }
  }
}
