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
package de.matgroe.hassio.types;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the Homeassistant Integration Cover.
 *
 * <p>https://www.home-assistant.io/integrations/cover.mqtt/
 * https://www.home-assistant.io/integrations/cover/#device-class
 */
@Getter
@Setter
public class Cover extends Component {

  public static final String PAYLOAD_OPEN = "open";
  public static final String PAYLOAD_CLOSE = "close";
  public static final String PAYLOAD_STOP = "stop";
  public static final String STATE_CLOSED = "closed";
  public static final String STATE_CLOSING = "closing";
  public static final String STATE_OPEN = "open";
  public static final String STATE_OPENING = "opening";
  public static final String STATE_STOPPED = "stopped";

  @SerializedName("payload_open")
  protected String payloadOpen = PAYLOAD_OPEN;

  @SerializedName("payload_close")
  protected String payloadClose = PAYLOAD_CLOSE;

  @SerializedName("payload_stop_tilt")
  protected String payloadStopTilt = PAYLOAD_STOP;

  @SerializedName("payload_stop")
  protected String payloadStop = PAYLOAD_STOP;

  @SerializedName("state_closed")
  protected String stateClosed = STATE_CLOSED;

  @SerializedName("state_closing")
  protected String stateClosing = STATE_CLOSING;

  @SerializedName("state_open")
  protected String stateOpen = STATE_OPEN;

  @SerializedName("state_opening")
  protected String stateOpening = STATE_OPENING;

  @SerializedName("state_stopped")
  protected String stateStopped = STATE_STOPPED;

  @SerializedName("position_closed")
  protected Integer positionClosed = 100;

  @SerializedName("position_open")
  protected Integer positionOpen = 0;

  @SerializedName("set_position_topic")
  protected String positionCommandTopic;

  @SerializedName("position_topic")
  protected String positionStateTopic;

  @SerializedName("tilt_closed_value")
  protected Integer tiltClosedValue;

  @SerializedName("tilt_opened_value")
  protected Integer tiltOpenedValue;

  @SerializedName("tilt_min")
  protected Integer tiltMin = 0;

  @SerializedName("tilt_max")
  protected Integer tiltMax = 100;

  @SerializedName("tilt_command_topic")
  protected String tiltCommandTopic;

  @SerializedName("tilt_status_topic")
  protected String tiltStatusTopic;

  public Cover() {
    this.platform = "cover";
  }
}
