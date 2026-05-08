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

import java.util.Arrays;
import java.util.Objects;

/**
 * Utility class for Uniform Resource Name (URN).
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneURN {
  public static final GiraOneURN INVALID = GiraOneURN.of("urn:ns:invalid:invalid");

  private static final String DELIMITER = ":";
  private final String[] urnParts;

  /**
   * @param urn The URN String representation. *
   * @return The parsed URN
   */
  public static GiraOneURN of(final String urn) {
    return new GiraOneURN(urn);
  }

  /**
   * @param deviceUrn The device urn String
   * @param resource the URN resource part
   * @return The parsed URN
   */
  public static GiraOneURN of(final String deviceUrn, final String resource) {
    return new GiraOneURN(String.format("%s:%s", deviceUrn, resource));
  }

  /**
   * @param urn The URN String representation. *
   * @return The parsed URN
   */
  public static GiraOneURN of(final GiraOneURN urn, final String resource) {
    return new GiraOneURN(String.format("%s:%s", urn, resource));
  }

  private GiraOneURN(final String urn) {
    this.urnParts = urn.split(DELIMITER);
    if (!"urn".equals(urnParts[0])) {
      throw new IllegalArgumentException("The String '" + urn + "' cannot get parsed as URN");
    }
  }

  private GiraOneURN(final String[] urnParts) {
    this.urnParts = urnParts;
  }

  /**
   * @return Returns the last part of the parsed URN
   */
  public String getResourceName() {
    if (urnParts.length > 0) {
      return urnParts[urnParts.length - 1];
    }
    return toString();
  }

  public GiraOneURN getParent() {
    if (urnParts.length > 1) {
      String[] b = new String[urnParts.length - 1];
      System.arraycopy(urnParts, 0, b, 0, b.length);
      return new GiraOneURN(b);
    }
    return new GiraOneURN(urnParts);
  }

  public GiraOneURN makeSibling(final String sibling) {
    return GiraOneURN.of(getParent(), sibling);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GiraOneURN that = (GiraOneURN) o;
    return Objects.deepEquals(urnParts, that.urnParts);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(urnParts);
  }

  @Override
  public String toString() {
    return String.join(DELIMITER, urnParts);
  }
}
