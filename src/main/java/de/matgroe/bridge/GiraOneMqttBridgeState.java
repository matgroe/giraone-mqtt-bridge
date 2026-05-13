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
