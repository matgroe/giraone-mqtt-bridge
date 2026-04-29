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

    @SerializedName("payload_open")
    protected String payloadOpen;

    @SerializedName("payload_close")
    protected String payloadClose;

    @SerializedName("payload_stop_tilt")
    protected String payloadStopTilt;

    @SerializedName("payload_stop")
    protected String payloadStop;

    @SerializedName("state_closed")
    protected String stateClosed;

    @SerializedName("state_closing")
    protected String stateClosing;

    @SerializedName("state_open")
    protected String stateOpen;

    @SerializedName("state_opening")
    protected String stateOpening;

    @SerializedName("state_stopped")
    protected String stateStopped;

    @SerializedName("position_closed")
    protected Integer positionClosed;

    @SerializedName("position_open")
    protected Integer positionOpen;

    @SerializedName("set_position_topic")
    protected String positionCommandTopic;

    @SerializedName("position_topic")
    protected String positionStateTopic;

    @SerializedName("tilt_closed_value")
    protected Integer tiltClosedValue;

    @SerializedName("tilt_opened_value")
    protected Integer tiltOpenedValue;

    @SerializedName("tilt_min")
    protected Integer tiltMin;

    @SerializedName("tilt_max")
    protected Integer tiltMax;

    @SerializedName("tilt_command_topic")
    protected String tiltCommandTopic;

    @SerializedName("tilt_status_topic")
    protected String tiltStatusTopic;

    public Cover() {
        this.platform = "cover";
    }

}
