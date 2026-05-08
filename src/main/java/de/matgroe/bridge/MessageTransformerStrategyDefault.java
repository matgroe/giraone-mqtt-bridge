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

import de.matgroe.giraone.client.types.GiraOneDataPoint;
import de.matgroe.giraone.client.types.GiraOneProject;
import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.mqtt.MqttMessage;
import java.util.List;
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
  public List<GiraOneValue> toGiraOneValue() {
    if (message instanceof MqttMessage mqttMessage) {
      Optional<GiraOneDataPoint> dp =
          giraOneChannelMqttTopicMapper.giraOneDataPointOf(mqttMessage.topic());
      if (dp.isPresent()) {
        return List.of(new GiraOneValue(dp.get().getUrn(), mqttMessage.payload()));
      }
    } else if (message instanceof GiraOneValue giraOneValue) {
      return List.of(giraOneValue);
    }
    return List.of();
  }

  @Override
  public List<MqttMessage> toMqttMessage() {
    if (message instanceof GiraOneValue giraOneValue) {
      String topic =
          giraOneChannelMqttTopicMapper.stateTopicNameOf(giraOneValue.getGiraOneDataPoint());
      return List.of(new MqttMessage(topic, giraOneValue.getValue()));
    } else if (message instanceof MqttMessage mqttMessage) {
      return List.of(mqttMessage);
    }
    return List.of();
  }
}
