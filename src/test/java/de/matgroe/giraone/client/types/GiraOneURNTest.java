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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test class for {@link GiraOneURN}
 *
 * @author Matthias Gröger - Initial contribution
 */
class GiraOneURNTest {

  @DisplayName("Should accept all given Strings and return them as urn")
  @ParameterizedTest
  @ValueSource(
      strings = {
        "ssrn:gds:chv:NumericFloatingPointStatus-Float-7",
        "ursn:gds:dp:GiraOneServer.GIOSRVKX03:KnxButton4Comfort2CSystem55Rocker2-gang-3.Humidity-1:HumidityStatus"
      })
  void shouldThrowIllegalArgumentException(String urn) {
    assertThrows(IllegalArgumentException.class, () -> GiraOneURN.of(urn));
  }

  @DisplayName("Should accept all given Strings and return them as urn")
  @ParameterizedTest
  @ValueSource(
      strings = {
        "urn:gds:chv:NumericFloatingPointStatus-Float-7",
        "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxButton4Comfort2CSystem55Rocker2-gang-3.Humidity-1:HumidityStatus"
      })
  void shouldAcceptAllGivenStrings(String urn) {
    assertEquals(urn, GiraOneURN.of(urn).toString());
  }

  @DisplayName("Should provide parent urn")
  @Test
  void shouldGiveParentUrn() {
    assertEquals(
        "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxButton4Comfort2CSystem55Rocker2-gang-3.Humidity-1",
        GiraOneURN.of(
                "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxButton4Comfort2CSystem55Rocker2-gang-3.Humidity-1:HumidityStatus")
            .getParent()
            .toString());
  }

  @DisplayName("Should provide resource name")
  @Test
  void shouldGiveResourceName() {
    assertEquals(
        "HumidityStatus",
        GiraOneURN.of(
                "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxButton4Comfort2CSystem55Rocker2-gang-3.Humidity-1:HumidityStatus")
            .getResourceName());
  }
}
