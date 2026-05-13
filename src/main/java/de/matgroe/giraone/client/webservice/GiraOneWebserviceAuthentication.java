/*
 * MIT License
 *
 * Copyright (c) 2026 Matthias Gröger
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.matgroe.giraone.client.webservice;

import de.matgroe.giraone.client.GiraOneClientException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class offers functionality for handling the authentication for using the Gira One Webservice
 * interface.
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
    throw new IllegalArgumentException("UnsupportedComponent version: " + session.getVersion());
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
  private String createHashSaltedPasswordGDS1(
      GiraOneWebserviceSession session, final String password) {
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
