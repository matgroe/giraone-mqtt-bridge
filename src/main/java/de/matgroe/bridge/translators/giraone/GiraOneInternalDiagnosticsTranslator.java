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
package de.matgroe.bridge.translators.giraone;

import static de.matgroe.Constants.DATAPOINT_LOCALTIME;
import static de.matgroe.Constants.DATAPOINT_UPTIME;

import de.matgroe.bridge.GiraOneChannelMqttTopicMapper;
import de.matgroe.giraone.client.types.GiraOneProject;
import de.matgroe.giraone.client.types.GiraOneURN;
import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.mqtt.MqttMessage;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;

/**
 * This {@link GiraOneValueTranslator} is reponsible for aplplying special needs on converting from
 * {@link GiraOneValue} to concerning {@link MqttMessage} for internal GDS Channels.
 *
 * @author Matthias Gröger - Initial contribution
 */
@Slf4j
class GiraOneInternalDiagnosticsTranslator extends GiraOneDefaultTranslator {
  private static final DateTimeFormatter PARSER =
      DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss", Locale.ROOT);

  GiraOneInternalDiagnosticsTranslator(
      GiraOneChannelMqttTopicMapper giraOneChannelMqttTopicMapper,
      GiraOneProject giraOneProject,
      GiraOneValue giraOneValue) {
    super(giraOneChannelMqttTopicMapper, giraOneProject, giraOneValue);
  }

  private String makeISO8601(String dateString, ZoneId zone) throws ParseException {
    ZonedDateTime dateTime = LocalDateTime.parse(dateString, PARSER).atZone(zone);
    return dateTime.withZoneSameInstant(ZoneOffset.UTC).toString();
  }

  @Override
  public List<MqttMessage> toMqttMessage() {
    ZoneId zone = ZoneId.of("Europe/Berlin");

    List<MqttMessage> list = new ArrayList<>();
    GiraOneURN urn = GiraOneURN.of(giraOneValue.getDatapointUrn());
    String topic = giraOneChannelMqttTopicMapper.stateTopicNameOf(urn);
    try {
      if (DATAPOINT_LOCALTIME.equals(urn.getResourceName())) {
        list.add(new MqttMessage(topic, makeISO8601(giraOneValue.getValue(), zone)));
      } else if (DATAPOINT_UPTIME.equals(urn.getResourceName())) {
        String localDateTime = LocalDateTime.now(zone).format(PARSER);
        list.add(new MqttMessage(topic, makeISO8601(localDateTime, zone)));
      }
    } catch (Exception exp) {
      log.error("Cannot convert {} to ISO8601", giraOneValue.getValue(), exp);
    }
    return list;
  }
}
