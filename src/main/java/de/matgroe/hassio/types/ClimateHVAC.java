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
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;

/** https://www.home-assistant.io/integrations/climate.mqtt/ */
@Getter
@Setter
public class ClimateHVAC extends Component {
  public static final String MODE_OFF = "off";
  public static final String MODE_HEATING = "heat";
  public static final String MODE_COMFORT = "Komfort";
  public static final String MODE_STANDBY = "Standby";
  public static final String MODE_NIGHT = "Nacht";
  public static final String MODE_FROST_CONTROL = "Frostschutz";

  @SerializedName("temperature_unit")
  protected String temperatureUnit;

  @SerializedName("action_topic")
  protected String actionTopic;

  /** The MQTT topic to publish commands to change the target temperature. */
  @SerializedName("temperature_command_topic")
  protected String temperatureCommandTopic;

  @SerializedName("temperature_state_topic")
  protected String temperatureStateTopic;

  @SerializedName("min_temp")
  protected Float minTemp;

  @SerializedName("max_temp")
  protected Float maxTemp;

  @SerializedName("precision")
  protected String precission;

  @SerializedName("temp_step")
  protected Float tempStep;

  @SerializedName("initial")
  protected Float initialTemp;

  /** The MQTT topic on which to listen for the current temperature. */
  @SerializedName("current_temperature_topic")
  protected String currentTemperatureTopic;

  @SerializedName("preset_mode_command_topic")
  protected String presetModeCommandTopic;

  @SerializedName("preset_mode_state_topic")
  protected String presetModeStateTopic;

  @SerializedName("preset_modes")
  protected Collection<String> presetModes;

  @SerializedName("modes")
  protected Collection<String> modes;

  @SerializedName("mode_state_topic")
  protected String modesStateTopic;

  public ClimateHVAC() {
    this.platform = "climate";
    this.temperatureUnit = "C";
    this.minTemp = 7.0f;
    this.maxTemp = 35.0f;
    this.precission = "0.1";
    this.tempStep = 1.0f;
    this.initialTemp = 21.0f;
  }
}
