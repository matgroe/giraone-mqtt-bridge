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
package de.matgroe.giraone.client;

import java.io.IOException;
import java.io.Serial;

/**
 * Generic Exception with Gira One Domain.
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneCommunicationException extends IOException {
  @Serial private static final long serialVersionUID = 1L;
  private GiraOneCommand causingCommand = null;

  public GiraOneCommunicationException(GiraOneCommand causingCommand, String message, int code) {
    this(causingCommand, String.format("%s.%d", message, code), null);
  }

  public GiraOneCommunicationException(GiraOneCommand causingCommand, String message, Throwable t) {
    super(message, t);
    this.causingCommand = causingCommand;
  }

  public GiraOneCommand getCausingCommand() {
    return causingCommand;
  }
}
