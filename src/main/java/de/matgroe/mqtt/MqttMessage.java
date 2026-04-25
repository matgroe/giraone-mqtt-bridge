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

import com.hivemq.client.mqtt.datatypes.MqttTopic;
import de.matgroe.GiraOneMqttBridge;

import java.util.UUID;

/**
 * This class decribes the intemediate message format between {@link GiraOneMqttBridge}
 * and {@link de.matgroe.mqtt.MqttClient}
 */
public class MqttMessage {
    private final String topic;
    private final String payload;
    private final String messageId;

    public MqttMessage(String topic, String payload) {
        this.topic = topic;
        this.payload = payload;
        this.messageId = UUID.randomUUID().toString();
    }

    public MqttMessage(MqttTopic topic, String payload) {
        this(topic.toString(), payload);
    }

    public String getTopic() {
        return topic;
    }

    public String getPayload() {
        return payload;
    }

    public String getMessageId() {
        return messageId;
    }
}
