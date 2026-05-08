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

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import de.matgroe.giraone.client.GiraOneCommand;
import de.matgroe.giraone.client.GiraOneTypeMapperFactory;

/**
 * The {@link GiraOneWebserviceRequest} wraps a {@link GiraOneCommand} to get sent out via
 * webservice to the Gira One Server.
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneWebserviceRequest {
  @SerializedName(value = "data")
  private final JsonObject data;

  private final transient GiraOneCommand command;

  protected GiraOneWebserviceRequest(GiraOneCommand command) {
    data = (JsonObject) GiraOneTypeMapperFactory.createGson().toJsonTree(command);
    this.command = command;
  }

  public GiraOneCommand getCommand() {
    return this.command;
  }
}
