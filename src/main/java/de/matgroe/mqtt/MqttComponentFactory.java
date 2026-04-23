package de.matgroe.mqtt;

import de.matgroe.giraone.client.types.GiraOneChannel;
import de.matgroe.giraone.client.types.GiraOneChannelTypeId;
import de.matgroe.giraone.client.types.GiraOneDataPoint;
import de.matgroe.mqtt.types.Component;
import de.matgroe.mqtt.types.Sensor;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

public class MqttComponentFactory {
    private static final String DATAPOINT_TEMPERATURE = "Temperature";
    private static final String DATAPOINT_HUMIDITY = "HumidityStatus";

    private Logger logger = LoggerFactory.getLogger(MqttComponentFactory.class);
    private final MqttTopicNameMapper mqttTopicNameMapper;

    public MqttComponentFactory(MqttTopicNameMapper mqttTopicNameMapper) {
        this.mqttTopicNameMapper = mqttTopicNameMapper;
    }

    public Component from(GiraOneChannel channel) {
        Component component;
        switch(channel.getChannelType()) {
            case Status:
                component =  createSensor(channel);
                break;
            default:
                logger.error("no factory implementation for {} ", channel);
                return null;
        }
        component.setUniqueId(DigestUtils.sha1Hex(channel.getUrn()));
        return component;
    }

    private Sensor createSensor(GiraOneChannel channel) {
        Optional<GiraOneDataPoint> datapoint = Optional.empty();
        Sensor s = new Sensor();
        s.setName(channel.getName());
        if (channel.getChannelTypeId() == GiraOneChannelTypeId.Humidity) {
            s.setDeviceClass("humidity");
            s.setUnitOfMeasurement("%");
            datapoint = channel.getDatapoint(DATAPOINT_HUMIDITY);
        } else if (channel.getChannelTypeId() == GiraOneChannelTypeId.Temperature) {
            s.setDeviceClass("temperature");
            s.setUnitOfMeasurement("°C");
            datapoint = channel.getDatapoint(DATAPOINT_TEMPERATURE);
        }
        datapoint.ifPresent(dataPoint -> s.setStateTopic(mqttTopicNameMapper.topicNameOf(dataPoint)));
        return s;
    }
}
