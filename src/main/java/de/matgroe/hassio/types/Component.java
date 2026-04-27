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

public abstract class Component {

  @SerializedName("platform")
  protected String platform;

  @SerializedName("name")
  protected String name;

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

  public Component() {
    this.entityCategory = "diagnostic";
    this.qos = 0;
  }

  public String getPlatform() {
    return platform;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEntityCategory() {
    return entityCategory;
  }

  public void setEntityCategory(String entityCategory) {
    this.entityCategory = entityCategory;
  }

  public String getDeviceClass() {
    return deviceClass;
  }

  public void setDeviceClass(String deviceClass) {
    this.deviceClass = deviceClass;
  }

  public String getStateTopic() {
    return stateTopic;
  }

  public void setStateTopic(String stateTopic) {
    this.stateTopic = stateTopic;
  }

  public void setCommandTopic(String commandTopic) {
    this.commandTopic = commandTopic;
  }

  public String getUniqueId() {
    return uniqueId;
  }

  public void setUniqueId(String uniqueId) {
    this.uniqueId = uniqueId;
  }

  public int getQos() {
    return qos;
  }

  public void setQos(int qos) {
    this.qos = qos;
  }
}
