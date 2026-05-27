/*
 * MIT License
 *
 * Copyright (c) 2026 Matthias Gröger
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.matgroe;

/**
 * Defines global constants within application context
 *
 * @author Matthias Groeger - Initial contribution
 */
public abstract class Constants {
  public static final String LOCATION_BRIDGE = "GiraoneMqttBridge";

  public static final String DATAPOINT_TEMPERATURE = "Temperature";
  public static final String DATAPOINT_HUMIDITY = "HumidityStatus";
  public static final String DATAPOINT_LOCALTIME = "Local-Time";
  public static final String DATAPOINT_READY = "Ready";
  public static final String DATAPOINT_ON_OFF = "OnOff";
  public static final String DATAPOINT_SHIFT = "Shift";
  public static final String DATAPOINT_BRIGHTNESS = "Brightness";
  public static final String DATAPOINT_STEP_UP_DOWN = "Step-Up-Down";
  public static final String DATAPOINT_UP_DOWN = "Up-Down";
  public static final String DATAPOINT_MOVEMENT = "Movement";
  public static final String DATAPOINT_POSITION = "Position";
  public static final String DATAPOINT_SLAT_POSITION = "Slat-Position";
  public static final String DATAPOINT_UPTIME = "Uptime";
  public static final String DATAPOINT_MQTT_STATE = "MqttConnectionState";
  public static final String DATAPOINT_GIRAONE_STATE = "GiraOneConnectionState";
  public static final String DATAPOINT_BRIDGE_STATE = "BridgeState";

  public static final String DATAPOINT_CURRENT = "Current";
  public static final String DATAPOINT_SET_POINT = "Set-Point";
  public static final String DATAPOINT_MODE = "Mode";
  public static final String DATAPOINT_STATUS = "Status";
  public static final String DATAPOINT_PRESENSE = "Presence";
  public static final String DATAPOINT_HEATING = "Heating";

  private static final String CHANNEL_URN_GDS_DEVICE =
      "urn:gds:dp:GiraOneServer.GIOSRVKX03:GDS-Device-Channel";
  private static final String DATAPOINT_GDS_DEVICE_READY = "Ready";
  private static final String DATAPOINT_GDS_DEVICE__LOCAL_TIME = "Local-Time";
}
