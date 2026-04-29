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
package de.matgroe.giraone.client.types;

import com.google.gson.annotations.SerializedName;

/**
 * GiraOneChannelParameter
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneChannelParameter {

    @SerializedName("key")
    private String key;

    @SerializedName("set")
    private String set;

    @SerializedName("value")
    private String value;

    public String getKey() {
        return key;
    }

    public String getSet() {
        return set;
    }

    public String getValue() {
        return value;
    }
}
