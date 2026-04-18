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
package de.matgroe.giraone.client.types;


/**
 * The {@link GiraOneValueChange} represents value change for a single source of data. The
 * GiraOneWebsocketClient emits {@link GiraOneValueChange} as a result after
 * received a GiraOneEvent.
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneValueChange extends GiraOneValue {

    /**
     * The previous value, the current value is available via super class
     */
    private final String previousValue;

    public GiraOneValueChange(String urn, String value, String previous) {
        this(GiraOneURN.of(urn), value, previous);
    }

    public GiraOneValueChange(GiraOneURN urn, String value, String previous) {
        super(urn, value);
        this.previousValue = previous;
    }

    /**
     * @return returns true, if the previous value differs from current value
     */
    public boolean isChanged() {
        return !getValue().equals(getPreviousValue());
    }

    /**
     * Checks, if the value as represented by the given {@link GiraOneValueChange} is increasing or not
     *
     * @return returns true, if increasing, false otherwise.
     */
    public boolean isValueIncreasing() {
        return getValueAsFloat() > getPreviousValueAsFloat();
    }

    /**
     * @return The previous value
     */
    public String getPreviousValue() {
        return previousValue;
    }

    /**
     * @return The previous value as Number
     */
    public Number getPreviousValueAsNumber() {
        return Float.parseFloat(getPreviousValue());
    }

    /**
     * @return The previous value as float
     */
    public float getPreviousValueAsFloat() {
        return getPreviousValueAsNumber().floatValue();
    }

    /**
     * @return The previous value as int
     */
    public int getPreviousValueAsInt() {
        return getPreviousValueAsNumber().intValue();
    }

    @Override
    public String toString() {
        return String.format("{urn=%s, oldValue=%s, newValue=%s}", getDatapointUrn(), previousValue, getValue());
    }
}
