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

import static de.matgroe.Constants.DATAPOINT_LOCALTIME;
import static de.matgroe.Constants.DATAPOINT_UPTIME;

import de.matgroe.giraone.client.types.GiraOneProject;
import de.matgroe.giraone.client.types.GiraOneURN;
import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.mqtt.MqttMessage;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;

/**
 * This strategy is reponsible for apllying special needs on converting between {@link MqttMessage}
 * and {@link GiraOneValue} for internal GDS channels.
 *
 * @author Matthias Gröger - Initial contribution
 */
@Slf4j
class MessageTransformerStrategyInternalDiagnostics<T>
    extends MessageTransformerStrategyDefault<T> {

  public MessageTransformerStrategyInternalDiagnostics(
      GiraOneChannelMqttTopicMapper giraOneChannelMqttTopicMapper,
      GiraOneProject giraOneProject,
      T message) {
    super(giraOneChannelMqttTopicMapper, giraOneProject, message);
  }

  private String makeISO8601(String g1Value) throws ParseException {
    DateFormat from = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    DateFormat to = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    return to.format(from.parse(g1Value));
  }

  @Override
  public List<MqttMessage> toMqttMessage() {
    List<MqttMessage> list = new ArrayList<>();
    if (message instanceof GiraOneValue g1Value) {
      GiraOneURN urn = GiraOneURN.of(g1Value.getDatapointUrn());
      String topic = giraOneChannelMqttTopicMapper.stateTopicNameOf(urn);
      try {
        if (DATAPOINT_LOCALTIME.equals(urn.getResourceName())) {
          list.add(new MqttMessage(topic, makeISO8601(g1Value.getValue())));
        } else if (DATAPOINT_UPTIME.equals(urn.getResourceName())) {
          list.add(new MqttMessage(topic, LocalDateTime.now().toString()));
        }
      } catch (Exception exp) {
        log.error("Cannot convert {} to ISO8601", g1Value.getValue(), exp);
      }
    } else {
      list.addAll(super.toMqttMessage());
    }
    return list;
  }

  public List<GiraOneValue> toGiraOneValue() {
    // readonly
    return List.of();
  }
}
