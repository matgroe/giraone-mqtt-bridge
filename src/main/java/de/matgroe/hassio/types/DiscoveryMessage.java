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
import java.util.HashMap;
import java.util.Map;

public class DiscoveryMessage {
  @SerializedName("dev")
  private Device device;

  @SerializedName("o")
  private Origin origin;

  @SerializedName("cmps")
  private Map<String, Component> components = new HashMap<>();

  public Device getDevice() {
    return device;
  }

  public void setDevice(Device device) {
    this.device = device;
  }

  public Origin getOrigin() {
    return origin;
  }

  public void setOrigin(Origin origin) {
    this.origin = origin;
  }

  public void addComponent(Component component) {
    this.components.put(component.getUniqueId(), component);
  }

  public Map<String, Component> getComponents() {
    return components;
  }

  public void setComponents(Map<String, Component> components) {
    this.components = components;
  }
}
