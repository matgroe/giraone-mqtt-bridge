package de.matgroe.hassio;

import de.matgroe.giraone.client.types.GiraOneChannel;
import de.matgroe.giraone.client.types.GiraOneChannelTypeId;
import de.matgroe.giraone.client.types.GiraOneDataPoint;
import de.matgroe.hassio.types.Component;
import de.matgroe.hassio.types.Sensor;
import java.util.Optional;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HassioComponentFactory {
  private static final String DATAPOINT_TEMPERATURE = "Temperature";
  private static final String DATAPOINT_HUMIDITY = "HumidityStatus";

  private Logger logger = LoggerFactory.getLogger(HassioComponentFactory.class);
  private final HassioTopicNameMapper hassioTopicNameMapper;

  public HassioComponentFactory(HassioTopicNameMapper hassioTopicNameMapper) {
    this.hassioTopicNameMapper = hassioTopicNameMapper;
  }

  public Component from(GiraOneChannel channel) {
    Component component;
    switch (channel.getChannelType()) {
      case Status:
        component = createSensor(channel);
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
    datapoint.ifPresent(dataPoint -> s.setStateTopic(hassioTopicNameMapper.topicNameOf(dataPoint)));
    return s;
  }
}
