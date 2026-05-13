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
