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
package de.matgroe.bridge.translators.giraone;

import static de.matgroe.Constants.DATAPOINT_POSITION;

import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.giraone.client.types.GiraOneValueChange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Disabled
class GiraOneCoveringTranslatorTest extends GiraOneTranslatorsTest {

  private GiraOneValue createGiraOneValue(String dataPoint, String oldValue, String newValue) {
    return new GiraOneValueChange(
        String.format(
            "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxSwitchingActuator24-gang2C16A2FBlindActuator12-gang-1.Curtain-4:%s",
            dataPoint),
        newValue,
        oldValue);
  }

  @DisplayName("should down to position")
  @Test
  void testMoveDownToPosition() {
    GiraOneValue valPosition = createGiraOneValue(DATAPOINT_POSITION, "51.372549", "62");
    GiraOneValue valMove = createGiraOneValue(DATAPOINT_POSITION, "51.372549", "62");

    Assertions.fail("not implemented!");
  }

  @DisplayName("should up to position")
  @Test
  void testMoveUpToPosition() {
    Assertions.fail("not implemented!");
  }

  @DisplayName("should move down and stop")
  @Test
  void testMoveDownAndStop() {
    Assertions.fail("not implemented!");
  }

  @DisplayName("should move single step, triggered by rocker")
  @Test
  void testMoveSingleStepByRocker() {
    Assertions.fail("not implemented!");
  }

  @DisplayName("should move single up, triggered by rocker")
  @Test
  void testMoveUpByRocker() {
    Assertions.fail("not implemented!");
  }

  @DisplayName("should change slat position, triggered by rocker")
  @Test
  void testSlatPositionByRocker() {
    Assertions.fail("not implemented!");
  }
}
