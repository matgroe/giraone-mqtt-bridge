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

import de.matgroe.bridge.GiraOneChannelMqttTopicMapper;
import de.matgroe.giraone.client.types.GiraOneChannel;
import de.matgroe.giraone.client.types.GiraOneChannelType;
import de.matgroe.giraone.client.types.GiraOneDataPoint;
import de.matgroe.giraone.client.types.GiraOneProject;
import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.mqtt.MqttMessage;
import java.util.Optional;

/**
 * This Factory creates a {@link MqttMessageTranslator} implementation to convert {@link
 * MqttMessage} into a concerning {@link GiraOneValue}
 *
 * @author Matthias Gröger - Initial contribution
 */
public class MqttTranslatorFactory {
  private final GiraOneChannelMqttTopicMapper giraOneChannelMqttTopicMapper;
  private final GiraOneProject giraOneProject;

  public MqttTranslatorFactory(
      GiraOneChannelMqttTopicMapper giraOneChannelMqttTopicMapper, GiraOneProject giraOneProject) {
    this.giraOneChannelMqttTopicMapper = giraOneChannelMqttTopicMapper;
    this.giraOneProject = giraOneProject;
  }

  public MqttMessageTranslator from(MqttMessage mqttMessage) {
    Optional<GiraOneDataPoint> dp =
        giraOneChannelMqttTopicMapper.giraOneDataPointOf(mqttMessage.topic());
    if (dp.isPresent()) {
      Optional<GiraOneChannel> optChannel = this.giraOneProject.lookupChannelByDataPoint(dp.get());
      if (optChannel.isPresent()) {
        switch (optChannel.get().getChannelType()) {
          case GiraOneChannelType.Covering:
            return new MqttMessageCoveringTranslator(
                giraOneChannelMqttTopicMapper, giraOneProject, mqttMessage);
          case GiraOneChannelType.Heating:
            return new MqttMessageHeatingTranslator(
                giraOneChannelMqttTopicMapper, giraOneProject, mqttMessage);
        }
      }
    }
    return new MqttMessageDefaultTranslator(
        giraOneChannelMqttTopicMapper, giraOneProject, mqttMessage);
  }
}
