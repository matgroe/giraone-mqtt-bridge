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

package de.matgroe;

/**
 * Defines global constants within application context
 *
 * @author Matthias Groeger - Initial contribution
 */
public abstract class Contstants {

  public static final String DATAPOINT_TEMPERATURE = "Temperature";
  public static final String DATAPOINT_HUMIDITY = "HumidityStatus";
  public static final String DATAPOINT_ON_OFF = "OnOff";
  public static final String DATAPOINT_SHIFT = "Shift";
  public static final String DATAPOINT_BRIGHTNESS = "Brightness";
  public static final String DATAPOINT_STEP_UP_DOWN = "Step-Up-Down";
  public static final String DATAPOINT_UP_DOWN = "Up-Down";
  public static final String DATAPOINT_MOVEMENT = "Movement";
  public static final String DATAPOINT_POSITION = "Position";
  public static final String DATAPOINT_SLAT_POSITION = "Slat-Position";

  private static final String CHANNEL_URN_GDS_DEVICE =
      "urn:gds:dp:GiraOneServer.GIOSRVKX03:GDS-Device-Channel";
  private static final String DATAPOINT_GDS_DEVICE_READY = "Ready";
  private static final String DATAPOINT_GDS_DEVICE__LOCAL_TIME = "Local-Time";
}
