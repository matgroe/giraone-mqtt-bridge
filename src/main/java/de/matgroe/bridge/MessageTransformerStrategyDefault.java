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
import lombok.AllArgsConstructor;

/**
 * This strategy is reponsible for converting between {@link MqttMessage} and {@link GiraOneValue}
 * devices.
 *
 * @author Matthias Gröger - Initial contribution
 */
@AllArgsConstructor
class MessageTransformerStrategyDefault<T> implements MessageTransformerStrategy {
  protected final GiraOneChannelMqttTopicMapper giraOneChannelMqttTopicMapper;
  protected final GiraOneProject giraOneProject;
  protected final T message;

  @Override
  public Optional<GiraOneValue> toGiraOneValue() {
    if (message instanceof MqttMessage mqttMessage) {
      Optional<GiraOneDataPoint> dp =
          giraOneChannelMqttTopicMapper.giraOneDataPointOf(mqttMessage.topic());
      if (dp.isPresent()) {
        return Optional.of(new GiraOneValue(dp.get().getUrn(), mqttMessage.payload()));
      }
    } else if (message instanceof GiraOneValue giraOneValue) {
      return Optional.of(giraOneValue);
    }
    return Optional.empty();
  }

  @Override
  public Optional<MqttMessage> toMqttMessage() {
    if (message instanceof GiraOneValue giraOneValue) {
      String topic =
          giraOneChannelMqttTopicMapper.stateTopicNameOf(giraOneValue.getGiraOneDataPoint());
      return Optional.of(new MqttMessage(topic, giraOneValue.getValue()));
    } else if (message instanceof MqttMessage mqttMessage) {
      return Optional.of(mqttMessage);
    }
    return Optional.empty();
  }
}
