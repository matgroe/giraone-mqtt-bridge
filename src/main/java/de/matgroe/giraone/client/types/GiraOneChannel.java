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
package de.matgroe.giraone.client.types;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * GiraOneChannel
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneChannel {
    private GiraOneURN urn;
    private String name;
    private String location;

    private GiraOneFunctionType functionType;
    private GiraOneChannelType channelType;

    private GiraOneChannelTypeId channelTypeId;

    private Set<GiraOneDataPoint> dataPoints = Collections.synchronizedSet(new HashSet<>());

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setUrn(String urn) {
        this.urn = GiraOneURN.of(urn);
    }

    public String getUrn() {
        return urn.toString();
    }

    public void setFunctionType(GiraOneFunctionType functionType) {
        this.functionType = functionType;
    }

    public GiraOneFunctionType getFunctionType() {
        return functionType;
    }

    public void setChannelType(GiraOneChannelType channelType) {
        this.channelType = channelType;
    }

    public GiraOneChannelType getChannelType() {
        return channelType;
    }

    public void setChannelTypeId(GiraOneChannelTypeId channelTypeId) {
        this.channelTypeId = channelTypeId;
    }

    public GiraOneChannelTypeId getChannelTypeId() {
        return channelTypeId;
    }

    public boolean containsGiraOneDataPoint(GiraOneURN datapointUrn) {
        return this.dataPoints.stream().anyMatch(f -> datapointUrn.equals(f.getUrn()));
    }

    public Collection<GiraOneDataPoint> getDataPoints() {
        return dataPoints;
    }

    public void addDataPoint(GiraOneDataPoint dataPoints) {
        this.getDataPoints().add(dataPoints);
    }

    public Optional<GiraOneDataPoint> getDatapoint(String resource) {
        return this.getDataPoints().stream().filter(dp -> dp.getUrn().getResourceName().equals(resource)).findFirst();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof GiraOneChannel that) {
            return Objects.equals(urn, that.urn);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(urn);
    }

    @Override
    public String toString() {
        return String.format(
                "%s{urn='%s', name='%s', location='%s', functionType=%s, channelType=%s, channelTypeId=%s, dataPoints=%s}",
                getClass().getSimpleName(), urn, name, location, functionType, channelType, channelTypeId,
                dataPoints.stream().map(GiraOneDataPoint::toString).toList());
    }
}
