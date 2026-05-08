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
