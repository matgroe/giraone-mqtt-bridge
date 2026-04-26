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
package de.matgroe.giraone.client.commands;

import com.google.gson.annotations.SerializedName;
import de.matgroe.giraone.client.GiraOneCommand;
import de.matgroe.giraone.client.GiraOneServerCommand;
import de.matgroe.util.GenericBuilder;

/**
 * {@link GiraOneCommand} for Webservice Command for creating password salt.
 *
 * @author Matthias Gröger - Initial contribution
 */
@GiraOneServerCommand(name = "getPasswordSalt", responsePayload = "data")
public class GetPasswordSalt extends GiraOneCommand {
  @SerializedName("username")
  private String username = "";

  public static GenericBuilder<GetPasswordSalt> builder() {
    return GenericBuilder.of(GetPasswordSalt::new);
  }

  private GetPasswordSalt() {}

  public void setUsername(String username) {
    this.username = username;
  }
}
