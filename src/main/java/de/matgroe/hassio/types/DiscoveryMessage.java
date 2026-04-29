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
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the Homeassistant Integration DiscoveryMessage.
 *
 * <p>https://www.home-assistant.io/integrations/cover.mqtt/
 */
@Getter
@Setter
public class DiscoveryMessage {
  @SerializedName("dev")
  private Device device;

  @SerializedName("o")
  private Origin origin;

  @SerializedName("cmps")
  private Map<String, Component> components = new HashMap<>();

  public void addComponent(Component component) {
    this.components.put(component.getUniqueId(), component);
  }
}
