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

package de.matgroe.mqtt;

import de.matgroe.giraone.client.GiraOneClient;
import de.matgroe.giraone.client.types.GiraOneDeviceConfiguration;
import de.matgroe.mqtt.types.Device;
import de.matgroe.mqtt.types.DiscoveryMessage;
import de.matgroe.mqtt.types.Origin;

/**
 * This class is responsible to create the
 */
class MqttDiscoveryMessageFactory {
    private GiraOneClient giraOneClient;
    private MqttConfiguration mqttConfiguration;

    public MqttDiscoveryMessageFactory(MqttConfiguration mqttConfiguration, GiraOneClient giraOneClient) {
        this.giraOneClient = giraOneClient;
        this.mqttConfiguration = mqttConfiguration;
    }

    public String createDiscoveryTopic() {
        return String.format("homeassistant/device/%s/config",
                giraOneClient.lookupGiraOneDeviceConfiguration().get(GiraOneDeviceConfiguration.SERIAL_NUMBER)
        );
    }

    public DiscoveryMessage createDiscoveryMessage() {
        DiscoveryMessage ddm = new DiscoveryMessage();
        ddm.setOrigin(createOrigin(mqttConfiguration));
        ddm.setDevice(createDevice(giraOneClient.lookupGiraOneDeviceConfiguration()));
        return ddm;
    }

    Origin createOrigin(MqttConfiguration mqttConfiguration) {
        Origin origin = new Origin();
        origin.setName(mqttConfiguration.getApplicationName());
        origin.setSwVersion("123454");
        origin.setSupportUrl(mqttConfiguration.getApplicationUrl());
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
                String.format("%s.%s.%s",
                        deviceCfg.get(GiraOneDeviceConfiguration.MODULE_ID),
                        deviceCfg.get(GiraOneDeviceConfiguration.MODULE_REVISION),
                        deviceCfg.get(GiraOneDeviceConfiguration.MODULE_VERSION))
        );
        return d;
    }

}
