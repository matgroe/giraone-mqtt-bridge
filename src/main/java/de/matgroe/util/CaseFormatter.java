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

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Utility class with some string case formatting functions.
 *
 * <p>see:
 * https://www.freecodecamp.org/news/snake-case-vs-camel-case-vs-pascal-case-vs-kebab-case-whats-the-difference/
 *
 * @author Matthias Gröger - Initial contribution
 */
public abstract class CaseFormatter {

  private static String normalizeInput(String input) {
    if (input == null) {
      return "";
    }

    String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    return pattern
        .matcher(normalized)
        .replaceAll("")
        .replace("ß", "ss")
        .replaceAll("[!§$%=\"&(),.:;'#/+*]", "");
  }

  /**
   * Converts the given input String into it's kabab-case representation.
   *
   * @param input The String to format
   * @return the kebab-case formatted input String.
   */
  public static String makeKebabCase(final String input) {
    return normalizeInput(input)
        .replaceAll("[a-z]+[0-9]*|[A-Z][a-z]+[0-9]*", "-$0-")
        .replace(" ", "-")
        .replaceFirst("^-+", "")
        .replaceFirst("-+$", "")
        .replaceAll("--+", "-")
        .replaceAll("-(\\s*-)*", "-")
        .toLowerCase();
  }

  /**
   * Converts the given input String into it's snake_case representation.
   *
   * @param input The String to format
   * @return the snake_case formatted input String.
   */
  public static String makeSnakeCase(final String input) {
    return makeKebabCase(input)
        .replace("-", "_")
        .replaceAll("_(\\s*_)*", "_")
        .replaceAll("(_)+", "_");
  }
}
