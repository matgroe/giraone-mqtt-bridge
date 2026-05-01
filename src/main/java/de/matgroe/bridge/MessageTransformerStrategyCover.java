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

import de.matgroe.giraone.client.types.GiraOneDataPoint;
import de.matgroe.giraone.client.types.GiraOneProject;
import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.mqtt.MqttMessage;
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
  public Optional<GiraOneValue> toGiraOneValue() {
    Optional<GiraOneValue> giraOneValue = Optional.empty();
    if (message instanceof MqttMessage mqttMessage) {
      Optional<GiraOneDataPoint> dataPoint =
          giraOneChannelMqttTopicMapper.giraOneDataPointOf(mqttMessage.topic());
      if (dataPoint.isPresent()) {
        if (mqttMessage.payload().startsWith(MAP_DATAPOINT)) {
          String[] parts = mqttMessage.payload().split(":");
          if (parts.length == 4) {
            GiraOneDataPoint mapped =
                new GiraOneDataPoint(
                    dataPoint.get().getUrn().toString().replace(parts[1], parts[2]));
            giraOneValue = Optional.of(new GiraOneValue(mapped, parts[3]));
          }
        } else {
          giraOneValue = Optional.of(new GiraOneValue(dataPoint.get(), mqttMessage.payload()));
        }
      }
    }
    return giraOneValue;
  }
}
