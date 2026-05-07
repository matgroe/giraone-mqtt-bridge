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

import static de.matgroe.Contstants.DATAPOINT_HEATING;
import static de.matgroe.Contstants.DATAPOINT_MODE;

import de.matgroe.giraone.client.types.GiraOneDataPoint;
import de.matgroe.giraone.client.types.GiraOneProject;
import de.matgroe.giraone.client.types.GiraOneURN;
import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.hassio.types.ClimateHVAC;
import de.matgroe.mqtt.MqttMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This strategy is reponsible for apllying special needs on converting between {@link MqttMessage}
 * and {@link GiraOneValue} for Climate/HVAC devices.
 *
 * @author Matthias Gröger - Initial contribution
 */
class MessageTransformerStrategyHVAC<T> extends MessageTransformerStrategyDefault<T> {

  public MessageTransformerStrategyHVAC(
      GiraOneChannelMqttTopicMapper giraOneChannelMqttTopicMapper,
      GiraOneProject giraOneProject,
      T message) {
    super(giraOneChannelMqttTopicMapper, giraOneProject, message);
  }

  @Override
  public List<MqttMessage> toMqttMessage() {
    List<MqttMessage> list = new ArrayList<>();
    if (message instanceof GiraOneValue g1Value) {
      GiraOneURN urn = GiraOneURN.of(g1Value.getDatapointUrn());
      String topic = giraOneChannelMqttTopicMapper.stateTopicNameOf(urn);
      if (DATAPOINT_MODE.equals(urn.getResourceName())) {
        switch (g1Value.getValue()) {
          case "1" -> list.add(new MqttMessage(topic, ClimateHVAC.MODE_COMFORT));
          case "2" -> list.add(new MqttMessage(topic, ClimateHVAC.MODE_STANDBY));
          case "3" -> list.add(new MqttMessage(topic, ClimateHVAC.MODE_NIGHT));
          case "4" -> list.add(new MqttMessage(topic, ClimateHVAC.MODE_FROST_CONTROL));
        }
      } else if (DATAPOINT_HEATING.equals(urn.getResourceName())) {
        switch (g1Value.getValue()) {
          case "0" -> list.add(new MqttMessage(topic, ClimateHVAC.MODE_OFF));
          case "1" -> list.add(new MqttMessage(topic, ClimateHVAC.MODE_HEATING));
        }
      } else {
        return super.toMqttMessage();
      }
      return list;
    }
    return super.toMqttMessage();
  }

  @Override
  public List<GiraOneValue> toGiraOneValue() {
    List<GiraOneValue> list = new ArrayList<>();
    if (message instanceof MqttMessage mqttMessage) {
      Optional<GiraOneDataPoint> dataPoint =
          giraOneChannelMqttTopicMapper.giraOneDataPointOf(mqttMessage.topic());
      if (dataPoint.isPresent()) {
        GiraOneURN urn = dataPoint.get().getUrn();
        if (DATAPOINT_MODE.equals(urn.getResourceName())) {
          switch (mqttMessage.payload()) {
            case ClimateHVAC.MODE_COMFORT -> list.add(new GiraOneValue(urn, "1"));
            case ClimateHVAC.MODE_STANDBY -> list.add(new GiraOneValue(urn, "2"));
            case ClimateHVAC.MODE_NIGHT -> list.add(new GiraOneValue(urn, "3"));
            case ClimateHVAC.MODE_FROST_CONTROL -> list.add(new GiraOneValue(urn, "4"));
          }
        } else if (DATAPOINT_HEATING.equals(urn.getResourceName())) {
          // DATAPOINT_HEATING is readonly
          return list;
        } else {
          return super.toGiraOneValue();
        }
      } else {
        list.addAll(super.toGiraOneValue());
      }
    }
    return list;
  }
}
