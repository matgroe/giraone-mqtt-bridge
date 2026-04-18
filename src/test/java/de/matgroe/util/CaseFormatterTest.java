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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

/**
 * Test class for {@link CaseFormatter}.
 *
 * @author Matthias Groeger - Initial contribution
 */
class CaseFormatterTest {

    private static Stream<Arguments> provideCaseHyphenArguments() {
        return Stream.of(Arguments.of("Hello", "hello"), Arguments.of("HelloWorld", "hello-world"),
                Arguments.of("helloWorld", "hello-world"), Arguments.of("hello-World", "hello-world"));
    }

    @DisplayName("test for correct lower-case-hyphen formatting")
    @ParameterizedTest
    @MethodSource("provideCaseHyphenArguments")
    void testLowerCaseHyphen(String input, String expected) {
        String formatted = CaseFormatter.lowerCaseHyphen(input);
        assertEquals(expected, formatted);
    }
}
