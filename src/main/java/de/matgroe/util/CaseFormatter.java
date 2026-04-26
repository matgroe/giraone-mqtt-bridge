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
package de.matgroe.util;

/**
 * Utility class with some string case formatting functions.
 *
 * @author Matthias Gröger - Initial contribution
 */
public abstract class CaseFormatter {

  /**
   * Converts the given input String into it's lower-case-hyphen representation.
   *
   * @param input The String to format
   * @return the lower-case-hyphen formatted input String.
   */
  public static String lowerCaseHyphen(final String input) {
    return input
        .replaceAll("[a-z]+[0-9]*|[A-Z][a-z]+[0-9]*", "-$0-")
        .replaceFirst("^-+", "")
        .replaceFirst("-+$", "")
        .replaceAll("--+", "-")
        .toLowerCase();
  }
}
