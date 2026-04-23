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
package de.matgroe.giraone.client;

/**
 * Defines a command to be sent out via websocket or
 * webservice api to Gira One Server.
 *
 * @author Matthias Groeger - Initial contribution
 */
public class GiraOneCommand {
    private static final String MISSING_ANNOTATION = "";

    private GiraOneServerCommand getAnnotation() {
        if (getClass().isAnnotationPresent(GiraOneServerCommand.class)) {
            return getClass().getAnnotation(GiraOneServerCommand.class);
        }
        throw new IllegalArgumentException(MISSING_ANNOTATION);
    }

    public String getCommand() {
        return getAnnotation().name();
    }

    public String getResponsePropertyName() {
        return getAnnotation().responsePayload();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof GiraOneCommand) {
            return getCommand().equals(((GiraOneCommand) o).getCommand());
        }
        return false;
    }
}
