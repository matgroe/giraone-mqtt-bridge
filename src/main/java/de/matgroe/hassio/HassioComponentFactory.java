package de.matgroe.hassio;

import static de.matgroe.Contstants.DATAPOINT_BRIGHTNESS;
import static de.matgroe.Contstants.DATAPOINT_HUMIDITY;
import static de.matgroe.Contstants.DATAPOINT_ON_OFF;
import static de.matgroe.Contstants.DATAPOINT_POSITION;
import static de.matgroe.Contstants.DATAPOINT_SLAT_POSITION;
import static de.matgroe.Contstants.DATAPOINT_TEMPERATURE;
import static de.matgroe.Contstants.DATAPOINT_UP_DOWN;

import de.matgroe.bridge.GiraOneChannelMqttTopicMapper;
import de.matgroe.giraone.client.types.GiraOneChannel;
import de.matgroe.giraone.client.types.GiraOneChannelTypeId;
import de.matgroe.giraone.client.types.GiraOneDataPoint;
import de.matgroe.hassio.types.Component;
import de.matgroe.hassio.types.Cover;
import de.matgroe.hassio.types.Device;
import de.matgroe.hassio.types.Light;
import de.matgroe.hassio.types.Sensor;
import de.matgroe.hassio.types.Switch;
import de.matgroe.hassio.types.UnsupportedComponent;
import java.util.Optional;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This factory provides {@link Component}s to be used within the MQTT DeviceDidcoveryMessage for
 * Homeassistant.
 *
 * <p>https://www.home-assistant.io/integrations/mqtt/
 * https://www.home-assistant.io/integrations/homeassistant/#device-class
 */
public class HassioComponentFactory {

  private final GiraOneChannelMqttTopicMapper hassioGiraOneChannelMqttTopicMapper;

  public HassioComponentFactory(GiraOneChannelMqttTopicMapper hassioGiraOneChannelMqttTopicMapper) {
    this.hassioGiraOneChannelMqttTopicMapper = hassioGiraOneChannelMqttTopicMapper;
  }

  public Component from(GiraOneChannel channel) {
    Logger logger = LoggerFactory.getLogger(HassioComponentFactory.class);
    Component component;
    switch (channel.getChannelType()) {
      case Status:
        component = createSensor(channel);
        break;
      case Switch:
        component = createSwitch(channel);
        break;
      case Dimmer:
      case Light:
        component = createLight(channel);
        break;
      case Covering:
        component = createCover(channel);
        break;
      default:
        logger.warn("no factory implementation for {} ", channel);
        component = createUnsupportedComponent(channel);
    }
    component.setUniqueId(DigestUtils.sha1Hex(channel.getUrn()));
    component.setName(channel.getName());

    Device d = new Device();
    d.setSuggestedArea(channel.getLocation());
    d.addIdentifier(channel.getUrn());
    d.setName(channel.getName());
    component.setDevice(d);

    return component;
  }

  /**
   * Create an {@link UnsupportedComponent} in case of getting an unkown {@link GiraOneChannel}
   * within the {@link de.matgroe.giraone.client.types.GiraOneProject}. This {@link Component}
   * exists only for not breaking the application in case of getting an new/unknown channel.
   *
   * @param channel The GiraOneChannel to be mapped to {@link UnsupportedComponent}
   * @return The {@link UnsupportedComponent}
   */
  private UnsupportedComponent createUnsupportedComponent(GiraOneChannel channel) {
    UnsupportedComponent u = new UnsupportedComponent();
    u.setPlatform(channel.getChannelTypeId().toString());
    return u;
  }

  /**
   * Creates a Homeassistant MQTT Sensor Component.
   *
   * <p>https://www.home-assistant.io/integrations/sensor.mqtt/
   * https://www.home-assistant.io/integrations/sensor#device-class
   *
   * @param channel The GiraOneChannel to be mapped to {@link Switch}
   * @return The {@link Switch}
   */
  private Sensor createSensor(GiraOneChannel channel) {
    Optional<GiraOneDataPoint> datapoint = Optional.empty();
    Sensor s = new Sensor();
    if (channel.getChannelTypeId() == GiraOneChannelTypeId.Humidity) {
      s.setDeviceClass("humidity");
      s.setUnitOfMeasurement("%");
      datapoint = channel.getDatapoint(DATAPOINT_HUMIDITY);
    } else if (channel.getChannelTypeId() == GiraOneChannelTypeId.Temperature) {
      s.setDeviceClass("temperature");
      s.setUnitOfMeasurement("°C");
      datapoint = channel.getDatapoint(DATAPOINT_TEMPERATURE);
    }
    datapoint.ifPresent(
        dataPoint ->
            s.setStateTopic(hassioGiraOneChannelMqttTopicMapper.stateTopicNameOf(dataPoint)));
    return s;
  }

  /**
   * Creates a Homeassistant MQTT Switch Component.
   *
   * <p>https://www.home-assistant.io/integrations/switch.mqtt/
   * https://www.home-assistant.io/integrations/switch/#device-class
   *
   * @param channel The GiraOneChannel to be mapped to {@link Switch}
   * @return The {@link Switch}
   */
  private Switch createSwitch(GiraOneChannel channel) {
    if (channel.getChannelTypeId() == GiraOneChannelTypeId.Lamp) {
      return createLight(channel);
    }
    Switch s = new Switch();
    Optional<GiraOneDataPoint> datapoint = channel.getDatapoint(DATAPOINT_ON_OFF);
    datapoint.ifPresent(
        dataPoint -> {
          s.setCommandTopic(hassioGiraOneChannelMqttTopicMapper.commandTopicNameOf(dataPoint));
          s.setStateTopic(hassioGiraOneChannelMqttTopicMapper.stateTopicNameOf(dataPoint));
        });

    if (channel.getChannelTypeId() == GiraOneChannelTypeId.PowerOutlet) {
      s.setDeviceClass("outlet");
    } else {
      s.setDeviceClass("switch");
    }

    s.setPayloadOff("0");
    s.setPayloadOn("1");
    return s;
  }

  /**
   * Creates a Homeassistant MQTT Switch Component.
   *
   * <p>https://www.home-assistant.io/integrations/light.mqtt/
   * https://www.home-assistant.io/integrations/light/#device-class
   *
   * @param channel The GiraOneChannel to be mapped to {@link Switch}
   * @return The {@link Switch}
   */
  private Light createLight(GiraOneChannel channel) {
    Light l = new Light();

    Optional<GiraOneDataPoint> datapoint = channel.getDatapoint(DATAPOINT_ON_OFF);
    datapoint.ifPresent(
        dataPoint -> {
          l.setCommandTopic(hassioGiraOneChannelMqttTopicMapper.commandTopicNameOf(dataPoint));
          l.setStateTopic(hassioGiraOneChannelMqttTopicMapper.stateTopicNameOf(dataPoint));
          l.setPayloadOff("0");
          l.setPayloadOn("1");
        });

    datapoint = channel.getDatapoint(DATAPOINT_BRIGHTNESS);
    datapoint.ifPresent(
        dataPoint -> {
          l.setBrightnessCommandTopic(
              hassioGiraOneChannelMqttTopicMapper.commandTopicNameOf(dataPoint));
          l.setBrightnessStateTopic(
              hassioGiraOneChannelMqttTopicMapper.stateTopicNameOf(dataPoint));
          l.setBrightnessScale(100);
          l.setOnCommandType("brightness");
        });

    return l;
  }

  private Cover createCover(GiraOneChannel channel) {
    Cover cover = new Cover();
    Optional<GiraOneDataPoint> dpUpDown = channel.getDatapoint(DATAPOINT_UP_DOWN);
    dpUpDown.ifPresent(
        dataPoint -> {
          cover.setCommandTopic(hassioGiraOneChannelMqttTopicMapper.commandTopicNameOf(dataPoint));
          cover.setStateTopic(hassioGiraOneChannelMqttTopicMapper.stateTopicNameOf(dataPoint));
        });

    if (channel.getChannelTypeId() == GiraOneChannelTypeId.Awning) {
      cover.setDeviceClass("awning");
    } else if (channel.getChannelTypeId() == GiraOneChannelTypeId.RoofWindow) {
      cover.setDeviceClass("window");
    } else if (channel.getChannelTypeId() == GiraOneChannelTypeId.VenetianBlind) {
      cover.setDeviceClass("shutter");
      addRelativeProperties(channel, cover);
    }

    return cover;
  }

  private void addRelativeProperties(GiraOneChannel channel, Cover cover) {
    Optional<GiraOneDataPoint> dpPosition = channel.getDatapoint(DATAPOINT_POSITION);
    dpPosition.ifPresent(
        dataPoint -> {
          cover.setPositionCommandTopic(
              hassioGiraOneChannelMqttTopicMapper.commandTopicNameOf(dataPoint));
          cover.setPositionStateTopic(
              hassioGiraOneChannelMqttTopicMapper.stateTopicNameOf(dataPoint));
          cover.setPositionClosed(100);
          cover.setPositionOpen(0);
        });

    Optional<GiraOneDataPoint> datapoint = channel.getDatapoint(DATAPOINT_SLAT_POSITION);
    datapoint.ifPresent(
        dataPoint -> {
          cover.setTiltCommandTopic(
              hassioGiraOneChannelMqttTopicMapper.commandTopicNameOf(dataPoint));
          cover.setTiltStatusTopic(hassioGiraOneChannelMqttTopicMapper.stateTopicNameOf(dataPoint));
          cover.setTiltMin(100);
          cover.setTiltMax(0);
          cover.setTiltClosedValue(100);
          cover.setTiltOpenedValue(0);
        });
  }
}
