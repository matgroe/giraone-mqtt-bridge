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
package de.matgroe.hassio.types;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the Homeassistant Integration Sensor.
 *
 * <p>https://www.home-assistant.io/integrations/sensor.mqtt/
 * https://www.home-assistant.io/integrations/sensor/#device-class
 */
@Getter
@Setter
public class Sensor extends Component {

  @SerializedName("state_class")
  protected String stateClass;

  @SerializedName("unit_of_measurement")
  protected String unitOfMeasurement;

  @SerializedName("suggested_display_precision")
  protected String suggestedDisplayPrecision;

  public Sensor() {
    this.platform = "sensor";
    this.stateClass = "measurement";
  }
}
