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
package de.matgroe.hassio;

import de.matgroe.GiraOneMqttApplicationProperties;
import de.matgroe.giraone.client.types.GiraOneDeviceConfiguration;
import de.matgroe.hassio.types.Device;
import de.matgroe.hassio.types.DiscoveryMessage;
import de.matgroe.hassio.types.Origin;

/** This class is responsible to create the */
public class HassioDiscoveryMessageFactory {
  private GiraOneDeviceConfiguration giraOneDeviceConfiguration;
  private GiraOneMqttApplicationProperties applicationProperties;

  public HassioDiscoveryMessageFactory(
      GiraOneMqttApplicationProperties applicationProperties,
      GiraOneDeviceConfiguration giraOneDeviceConfiguration) {
    this.giraOneDeviceConfiguration = giraOneDeviceConfiguration;
    this.applicationProperties = applicationProperties;
  }

  public String createDiscoveryTopic() {
    return String.format(
        "homeassistant/device/%s/config",
        giraOneDeviceConfiguration.get(GiraOneDeviceConfiguration.SERIAL_NUMBER));
  }

  public DiscoveryMessage createDiscoveryMessage() {
    DiscoveryMessage ddm = new DiscoveryMessage();
    ddm.setOrigin(createOrigin(applicationProperties));
    ddm.setDevice(createDevice(giraOneDeviceConfiguration));
    return ddm;
  }

  Origin createOrigin(GiraOneMqttApplicationProperties mqttClientProperties) {
    Origin origin = new Origin();
    origin.setName(mqttClientProperties.getName());
    origin.setSwVersion("123454");
    origin.setSupportUrl(mqttClientProperties.getUrl());
    return origin;
  }

  Device createDevice(GiraOneDeviceConfiguration deviceCfg) {
    Device d = new Device();
    d.addIdentifier(deviceCfg.get(GiraOneDeviceConfiguration.SERIAL_NUMBER));
    d.setName(deviceCfg.get(GiraOneDeviceConfiguration.DEVICE_NAME));
    d.setSerialNumber(deviceCfg.get(GiraOneDeviceConfiguration.SERIAL_NUMBER));
    d.setModel(deviceCfg.get(GiraOneDeviceConfiguration.APP_DEVICE_NAME));
    d.setManufacturer("Gira");
    d.setSwVersion(deviceCfg.get(GiraOneDeviceConfiguration.CURRENT_FIRMWARE_VERSION));
    d.setHwVersion(
        String.format(
            "%s.%s.%s",
            deviceCfg.get(GiraOneDeviceConfiguration.MODULE_ID),
            deviceCfg.get(GiraOneDeviceConfiguration.MODULE_REVISION),
            deviceCfg.get(GiraOneDeviceConfiguration.MODULE_VERSION)));
    return d;
  }
}
