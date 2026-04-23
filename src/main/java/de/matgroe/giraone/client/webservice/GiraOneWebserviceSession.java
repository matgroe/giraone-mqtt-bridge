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
package de.matgroe.giraone.client.webservice;

/**
 * The {@link GiraOneWebserviceSession} object represents the current session after authenticating
 * against Gira One Server via webservice interface.
 *
 * @author Matthias Gröger - Initial contribution
 */
class GiraOneWebserviceSession {
    private final String salt;
    private final String sessionSalt;
    private final String version;

    public GiraOneWebserviceSession(String salt, String sessionSalt, String version) {
        this.salt = salt;
        this.sessionSalt = sessionSalt;
        this.version = version;
    }

    public String getSalt() {
        return salt;
    }

    public String getSessionSalt() {
        return sessionSalt;
    }

    public String getVersion() {
        return version;
    }
}
