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

package de.matgroe.mqtt;

import de.matgroe.giraone.client.types.GiraOneDataPoint;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.TreeSet;

/**
 * This class offers functionality to derive a MQTT Topicname from a {@link GiraOneDataPoint}
 * and offers a mapping between the TopiocName and the concerning {@link GiraOneDataPoint} *
 */
public class MqttTopicNameMapper {
    private final String prefix;

    private Collection<ImmutablePair<String, GiraOneDataPoint>> dataPointTopicPairs;

    public MqttTopicNameMapper(String prefix) {
        this.dataPointTopicPairs = Collections.synchronizedSortedSet(new TreeSet<>());
        this.prefix = prefix;
    }

    private String formatDatapointChannel(GiraOneDataPoint dataPoint) {
        String parent = dataPoint.getUrn().getParent().getResourceName();
        parent = parent.replace('.', '/');
        return parent.toLowerCase();
    }

    private String generateDataPointId(GiraOneDataPoint dataPoint) {
        return dataPoint.getUrn().getResourceName().toLowerCase();
    }

    /**
     *
     * Formats Topicname to {prefix}/{location}/{channel}/{datapointId}
     *
     * @param dataPoint
     * @return
     *
     */
    public String topicNameOf(GiraOneDataPoint dataPoint) {
        String topicName =  String.format("%s/%s/%s",
                prefix,
                formatDatapointChannel(dataPoint),
                generateDataPointId(dataPoint)
        );
        //this.dataPointTopicPairs.add(new ImmutablePair<>(topicName, dataPoint));
        return topicName;
    }

    public Optional<GiraOneDataPoint> giraOneDataPointOf(String topic) {
        Optional<ImmutablePair<String, GiraOneDataPoint>> pair = this.dataPointTopicPairs.stream().filter(p -> topic.equals(p.left)).findFirst();
        if (pair.isPresent()) {
            return Optional.of(pair.get().right);
        }
        return Optional.empty();
    }

}
