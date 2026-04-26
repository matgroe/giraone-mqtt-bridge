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

package de.matgroe.giraone.client.types;

import com.google.gson.annotations.SerializedName;

/**
 * Type for received error states from gira one server
 *
 * @author Matthias Groeger - Initial contribution
 */
public class GiraOneMessageError {

  @SerializedName("text")
  private String error = "OK";

  @SerializedName("hint")
  private String hint = "";

  @SerializedName("code")
  private int code = 0;

  public String getHint() {
    return hint;
  }

  public String getError() {
    return error;
  }

  public int getCode() {
    return code;
  }

  public boolean isErrorState() {
    return !"OK".equalsIgnoreCase(error);
  }

  @Override
  public String toString() {
    return String.format("GiraOneMessageError: %d -- %s (%s)", code, error, hint);
  }
}
