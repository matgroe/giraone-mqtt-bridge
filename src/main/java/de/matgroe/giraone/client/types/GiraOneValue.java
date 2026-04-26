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
 * The {@link GiraOneValue} represents a value for a single source of data within the Gira One
 * project. The GiraOneWebsocketClient emits a {@link GiraOneValue} as result of sending GetValue
 * command.
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneValue {

  /** The value as received from Gira One Server. */
  private final String value;

  /** The datapoint urn this value belongs to. */
  private final GiraOneURN datapointUrn;

  public GiraOneValue(String datapointUrn, String value) {
    this(GiraOneURN.of(datapointUrn), value);
  }

  public GiraOneValue(GiraOneURN datapointUrn, String value) {
    this.datapointUrn = datapointUrn;
    this.value = value;
  }

  public String getDatapointUrn() {
    return datapointUrn.toString();
  }

  public GiraOneDataPoint getGiraOneDataPoint() {
    return new GiraOneDataPoint(datapointUrn.toString());
  }

  public String getValue() {
    return value;
  }

  public boolean getValueAsBoolean() {
    return "1".equals(getValue());
  }

  public Number getValueAsNumber() {
    return Float.parseFloat(getValue());
  }

  public float getValueAsFloat() {
    return getValueAsNumber().floatValue();
  }

  public int getValueAsInt() {
    return getValueAsNumber().intValue();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GiraOneValue that = (GiraOneValue) o;
    return Objects.equals(value, that.value) && Objects.equals(datapointUrn, that.datapointUrn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, datapointUrn);
  }

  @Override
  public String toString() {
    return String.format("{urn=%s, value=%s}", datapointUrn, value);
  }
}
