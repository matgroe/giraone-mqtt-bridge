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

import java.util.Objects;

/**
 * The GiraOneDataPoint defines a source of data which may have a value.
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneDataPoint {
  private GiraOneURN urn;

  public GiraOneDataPoint(final String urn) {
    this.urn = GiraOneURN.of(urn);
  }

  public GiraOneDataPoint(final GiraOneURN urn) {
    this.urn = urn;
  }

  public String getName() {
    return urn.getResourceName();
  }

  public GiraOneURN getDeviceUrn() {
    return urn.getParent();
  }

  public GiraOneURN getUrn() {
    return urn;
  }

  public void setUrn(String urn) {
    this.urn = GiraOneURN.of(urn);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GiraOneDataPoint dataPoint = (GiraOneDataPoint) o;
    return Objects.equals(getUrn(), dataPoint.getUrn());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(urn);
  }

  @Override
  public String toString() {
    return urn.toString();
  }
}
