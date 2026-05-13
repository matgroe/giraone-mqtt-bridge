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
