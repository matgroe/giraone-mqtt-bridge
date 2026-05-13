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
