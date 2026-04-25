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

import de.matgroe.giraone.GiraOneClientProperties;
import de.matgroe.giraone.client.GiraOneClient;
import de.matgroe.mqtt.MqttClient;
import de.matgroe.mqtt.MqttClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({GiraOneMqttApplicationProperties.class, GiraOneClientProperties.class, MqttClientProperties.class})
public class GiraOneMqttApplicationConfiguration {

    @Bean
    GiraOneClient createGiraOneClient(GiraOneClientProperties giraOneClientProperties) {
        return new GiraOneClient(giraOneClientProperties);
    }

    @Bean
    MqttClient createGiraOneMqttBridge(MqttClientProperties mqttClientProperties) {
        return new MqttClient(mqttClientProperties);
    }
}