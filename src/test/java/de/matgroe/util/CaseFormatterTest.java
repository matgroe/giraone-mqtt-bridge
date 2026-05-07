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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test class for {@link CaseFormatter}.
 *
 * @author Matthias Groeger - Initial contribution
 */
class CaseFormatterTest {

  private static Stream<Arguments> provideKababCaseArguments() {
    return Stream.of(
        Arguments.of(null, ""),
        Arguments.of("Hello", "hello"),
        Arguments.of("HelloWorld", "hello-world"),
        Arguments.of("helloWorld", "hello-world"),
        Arguments.of("hello-World", "hello-world"),
        Arguments.of("hello - World", "hello-world"),
        Arguments.of("bÜrÖ Änß", "b-ur-o-anss"),
        Arguments.of("Büro Beschattung", "buro-beschattung"),
        Arguments.of("Büro Beßchattung", "buro-besschattung"),
        Arguments.arguments(
            "Ankleide Taster, Beschattung und Lüftung 2!§$%&/()=",
            "ankleide-taster-beschattung-und-luftung-2"));
  }

  @DisplayName("test for correct kebab-case formatting")
  @ParameterizedTest
  @MethodSource("provideKababCaseArguments")
  void testKababCase(String input, String expected) {
    String formatted = CaseFormatter.makeKebabCase(input);
    assertEquals(expected, formatted);
  }

  private static Stream<Arguments> provideSnakeCaseArguments() {
    return Stream.of(
        Arguments.of("Hello", "hello"),
        Arguments.of("HelloWorld", "hello_world"),
        Arguments.of("helloWorld", "hello_world"),
        Arguments.of("hello-World", "hello_world"),
        Arguments.of("hello - World", "hello_world"),
        Arguments.of("hello_world", "hello_world"),
        Arguments.of("hello - xxx-World", "hello_xxx_world"));
  }

  @DisplayName("test for correct snake_case formatting")
  @ParameterizedTest
  @MethodSource("provideSnakeCaseArguments")
  void testSnakeCase(String input, String expected) {
    String formatted = CaseFormatter.makeSnakeCase(input);
    assertEquals(expected, formatted);
  }
}
