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
