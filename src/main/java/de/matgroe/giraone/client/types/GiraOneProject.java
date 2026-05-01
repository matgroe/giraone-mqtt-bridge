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
import java.util.Optional;
import java.util.Set;

/**
 * This class represents the project installation within your as configured GiraOne SmartHome
 * Environment. It offers some functions for accessing the {@link GiraOneChannel} and
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneProject {

  private final Set<GiraOneChannel> channels = Collections.synchronizedSet(new HashSet<>());

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

  private boolean matches(String dataPointUrn, GiraOneDataPoint dataPoint) {
    if (dataPoint.getUrn() != null) {
      return dataPointUrn.matches(dataPoint.getUrn().toString());
    }
    return false;
  }
}
