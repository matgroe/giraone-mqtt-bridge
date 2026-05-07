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
