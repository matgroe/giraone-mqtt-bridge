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

/**
 * The enumeration of GiraOneFunctionTypes
 *
 * @author Matthias Gröger - Initial contribution
 */
public enum GiraOneFunctionType {
  Trigger("de.gira.schema.functions.Trigger"),
  PressAndHold("de.gira.schema.functions.PressAndHold"),
  Light("de.gira.schema.functions.KNX.Light"),
  Covering("de.gira.schema.functions.Covering"),
  HeatingCooling("de.gira.schema.functions.KNX.HeatingCooling"),
  Status("de.gira.schema.functions.NumericFloatStatus"),
  Scene("de.gira.schema.functions.FunctionScene"),
  Switch("de.gira.schema.functions.Switch"),
  HueLight("de.gira.schema.functions.Hue.Light"),
  Unknown("Unknown");

  private final String name;

  GiraOneFunctionType(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static GiraOneFunctionType fromName(String value) throws IllegalArgumentException {
    return Arrays.stream(GiraOneFunctionType.values())
        .filter(f -> value.equals(f.name))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}
