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
package de.matgroe.giraone.client.commands;

import de.matgroe.giraone.client.GiraOneCommand;
import de.matgroe.giraone.client.GiraOneServerCommand;
import de.matgroe.giraone.client.types.GiraOneURN;
import de.matgroe.util.GenericBuilder;

/**
 * {@link GiraOneCommand} for reading the DiagnosticDeviceList
 *
 * @author Matthias Gröger - Initial contribution
 */
@GiraOneServerCommand(name = "DiagnosticSetValue", responsePayload = "")
public class DiagnosticSetValue extends GiraOneCommand {

  public static GenericBuilder<DiagnosticSetValue> builder() {
    return GenericBuilder.of(DiagnosticSetValue::new);
  }

  protected DiagnosticSetValue() {}

  private String urn;
  private Object value;

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public String getUrn() {
    return urn;
  }

  public void setUrn(String urn) {
    this.urn = urn;
  }

  public void setUrn(GiraOneURN urn) {
    this.urn = urn.toString();
  }
}
