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

import static de.matgroe.Constants.LOCATION_BRIDGE;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * This class represents the project installation within your as configured GiraOne SmartHome
 * Environment. It offers some functions for accessing the {@link GiraOneChannel} and
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneProject {
  private final Set<GiraOneChannel> channels = Collections.synchronizedSet(new HashSet<>());

  public GiraOneProject() {
    addDiagnosticChannel(
        "urn:gds:ch:GiraOneServer.GIOSRVKX03:GDS-Device-Channel",
        "GiraOneServer Zeit",
        "urn:gds:dp:GiraOneServer.GIOSRVKX03:GDS-Device-Channel:Local-Time");
    addDiagnosticChannel(
        "urn:gds:ch:GiraOneServer.GIOSRVKX03:GDS-Device-Channel",
        "GiraOneServer Bereit",
        "urn:gds:dp:GiraOneServer.GIOSRVKX03:GDS-Device-Channel:Ready");
  }

  /**
   * Adds the given channel to it's Set of {@link GiraOneChannel}. Duplicates with same urn are
   * getting ignored.
   *
   * @param channel The {@link GiraOneChannel} to add.
   */
  public void addChannel(GiraOneChannel channel) {
    this.channels.add(channel);
  }

  /**
   * @return Returna a {@link Collection} of all {@link GiraOneChannel} within this project.
   */
  public Collection<GiraOneChannel> lookupChannels() {
    return channels;
  }

  /**
   * Performs a lookup within the internal {@link Collection} of {@link GiraOneChannel} by the given
   * urn.
   *
   * @param urn The channelUrn
   * @return The optional {@link GiraOneChannel}, if there is any
   */
  public Optional<GiraOneChannel> lookupChannelByUrn(final String urn) {
    return this.channels.stream().filter(f -> urn.equals(f.getUrn())).findFirst();
  }

  /**
   * Performs a lookup within the internal {@link Collection} of {@link GiraOneChannel} by the given
   * channel name.
   *
   * @param name The channel name
   * @return The optional {@link GiraOneChannel}, if there is any
   */
  public Optional<GiraOneChannel> lookupChannelByName(final String name) {
    return this.channels.stream().filter(f -> name.equalsIgnoreCase(f.getName())).findFirst();
  }

  /**
   * This method returns the {@link GiraOneChannel} the given {@link GiraOneDataPoint} is assigned
   * to.
   *
   * @param dataPoint - The {@link GiraOneDataPoint} to assign on it's referenced channel
   * @return A {@link Optional} of {@link GiraOneChannel} for the given {@link GiraOneDataPoint}
   */
  public Optional<GiraOneChannel> lookupChannelByDataPoint(GiraOneDataPoint dataPoint) {
    return this.channels.stream()
        .filter(ch -> ch.containsGiraOneDataPoint(dataPoint.getUrn()))
        .findFirst();
  }

  /**
   * @return Returna a {@link Collection} of all {@link GiraOneChannel} within this project.
   */
  public Collection<GiraOneDataPoint> lookupGiraOneDataPoints() {
    return this.channels.stream()
        .map(GiraOneChannel::getDataPoints)
        .flatMap(Collection::stream)
        .toList();
  }

  /**
   * This method iterates over all channels for the given dataPointUrn and returns the concerning
   * {@link GiraOneDataPoint} if there is any.
   *
   * @param dataPointUrn - The datapoint urn
   * @return A {@link Optional} of {@link GiraOneDataPoint}
   */
  public Optional<GiraOneDataPoint> lookupGiraOneDataPoint(final String dataPointUrn) {
    return this.channels.stream()
        .map(GiraOneChannel::getDataPoints)
        .flatMap(Collection::stream)
        .filter(f -> matches(dataPointUrn, f))
        .findFirst();
  }

  /**
   * Adds an internal diagnostic channel to the project
   *
   * @param channelUrn The disgnostic channel URN
   * @param name The chanel name
   * @param datapointUrn A List of datapoints for the channel
   */
  public void addDiagnosticChannel(String channelUrn, String name, String... datapointUrn) {
    GiraOneChannel channel = new GiraOneChannel();
    channel.setUrn(channelUrn);
    channel.setLocation(LOCATION_BRIDGE);
    channel.setName(name);
    channel.setChannelType(GiraOneChannelType.Diagnostic);
    Stream.of(datapointUrn)
        .map(m -> new GiraOneDataPoint(GiraOneURN.of(m)))
        .forEach(channel::addDataPoint);
    this.channels.add(channel);
  }

  private boolean matches(String dataPointUrn, GiraOneDataPoint dataPoint) {
    if (dataPoint.getUrn() != null) {
      return dataPointUrn.matches(dataPoint.getUrn().toString());
    }
    return false;
  }
}
