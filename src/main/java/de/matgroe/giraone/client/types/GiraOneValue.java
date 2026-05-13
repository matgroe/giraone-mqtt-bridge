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

  public GiraOneValue(GiraOneDataPoint datapoint, String value) {
    this.datapointUrn = datapoint.getUrn();
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
