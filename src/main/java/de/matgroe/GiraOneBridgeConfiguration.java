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
import de.matgroe.giraone.client.GiraOneClientException;
import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.mqtt.GiraOneMqttBridge;
import de.matgroe.mqtt.MqttConfiguration;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("de.matgroe")
public class GiraOneBridgeConfiguration  {
    private final Logger logger = LoggerFactory.getLogger(GiraOneBridgeConfiguration.class);

    @Autowired
    GiraOneClientConfiguration giraOneClientConfiguration;

    @Bean("giraInboundMessages")
    Subject<GiraOneValue> giraInbound() { return PublishSubject.create(); }

    @Bean("giraOutboundMessages")
    Subject<GiraOneValue> giraOutbound() { return PublishSubject.create(); }

    @Bean
    GiraOneClient createGiraOneClient() {
        return new GiraOneClient(giraOneClientConfiguration);
    }

    @Bean
    GiraOneMqttBridge createGiraOneMqttBridge(MqttConfiguration mqttConfiguration, GiraOneClient giraOneClient) {
        return new GiraOneMqttBridge(mqttConfiguration, giraOneClient);
    }
}