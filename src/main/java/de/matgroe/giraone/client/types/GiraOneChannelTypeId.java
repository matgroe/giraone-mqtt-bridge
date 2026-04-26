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
 * Enumeration describes channel type id for {@link GiraOneChannelTypeId}
 *
 * @author Matthias Gröger - Initial contribution
 */
public enum GiraOneChannelTypeId {
  Temperature("NumericFloatStatus.Temperatur"),
  Humidity("NumericFloatStatus.Humidity"),
  Underfloor("KNX.HeatingCooling.HeatingUnderfloorHeatingWaterBased"),
  Light("KNX.Light.Light"),
  Dimmer("KNX.Light.Dimmer"),
  Lamp("Switch.Lamp"),
  Pump("Switch.Pump"),
  PowerOutlet("Switch.PowerOutlet"),
  Awning("Covering.Awning"),
  RoofWindow("Covering.RoofWindow"),
  VenetianBlind("Covering.VenetianBlind"),
  Scene("FunctionScene.Scene"),
  Button("Trigger.Button"),
  HueLight("Hue.Light"),
  Unknown("Unknown");

  private final String name;

  GiraOneChannelTypeId(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static GiraOneChannelTypeId fromName(String value) throws IllegalArgumentException {
    return Arrays.stream(GiraOneChannelTypeId.values())
        .filter(f -> value.equals(f.name))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}
