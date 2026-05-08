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
package de.matgroe.mqtt;

import com.hivemq.client.mqtt.datatypes.MqttTopic;
import de.matgroe.bridge.GiraOneMqttBridge;
import java.util.UUID;

/**
 * This record decribes the intemediate message format between {@link GiraOneMqttBridge} and {@link
 * MqttClient}
 */
public record MqttMessage(String topic, String payload, String messageId, long expiresAfterMs) {

  public static final long EXPIRE_NEVER = -1;
  public static final long EXPIRE_AFTER_ONE_MINUTE = 1000 * 60;
  public static final long EXPIRE_AFTER_ONE_HOUR = EXPIRE_AFTER_ONE_MINUTE * 60;
  public static final long EXPIRE_AFTER_ONE_DAY = EXPIRE_AFTER_ONE_HOUR * 24;

  public MqttMessage(String topic, String payload, long expiresAfterMs) {
    this(topic, payload, UUID.randomUUID().toString(), expiresAfterMs);
  }

  public MqttMessage(String topic, String payload) {
    this(topic, payload, UUID.randomUUID().toString(), EXPIRE_NEVER);
  }

  public MqttMessage(MqttTopic topic, String payload) {
    this(topic.toString(), payload);
  }

  @Override
  public String toString() {
    if (payload == null) {
      return String.format("MqttMessage{%s@%s : '-> NO_PAYLOAD <-' }", messageId, topic);
    }
    return String.format("MqttMessage{%s@%s : '%s' }", messageId, topic, payload);
  }
}
