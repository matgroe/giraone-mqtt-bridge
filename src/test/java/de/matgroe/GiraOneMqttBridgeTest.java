package de.matgroe;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import de.matgroe.giraone.GiraOneTestDataProvider;
import de.matgroe.giraone.client.GiraOneClient;
import de.matgroe.mqtt.MqttClient;
import de.matgroe.mqtt.MqttClientConnectionState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GiraOneMqttBridgeTest {
  GiraOneClient giraOneClient = mock(GiraOneClient.class);
  MqttClient mqttClient = mock(MqttClient.class);

  GiraOneMqttBridge theBridge;

  @BeforeEach
  void setup() {
    GiraOneMqttApplicationProperties applicationProperties =
        mock(GiraOneMqttApplicationProperties.class);
    when(applicationProperties.getName()).thenReturn("appname");
    when(applicationProperties.getUrl()).thenReturn("http://localhost");

    when(giraOneClient.getGiraOneProject())
        .thenReturn(GiraOneTestDataProvider.createGiraOneProject());
    when(giraOneClient.lookupGiraOneDeviceConfiguration())
        .thenReturn(GiraOneTestDataProvider.createGiraOneDeviceConfiguration());
    theBridge = spy(new GiraOneMqttBridge(applicationProperties, giraOneClient, mqttClient));
  }

  @Test
  void testInit() {
    theBridge.onMqttClientConnectionStateChanged(MqttClientConnectionState.Connected);
  }
}
