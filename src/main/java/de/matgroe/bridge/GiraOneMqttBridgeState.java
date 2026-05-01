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
package de.matgroe.bridge;

/**
 * Describes the bridge's current working state. - Stopped - Nothing happens right now -
 * ConnectingGiraOneClient - wait for GiraOneServer to be connected - ConnectingMqttClient wait for
 * MQTT-Broker to be connected - Connected - Everything is ok, Bridge is transfering messsages
 * between Gira and MQTT - Disconnected - - Error - Something bad happend, Work stops
 *
 * <p>The normal state flow for startup is Stopped -> ConnectingGiraOneClient ->
 * ConnectingMqttClient -> Connected -> Disconnected -> Stopped
 *
 * <p>Each state may change to Error
 */
public enum GiraOneMqttBridgeState {
  Stopped,
  ConnectingGiraOneClient,
  ConnectingMqttClient,
  Connected,
  Disconnected,
  Error
}
