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
package de.matgroe.bridge;

import de.matgroe.giraone.client.types.GiraOneChannel;
import de.matgroe.giraone.client.types.GiraOneChannelType;
import de.matgroe.giraone.client.types.GiraOneDataPoint;
import de.matgroe.giraone.client.types.GiraOneProject;
import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.mqtt.MqttMessage;
import java.util.Optional;

/** */
public class MessageTransformer {
  private final GiraOneChannelMqttTopicMapper giraOneChannelMqttTopicMapper;
  private final GiraOneProject giraOneProject;

  public MessageTransformer(
      GiraOneChannelMqttTopicMapper giraOneChannelMqttTopicMapper, GiraOneProject giraOneProject) {
    this.giraOneChannelMqttTopicMapper = giraOneChannelMqttTopicMapper;
    this.giraOneProject = giraOneProject;
  }

  public MessageTransformerStrategy from(GiraOneValue giraOneValue) {
    Optional<GiraOneChannel> optChannel =
        this.giraOneProject.lookupChannelByDataPoint(giraOneValue.getGiraOneDataPoint());
    if (optChannel.isPresent()) {
      switch (optChannel.get().getChannelType()) {
        case GiraOneChannelType.Covering:
          return new MessageTransformerStrategyCover<>(
              giraOneChannelMqttTopicMapper, giraOneProject, giraOneValue);
        case GiraOneChannelType.Heating:
          return new MessageTransformerStrategyHVAC<>(
              giraOneChannelMqttTopicMapper, giraOneProject, giraOneValue);
        case GiraOneChannelType.Diagnostic:
          return new MessageTransformerStrategyInternalDiagnostics<>(
              giraOneChannelMqttTopicMapper, giraOneProject, giraOneValue);
      }
    }
    return new MessageTransformerStrategyDefault<>(
        giraOneChannelMqttTopicMapper, giraOneProject, giraOneValue);
  }

  public MessageTransformerStrategy from(MqttMessage mqttMessage) {
    Optional<GiraOneDataPoint> dp =
        giraOneChannelMqttTopicMapper.giraOneDataPointOf(mqttMessage.topic());
    if (dp.isPresent()) {
      Optional<GiraOneChannel> optChannel = this.giraOneProject.lookupChannelByDataPoint(dp.get());
      if (optChannel.isPresent()) {
        switch (optChannel.get().getChannelType()) {
          case GiraOneChannelType.Covering:
            return new MessageTransformerStrategyCover<>(
                giraOneChannelMqttTopicMapper, giraOneProject, mqttMessage);
          case GiraOneChannelType.Heating:
            return new MessageTransformerStrategyHVAC<>(
                giraOneChannelMqttTopicMapper, giraOneProject, mqttMessage);
          case GiraOneChannelType.Diagnostic:
            return new MessageTransformerStrategyInternalDiagnostics<>(
                giraOneChannelMqttTopicMapper, giraOneProject, mqttMessage);
        }
      }
    }
    return new MessageTransformerStrategyDefault<>(
        giraOneChannelMqttTopicMapper, giraOneProject, mqttMessage);
  }
}
