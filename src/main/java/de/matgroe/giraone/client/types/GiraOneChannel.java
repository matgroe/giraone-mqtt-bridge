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
package de.matgroe.giraone.client.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * GiraOneChannel
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneChannel {
  private String name;
  private String location;

  private GiraOneURN urn = GiraOneURN.INVALID;
  private GiraOneFunctionType functionType = GiraOneFunctionType.Unknown;
  private GiraOneChannelType channelType = GiraOneChannelType.Unknown;
  private GiraOneChannelTypeId channelTypeId = GiraOneChannelTypeId.Unknown;
  private final Set<GiraOneDataPoint> dataPoints = Collections.synchronizedSet(new HashSet<>());
  private final List<GiraOneChannelParameter> channelParameter = new ArrayList<>();

  public Collection<GiraOneChannelParameter> getChannelParameter() {
    return channelParameter;
  }

  public void addParameter(GiraOneChannelParameter channelParameter) {
    this.channelParameter.add(channelParameter);
  }

  public Optional<String> getParameterValue(String parameter) {
    return this.channelParameter.stream()
        .filter(p -> p.getKey().equals(parameter))
        .map(GiraOneChannelParameter::getValue)
        .findFirst();
  }

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
    return this.getDataPoints().stream()
        .filter(dp -> dp.getUrn().getResourceName().equals(resource))
        .findFirst();
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
        "%s{urn='%s', name='%s', location='%s', functionType=%s, channelType=%s, channelTypeId=%s, dataPoints=%s, channelParameter=%s}",
        getClass().getSimpleName(),
        urn,
        name,
        location,
        functionType,
        channelType,
        channelTypeId,
        dataPoints.stream().map(GiraOneDataPoint::toString).toList(),
        channelParameter.stream().map(GiraOneChannelParameter::toString).toList());
  }
}
