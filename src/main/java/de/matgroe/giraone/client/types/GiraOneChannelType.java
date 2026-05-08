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
 * Enumeration describes channel type for {@link GiraOneChannelType}
 *
 * @author Matthias Gröger - Initial contribution
 */
public enum GiraOneChannelType {
  Covering("de.gira.schema.channels.BlindWithPos"),
  Dimmer("de.gira.schema.channels.KNX.Dimmer"),
  Light("de.gira.schema.channels.KNX.Light"),
  Status("de.gira.schema.channels.Float"),
  Switch("de.gira.schema.channels.Switch"),
  Function("de.gira.schema.channels.FunctionScene"),
  Heating("de.gira.schema.channels.KNX.HeatingCoolingSwitchable"),
  Trigger("de.gira.schema.channels.Trigger"),
  HueLight("de.gira.schema.channels.Hue.Light"),
  Diagnostic("de.matgroe.internal.diagnostics"),

  Unknown("Unknown");

  private final String name;

  GiraOneChannelType(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static GiraOneChannelType fromName(String value) throws IllegalArgumentException {
    return Arrays.stream(GiraOneChannelType.values())
        .filter(f -> value.equals(f.name))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}
