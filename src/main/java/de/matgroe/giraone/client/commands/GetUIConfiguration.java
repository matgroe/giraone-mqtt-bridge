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
package de.matgroe.giraone.client.commands;

import de.matgroe.giraone.client.GiraOneCommand;
import de.matgroe.giraone.client.GiraOneServerCommand;
import de.matgroe.util.GenericBuilder;

/**
 * {@link GiraOneCommand} for reading the current project configuration
 * from Gira One Server.
 *
 * @author Matthias Gröger - Initial contribution
 */
@GiraOneServerCommand(name = "GetUIConfiguration", responsePayload = "config")
public class GetUIConfiguration extends GiraOneCommand {
    public static GenericBuilder<GetUIConfiguration> builder() {
        return GenericBuilder.of(GetUIConfiguration::new);
    }

    private boolean urns = true;

    private String guid = null;

    private String instanceId = null;

    protected GetUIConfiguration() {
    }

    public boolean withUrns() {
        return urns;
    }

    public void withUrns(boolean urns) {
        this.urns = urns;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
}
