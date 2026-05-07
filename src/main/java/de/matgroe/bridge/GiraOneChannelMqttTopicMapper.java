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

import de.matgroe.giraone.client.types.GiraOneChannel;
import de.matgroe.giraone.client.types.GiraOneDataPoint;
import de.matgroe.giraone.client.types.GiraOneProject;
import de.matgroe.giraone.client.types.GiraOneURN;
import de.matgroe.util.CaseFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * This class offers functionality to derive a MQTT Topicname from a {@link GiraOneDataPoint} and
 * offers a mapping between the TopicName and the concerning {@link GiraOneDataPoint}
 */
public class GiraOneChannelMqttTopicMapper {
  public static final String COMMAND = "command";
  public static final String STATE = "state";

  private final String statePrefix;
  private final String commandPrefix;
  private final Map<String, GiraOneDataPoint> dataPointTopicMap;
  private final GiraOneProject giraOneProject;

  public GiraOneChannelMqttTopicMapper(String prefix, GiraOneProject giraOneProject) {
    this.dataPointTopicMap = Collections.synchronizedMap(new HashMap<>());
    this.statePrefix = String.format("%s/%s/", prefix, STATE);
    this.commandPrefix = String.format("%s/%s/", prefix, COMMAND);
    this.giraOneProject = giraOneProject;
    this.prepareLookupMap();
  }

  private void prepareLookupMap() {
    this.giraOneProject
        .lookupGiraOneDataPoints()
        .forEach(
            dp -> {
              dataPointTopicMap.put(topicNameOf(dp.getUrn()), dp);
              // System.out.println(String.format("Arguments.of(\"%s\", \"%s\"),", dp,
              // topicNameOf(dp.getUrn())));
            });
  }

  private String formatDatapointChannel(GiraOneURN urn) {
    Optional<GiraOneChannel> optChannel =
        this.giraOneProject.lookupChannelByDataPoint(new GiraOneDataPoint(urn));
    if (optChannel.isPresent()) {
      GiraOneChannel channel = optChannel.get();

      return String.format(
          "%s/%s/%s_%s",
          StringUtils.isNotEmpty(channel.getLocation())
              ? CaseFormatter.makeSnakeCase(channel.getLocation())
              : "nonlocation",
          CaseFormatter.makeSnakeCase(channel.getChannelType().toString()),
          DigestUtils.md5Hex(urn.getParent().toString()).substring(0, 8).toLowerCase(),
          CaseFormatter.makeSnakeCase(optChannel.get().getName()));

    } else {
      String parent = urn.getParent().getResourceName();
      parent = parent.replace('.', '/');
      return parent.toLowerCase();
    }
  }

  private String generateDataPointId(GiraOneURN urn) {
    return urn.getResourceName().toLowerCase();
  }

  /**
   * Creates topicname for the given {@link GiraOneDataPoint} without prefix
   *
   * @param urn The {@link GiraOneURN}
   * @return returns a topicname in format of {channel in snake case}/{datapointId}
   */
  public String topicNameOf(GiraOneURN urn) {
    return String.format("%s/%s", formatDatapointChannel(urn), generateDataPointId(urn));
  }

  /**
   * Creates a state topicname for the given {@link GiraOneDataPoint}. The
   *
   * @param dataPoint The {@link GiraOneDataPoint}
   * @return returns a topicname in format of {prefix}/state/{topicName}
   */
  public String stateTopicNameOf(GiraOneDataPoint dataPoint) {
    return stateTopicNameOf(dataPoint.getUrn());
  }

  /**
   * Creates a state topicname for the given {@link GiraOneDataPoint}. The
   *
   * @param urn The {@link GiraOneURN}
   * @return returns a topicname in format of {prefix}/state/{topicName}
   */
  public String stateTopicNameOf(GiraOneURN urn) {
    return statePrefix + topicNameOf(urn);
  }

  /**
   * Creates a state topicname for the given {@link GiraOneDataPoint}. The
   *
   * @param dataPoint The {@link GiraOneDataPoint}
   * @return returns a topicname in format of {prefix}/command/{topicName}
   */
  public String commandTopicNameOf(GiraOneDataPoint dataPoint) {
    return commandTopicNameOf(dataPoint.getUrn());
  }

  /**
   * Creates a state topicname for the given {@link GiraOneDataPoint}. The
   *
   * @param urn The {@link GiraOneURN}
   * @return returns a topicname in format of {prefix}/command/{topicName}
   */
  public String commandTopicNameOf(GiraOneURN urn) {
    return commandPrefix + topicNameOf(urn);
  }

  /**
   * @return returns a Optional of {@link GiraOneDataPoint} for the given topicname.
   */
  public Optional<GiraOneDataPoint> giraOneDataPointOf(String topic) {
    GiraOneDataPoint dp =
        this.dataPointTopicMap.get(
            topic.replace(this.commandPrefix, "").replace(this.statePrefix, ""));
    return dp != null ? Optional.of(dp) : Optional.empty();
  }
}
