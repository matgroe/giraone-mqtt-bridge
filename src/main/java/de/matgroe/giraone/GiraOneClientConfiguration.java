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
package de.matgroe.giraone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GiraOneClientConfiguration {
    @Value("${giraone.hostname:}")
    public String hostname;

    @Value("${giraone.username:}")
    public String username;

    @Value("${giraone.password:}")
    public String password;

    public int defaultTimeoutSeconds = 10;
    public int maxTextMessageSize = 100; // 100kB
    public int tryReconnectAfterSeconds = 30;
    public int buttonReleaseTimeout = 1200;
    public int sessionTimeToLive = 5;
    public boolean discoverDevices = true;
    public boolean discoverButtons = true;
    public boolean overrideWithProjectSettings = false;
}
