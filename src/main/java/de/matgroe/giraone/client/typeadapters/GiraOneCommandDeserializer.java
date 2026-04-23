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
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.matgroe.giraone.client.GiraOneCommand;
import de.matgroe.giraone.client.GiraOneServerCommand;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Deserializes a Json Element to {@link GiraOneCommand} within context of Gson parsing.
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneCommandDeserializer implements JsonDeserializer<GiraOneCommand> {
    private final Set<Class<?>> giraOneCommandClasses;

    public GiraOneCommandDeserializer(Set<Class<?>> commandClasses) {
        this.giraOneCommandClasses = commandClasses;
    }

    @Override
    public GiraOneCommand deserialize(JsonElement jsonElement,  Type type,
            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonDeserializationContext != null && jsonElement != null && jsonElement.isJsonObject()) {
            String command = ((JsonObject) jsonElement).getAsJsonPrimitive("command").getAsString();
            Optional<Class<?>> commandClassOptional = giraOneCommandClasses.stream()
                    .filter(f -> command.equals(f.getAnnotation(GiraOneServerCommand.class).name())).findFirst();
            if (commandClassOptional.isPresent()) {
                return Objects.requireNonNull(jsonDeserializationContext).deserialize(jsonElement,
                        commandClassOptional.get());
            }
        }
        return new GiraOneCommand();
    }
}
