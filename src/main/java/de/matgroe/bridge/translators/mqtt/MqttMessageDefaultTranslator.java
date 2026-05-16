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
import de.matgroe.giraone.client.types.GiraOneDataPoint;
import de.matgroe.giraone.client.types.GiraOneProject;
import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.mqtt.MqttMessage;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;

/**
 * This {@link MqttMessageTranslator} is reponsible for aplplying special needs on converting from
 * {@link MqttMessage} to concerning {@link GiraOneValue} messages.
 *
 * @author Matthias Gröger - Initial contribution
 */
@AllArgsConstructor
class MqttMessageDefaultTranslator implements MqttMessageTranslator {
  protected final GiraOneChannelMqttTopicMapper giraOneChannelMqttTopicMapper;
  protected final GiraOneProject giraOneProject;
  protected final MqttMessage mqttMessage;

  public List<GiraOneValue> toGiraOneValue() {
    Optional<GiraOneDataPoint> dp =
        giraOneChannelMqttTopicMapper.giraOneDataPointOf(mqttMessage.topic());
    return dp.map(dataPoint -> List.of(new GiraOneValue(dataPoint.getUrn(), mqttMessage.payload())))
        .orElseGet(List::of);
  }
}
