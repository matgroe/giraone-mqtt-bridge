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
package de.matgroe.giraone.client.typeadapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import de.matgroe.giraone.client.types.GiraOneFunctionType;
import java.lang.reflect.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Deserializes a Json Element to {@link GiraOneFunctionType} within context of Gson parsing.
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneFunctionTypeDeserializer implements JsonDeserializer<GiraOneFunctionType> {
  private final Logger logger = LoggerFactory.getLogger(GiraOneFunctionTypeDeserializer.class);

  @Override
  public GiraOneFunctionType deserialize(
      JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {
    if (jsonElement != null) {
      try {
        return GiraOneFunctionType.fromName(jsonElement.getAsString());
      } catch (IllegalArgumentException exp) {
        logger.warn(
            "Cannot map '{}' into enum of {}",
            jsonElement.getAsString(),
            GiraOneFunctionType.class.getName());
      }
    }
    return GiraOneFunctionType.Unknown;
  }
}
