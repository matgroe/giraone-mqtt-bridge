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

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Generic Builder Pattern taken from https://www.baeldung.com/java-builder-pattern
 *
 * @param <T> The class to build
 * @author Matthias Gröger - Initial contribution
 */
public class GenericBuilder<T> {
  private final Supplier<T> supplier;

  private GenericBuilder(Supplier<T> supplier) {
    this.supplier = supplier;
  }

  public static <T> GenericBuilder<T> of(Supplier<T> supplier) {
    return new GenericBuilder<>(supplier);
  }

  public <P> GenericBuilder<T> with(BiConsumer<T, P> consumer, P value) {
    return new GenericBuilder<>(
        () -> {
          T object = supplier.get();
          consumer.accept(object, value);
          return object;
        });
  }

  public T build() {
    return supplier.get();
  }
}
