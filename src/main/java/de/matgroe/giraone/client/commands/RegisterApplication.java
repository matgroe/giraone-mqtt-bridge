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

import java.util.UUID;

/**
 * {@link GiraOneCommand} for registering the giraone bridge as
 * client application at Gira One Server.
 *
 * @author Matthias Gröger - Initial contribution
 */
@GiraOneServerCommand(name = "RegisterApplication")
public class RegisterApplication extends GiraOneCommand {

    private String applicationId = "Gira.UniversalApp";
    private String applicationType = "ui";
    private String instanceId;

    public static GenericBuilder<RegisterApplication> builder() {
        return GenericBuilder.of(RegisterApplication::new);
    }

    protected RegisterApplication() {
        this.instanceId = UUID.randomUUID().toString();
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
