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

import de.matgroe.giraone.client.types.GiraOneChannel;
import de.matgroe.giraone.client.types.GiraOneChannelType;
import de.matgroe.giraone.client.types.GiraOneDataPoint;
import de.matgroe.giraone.client.types.GiraOneProject;
import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.mqtt.MqttMessage;
import java.util.Optional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MessageTransformer {
  private final GiraOneChannelMqttTopicMapper giraOneChannelMqttTopicMapper;
  private final GiraOneProject giraOneProject;

  public MessageTransformerStrategy from(GiraOneValue giraOneValue) {
    Optional<GiraOneChannel> optChannel =
        this.giraOneProject.lookupChannelByDataPoint(giraOneValue.getGiraOneDataPoint());
    if (optChannel.isPresent()) {
      if (optChannel.get().getChannelType() == GiraOneChannelType.Covering) {
        return new MessageTransformerStrategyCover<GiraOneValue>(
            giraOneChannelMqttTopicMapper, giraOneProject, giraOneValue);
      }
      if (optChannel.get().getChannelType() == GiraOneChannelType.Heating) {
        return new MessageTransformerStrategyHVAC<GiraOneValue>(
            giraOneChannelMqttTopicMapper, giraOneProject, giraOneValue);
      }
    }
    return new MessageTransformerStrategyDefault<GiraOneValue>(
        giraOneChannelMqttTopicMapper, giraOneProject, giraOneValue);
  }

  public MessageTransformerStrategy from(MqttMessage mqttMessage) {
    Optional<GiraOneDataPoint> dp =
        giraOneChannelMqttTopicMapper.giraOneDataPointOf(mqttMessage.topic());
    if (dp.isPresent()) {
      Optional<GiraOneChannel> optChannel = this.giraOneProject.lookupChannelByDataPoint(dp.get());
      if (optChannel.isPresent()) {
        if (optChannel.get().getChannelType() == GiraOneChannelType.Covering) {
          return new MessageTransformerStrategyCover<MqttMessage>(
              giraOneChannelMqttTopicMapper, giraOneProject, mqttMessage);
        }
        if (optChannel.get().getChannelType() == GiraOneChannelType.Heating) {
          return new MessageTransformerStrategyHVAC<MqttMessage>(
              giraOneChannelMqttTopicMapper, giraOneProject, mqttMessage);
        }
      }
    }
    return new MessageTransformerStrategyDefault<MqttMessage>(
        giraOneChannelMqttTopicMapper, giraOneProject, mqttMessage);
  }
}
