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

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix="mqtt")
public class MqttConfiguration {

    @Value("${application.name}")
    @NotEmpty
    String applicationName;

    @Value("${application.url}")
    String applicationUrl;

    @Value("${mqtt.user}")
    @NotEmpty
    String username;

    @Value("${mqtt.password}")
    @NotEmpty(message = "provide a value for")
    String password;

    @Value("${mqtt.broker}")
    @NotEmpty
    String mqttBroker;

    @Value("${mqtt.port}")
    @Min(1000)
    @Max(65655)
    int mqttPort;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getMqttBroker() {
        return mqttBroker;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public int getMqttPort() {
        return mqttPort;
    }

    public String getApplicationUrl() {
        return applicationUrl;
    }
}
