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


/**
 * Enumeration describes type of {@link GiraOneComponent}
 *
 * @author Matthias Gröger - Initial contribution
 */
public enum GiraOneComponentType {
    KnxDimmingActuator,
    KnxHvacActuator,
    KnxSwitchingActuator,
    KnxButton,
    Unknown;

    public static GiraOneComponentType fromName(String name) {
        if (name.matches("urn:gds:cmp:.*:KnxDimmingActuator.*")) {
            return KnxDimmingActuator;
        } else if (name.matches("urn:gds:cmp:.*:KnxHvacActuator.*")) {
            return KnxHvacActuator;
        } else if (name.matches("urn:gds:cmp:.*:KnxSwitchingActuator.*")) {
            return KnxSwitchingActuator;
        } else if (name.matches("urn:gds:cmp:.*:KnxButton.*")) {
            return KnxButton;
        }
        return Unknown;
    }
}
