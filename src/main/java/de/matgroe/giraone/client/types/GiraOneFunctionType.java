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
