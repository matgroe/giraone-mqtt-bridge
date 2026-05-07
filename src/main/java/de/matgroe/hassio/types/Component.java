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
public abstract class Component {
  @SerializedName("platform")
  protected String platform;

  @SerializedName("name")
  protected String name;

  @SerializedName("device")
  protected Device device;

  @SerializedName("entity_category")
  protected String entityCategory;

  @SerializedName("device_class")
  protected String deviceClass;

  @SerializedName("state_topic")
  protected String stateTopic;

  @SerializedName("command_topic")
  protected String commandTopic;

  @SerializedName("unique_id")
  protected String uniqueId;

  @SerializedName("qos")
  protected int qos;

  @SerializedName("retain")
  protected boolean retain;

  public Component() {
    this.qos = 0;
    this.retain = false;
  }
}
