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


import java.util.ArrayList;
import java.util.Collection;

/**
 * The {@link GiraOneChannelCollection} describes a base component within the GiraOne SmartHome system.
 * A physical GiraOneComponent might Triggering Element like a Rocker or Button
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneChannelCollection {
    private final Collection<GiraOneChannel> channels = new ArrayList<>();

    public Collection<GiraOneChannel> getChannels() {
        return channels;
    }

    public void add(GiraOneChannel giraOneChannel) {
        this.channels.add(giraOneChannel);
    }
}
