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

import static de.matgroe.Contstants.DATAPOINT_MOVEMENT;
import static de.matgroe.Contstants.DATAPOINT_POSITION;
import static de.matgroe.Contstants.DATAPOINT_STEP_UP_DOWN;
import static de.matgroe.Contstants.DATAPOINT_UP_DOWN;

import de.matgroe.giraone.client.types.GiraOneDataPoint;
import de.matgroe.giraone.client.types.GiraOneProject;
import de.matgroe.giraone.client.types.GiraOneURN;
import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.giraone.client.types.GiraOneValueChange;
import de.matgroe.hassio.types.Cover;
import de.matgroe.mqtt.MqttMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * This strategy is reponsible for apllying special needs on converting between {@link MqttMessage}
 * and {@link GiraOneValue} for Cover/Shutter/Window devices.
 *
 * @author Matthias Gröger - Initial contribution
 */
@Slf4j
class MessageTransformerStrategyCover<T> extends MessageTransformerStrategyDefault<T> {
  public static final String MAP_DATAPOINT = "#MAP-DATAPOINT#";

  public MessageTransformerStrategyCover(
      GiraOneChannelMqttTopicMapper giraOneChannelMqttTopicMapper,
      GiraOneProject giraOneProject,
      T message) {
    super(giraOneChannelMqttTopicMapper, giraOneProject, message);
  }

  @Override
  public List<MqttMessage> toMqttMessage() {
    List<MqttMessage> list = new ArrayList<>();
    if (message instanceof GiraOneValueChange valueChange) {
      GiraOneURN srcUrn = GiraOneURN.of(valueChange.getDatapointUrn());
      GiraOneURN dstUrn = srcUrn.makeSibling(DATAPOINT_UP_DOWN);
      String topic = giraOneChannelMqttTopicMapper.stateTopicNameOf(dstUrn);
      String values = String.format("%s%s", valueChange.getPreviousValue(), valueChange.getValue());
      if (DATAPOINT_STEP_UP_DOWN.equals(srcUrn.getResourceName())) {
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

  public List<GiraOneValue> toGiraOneValue() {
    List<GiraOneValue> list = new ArrayList<>();
    if (message instanceof MqttMessage mqttMessage) {
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
    }
    return list;
  }
}
