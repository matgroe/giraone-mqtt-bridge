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
package de.matgroe.hassio;

import de.matgroe.giraone.client.types.GiraOneDataPoint;
import de.matgroe.giraone.client.types.GiraOneProject;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

/**
 * This class offers functionality to derive a MQTT Topicname from a {@link GiraOneDataPoint}
 * and offers a mapping between the TopiocName and the concerning {@link GiraOneDataPoint}
 */
public class HassioTopicNameMapper {
    private final String prefix;

    private Map<String, GiraOneDataPoint> dataPointTopicMap;

    public HassioTopicNameMapper(String prefix, GiraOneProject giraOneProject) {
        this.dataPointTopicMap = Collections.synchronizedMap(new HashMap<String, GiraOneDataPoint>());
        this.prefix = prefix;
        giraOneProject.lookupGiraOneDataPoints().forEach(dp ->{
            dataPointTopicMap.put(topicNameOf(dp), dp);
        });
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
     * Creates a topicname for the given {@link GiraOneDataPoint}. The
     *
     * @param dataPoint The {@link GiraOneDataPoint}
     * @return returns a topicname in format of {prefix}/{channel}/{datapointId}
     */
    public String topicNameOf(GiraOneDataPoint dataPoint) {
        String topicName =  String.format("%s/%s/%s",
                prefix,
                formatDatapointChannel(dataPoint),
                generateDataPointId(dataPoint)
        );
        return topicName;
    }

    /**
     * Creates a topicname for the given {@link GiraOneDataPoint}. The
     * @return returns a topicname in format of {prefix}/{channel}/{datapointId}
     */
    public Optional<GiraOneDataPoint> giraOneDataPointOf(String topic) {
        GiraOneDataPoint dp = this.dataPointTopicMap.get(topic);
        return dp != null ? Optional.of(dp) : Optional.empty();
    }

}
