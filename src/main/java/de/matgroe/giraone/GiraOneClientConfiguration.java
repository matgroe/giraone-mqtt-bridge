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

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix="giraone")
public class GiraOneClientConfiguration {

    @Value("${giraone.hostname:}")
    @NotEmpty
    public String hostname;

    @Value("${giraone.username:}")
    @NotEmpty
    public String username;

    @Value("${giraone.password:}")
    @NotEmpty
    public String password;

    @Value("${giraone.defaultTimeoutSeconds:10}")
    public int defaultTimeoutSeconds;

    @Value("${giraone.maxTextMessageSize:100}")
    public int maxTextMessageSize; // 100kB

    @Value("${giraone.discoverButtons:true}")
    public boolean discoverButtons;

}
