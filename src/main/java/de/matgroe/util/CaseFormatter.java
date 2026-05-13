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
