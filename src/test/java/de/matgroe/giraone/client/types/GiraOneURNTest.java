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
