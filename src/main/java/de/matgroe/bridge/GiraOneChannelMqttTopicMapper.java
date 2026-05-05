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

import de.matgroe.giraone.client.types.GiraOneDataPoint;
import de.matgroe.giraone.client.types.GiraOneProject;
import de.matgroe.giraone.client.types.GiraOneURN;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class offers functionality to derive a MQTT Topicname from a {@link GiraOneDataPoint} and
 * offers a mapping between the TopicName and the concerning {@link GiraOneDataPoint}
 */
public class GiraOneChannelMqttTopicMapper {
  public static final String COMMAND = "command";
  public static final String STATE = "state";

  private final String prefix;
  private Map<String, GiraOneDataPoint> dataPointTopicMap;
  private final GiraOneProject giraOneProject;

  public GiraOneChannelMqttTopicMapper(String prefix, GiraOneProject giraOneProject) {
    this.dataPointTopicMap = Collections.synchronizedMap(new HashMap<>());
    this.prefix = prefix;
    this.giraOneProject = giraOneProject;

    // prepare lookup map
    this.giraOneProject
        .lookupGiraOneDataPoints()
        .forEach(
            dp -> {
              dataPointTopicMap.put(stateTopicNameOf(dp), dp);
              dataPointTopicMap.put(commandTopicNameOf(dp), dp);
            });
  }

  private String formatDatapointChannel(GiraOneURN urn) {
    String parent = urn.getParent().getResourceName();
    parent = parent.replace('.', '/');
    return parent.toLowerCase();
  }

  private String generateDataPointId(GiraOneURN urn) {
    return urn.getResourceName().toLowerCase();
  }

  /**
   * Creates a state topicname for the given {@link GiraOneDataPoint}. The
   *
   * @param dataPoint The {@link GiraOneDataPoint}
   * @return returns a topicname in format of {prefix}/{channel}/{datapointId}
   */
  public String stateTopicNameOf(GiraOneDataPoint dataPoint) {
    return stateTopicNameOf(dataPoint.getUrn());
  }

  /**
   * Creates a state topicname for the given {@link GiraOneDataPoint}. The
   *
   * @param urn The {@link GiraOneURN}
   * @return returns a topicname in format of {prefix}/{channel}/{datapointId}
   */
  public String stateTopicNameOf(GiraOneURN urn) {
    return String.format(
        "%s/%s/%s/%s", prefix, STATE, formatDatapointChannel(urn), generateDataPointId(urn));
  }

  /**
   * Creates a state topicname for the given {@link GiraOneDataPoint}. The
   *
   * @param dataPoint The {@link GiraOneDataPoint}
   * @return returns a topicname in format of {prefix}/{channel}/{datapointId}
   */
  public String commandTopicNameOf(GiraOneDataPoint dataPoint) {
    return commandTopicNameOf(dataPoint.getUrn());
  }

  /**
   * Creates a state topicname for the given {@link GiraOneDataPoint}. The
   *
   * @param urn The {@link GiraOneURN}
   * @return returns a topicname in format of {prefix}/{channel}/{datapointId}
   */
  public String commandTopicNameOf(GiraOneURN urn) {
    return String.format(
        "%s/%s/%s/%s", prefix, COMMAND, formatDatapointChannel(urn), generateDataPointId(urn));
  }

  /**
   * Creates a topicname for the given {@link GiraOneDataPoint}. The
   *
   * @return returns a topicname in format of {prefix}/{channel}/{datapointId}
   */
  public Optional<GiraOneDataPoint> giraOneDataPointOf(String topic) {
    GiraOneDataPoint dp = this.dataPointTopicMap.get(topic);
    return dp != null ? Optional.of(dp) : Optional.empty();
  }
}
