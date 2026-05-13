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

import lombok.Getter;

/**
 * The {@link GiraOneValueChange} represents value change for a single source of data. The
 * GiraOneWebsocketClient emits {@link GiraOneValueChange} as a result after received a
 * GiraOneEvent.
 *
 * @author Matthias Gröger - Initial contribution
 */
@Getter
public class GiraOneValueChange extends GiraOneValue {

  /**
   * The previous value, the current value is available via super class -- GETTER --
   *
   * @return The previous value
   */
  private final String previousValue;

  public GiraOneValueChange(String urn, String value, String previous) {
    this(GiraOneURN.of(urn), value, previous);
  }

  public GiraOneValueChange(GiraOneURN urn, String value, String previous) {
    super(urn, value);
    this.previousValue = previous;
  }

  /**
   * @return returns true, if the previous value differs from current value
   */
  public boolean isChanged() {
    return !getValue().equals(getPreviousValue());
  }

  /**
   * Checks, if the value as represented by the given {@link GiraOneValueChange} is increasing or
   * not
   *
   * @return returns true, if increasing, false otherwise.
   */
  public boolean isValueIncreasing() {
    return getValueAsFloat() > getPreviousValueAsFloat();
  }

  /**
   * @return The previous value as Number
   */
  public Number getPreviousValueAsNumber() {
    return Float.parseFloat(getPreviousValue());
  }

  /**
   * @return The previous value as float
   */
  public float getPreviousValueAsFloat() {
    return getPreviousValueAsNumber().floatValue();
  }

  /**
   * @return The previous value as int
   */
  public int getPreviousValueAsInt() {
    return getPreviousValueAsNumber().intValue();
  }

  @Override
  public String toString() {
    return String.format(
        "{urn=%s, oldValue=%s, newValue=%s}", getDatapointUrn(), previousValue, getValue());
  }
}
