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

import de.matgroe.giraone.GiraOneClientConfiguration;
import de.matgroe.giraone.client.GiraOneClient;
import de.matgroe.mqtt.MqttClient;
import de.matgroe.mqtt.MqttConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({GiraOneClientConfiguration.class, MqttConfiguration.class})
public class GiraOneMqttApplicationConfiguration {

    @Bean
    GiraOneClient createGiraOneClient(GiraOneClientConfiguration giraOneClientConfiguration) {
        return new GiraOneClient(giraOneClientConfiguration);
    }

    @Bean
    MqttClient createGiraOneMqttBridge(MqttConfiguration mqttConfiguration, GiraOneClient giraOneClient) {
        return new MqttClient(mqttConfiguration, giraOneClient);
    }
}