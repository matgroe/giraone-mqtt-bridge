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
 * This class represents a system event as received from the Gira One Sever
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneEvent {
  private int id;

  private String urn;

  @SerializedName(value = "new")
  private String newValue;

  @SerializedName(value = "old")
  private String oldValue;

  private String newInternal;

  private String oldInternal;

  private String state;

  private String source;

  public int getId() {
    return id;
  }

  public String getUrn() {
    return urn;
  }

  public String getNewValue() {
    return newValue;
  }

  public String getOldValue() {
    return oldValue;
  }

  public String getNewInternal() {
    return newInternal;
  }

  public String getOldInternal() {
    return oldInternal;
  }

  public String getState() {
    return state;
  }

  public String getSource() {
    return source;
  }
}
