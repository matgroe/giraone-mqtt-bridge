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
