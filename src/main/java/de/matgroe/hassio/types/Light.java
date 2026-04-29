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

@Getter
@Setter
public class Light extends Switch {

  @SerializedName("brightness_command_topic")
  protected String brightnessCommandTopic;

  @SerializedName("brightness_state_topic")
  protected String brightnessStateTopic;

  @SerializedName("brightness_scale")
  protected int brightnessScale;

  @SerializedName("on_command_type")
  protected String onCommandType;

  public Light() {
    brightnessScale = 100;
    this.platform = "light";
    this.onCommandType = "brightness";
    this.deviceClass = null;
  }
}
