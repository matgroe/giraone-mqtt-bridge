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

import de.matgroe.giraone.client.GiraOneClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * This class offers functionality for handling the authentication for using the
 * Gira One Webservice interface.
 *
 * @author Matthias Gröger - Initial contribution
 */
class GiraOneWebserviceAuthentication {
    public static final String SHA_256 = "SHA-256";

    private final Logger logger = LoggerFactory.getLogger(GiraOneWebserviceAuthentication.class);
    private final MessageDigest digest;

    GiraOneWebserviceAuthentication() {
        try {
            this.digest = MessageDigest.getInstance(SHA_256);
        } catch (NoSuchAlgorithmException e) {
            throw new GiraOneClientException("Cannot create instance of MessageDigest.", e);
        }
    }

    String saltAndHashPassword(GiraOneWebserviceSession session, final String password) {
        if ("GDS_1".equals(session.getVersion())) {
            return createHashSaltedPasswordGDS1(session, password);
        }
        throw new IllegalArgumentException("Unsupported version: " + session.getVersion());
    }

    /**
     * Adopted from javascript code as provided by GiraOne Server
     *
     * <pre>
     * authMethodGDS1(e,t){
     *    const r=_e.sha256.create();
     *    r.update(_e.util.encodeUtf8(e)+t.salt);
     *    const n=r.digest().getBytes();
     *    return _e.util.encode64(n).substring(0,43)
     *  }
     * </pre>
     *
     * @param session
     * @param password
     * @return
     */

    private String createHashSaltedPasswordGDS1(GiraOneWebserviceSession session, final String password) {
        logger.trace("Salting given password with {}", session.getSalt());
        String text = password + session.getSalt();
        byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash).substring(0, 43);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }

    /**
     * computes the authentication token for webservice command doAuthenticateSession
     * 
     * @param session
     * @param password
     * @return
     */
    String computeAuthToken(GiraOneWebserviceSession session, String password) {
        String saltedPasswd = saltAndHashPassword(session, password);
        logger.trace("Salting salted and hashed password with {}", session.getSessionSalt());
        String text = String.format("%s+%s", saltedPasswd, session.getSessionSalt());
        byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash).toUpperCase();
    }
}
