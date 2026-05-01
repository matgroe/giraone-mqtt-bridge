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
