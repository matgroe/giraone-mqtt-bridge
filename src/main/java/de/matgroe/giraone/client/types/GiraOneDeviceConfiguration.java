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

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;

/**
 * The {@link GiraOneDeviceConfiguration} class describes the gira one server runtime configuration.
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneDeviceConfiguration {
  public static final String CURRENT_APPLICATION_VERSION = "CurrentApplicationVersion";
  public static final String CURRENT_FIRMWARE_VERSION = "CurrentFirmwareVersion";
  public static final String CURRENT_SYSTEM = "CurrentSystem";
  public static final String DEVICE_NAME = "DeviceName";
  public static final String DEVICE_ID = "DeviceId";
  public static final String DEVICE_UID = "DeviceUid";
  public static final String IP_ADDRESS = "IpAddress";
  public static final String SERIAL_NUMBER = "SerialNumber";
  public static final String APP_DEVICE_NAME = "AppDeviceName";

  public static final String MODULE_ID = "ModuleId";
  public static final String MODULE_REVISION = "ModuleRevision";
  public static final String MODULE_VERSION = "ModuleVersion";

  @SerializedName("ipc")
  private final HashMap<String, String> configuration = new HashMap<>();

  public String get(String name) {
    return configuration.get(name);
  }
}
