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

/**
 * Constants to name properties within JsonObjects.
 *
 * @author Matthias Gröger - Initial contribution
 */
abstract class GiraOneJsonPropertyNames {

  private GiraOneJsonPropertyNames() {}

  static final String PROPERTY_CHANNEL_VIEW_ID = "channelViewID";
  static final String PROPERTY_URN = "urn";
  static final String PROPERTY_TYPE = "type";
  static final String PROPERTY_CHANNEL_VIEW_URN = "channelViewUrn";
  static final String PROPERTY_CHANNELS = "channels";
  static final String PROPERTY_CHANNEL_TYPE = "channelType";
  static final String PROPERTY_CHANNEL_TYPE_ID = "channelTypeId";
  static final String PROPERTY_DATAPOINTS = "datapoints";
  static final String PROPERTY_DATA_POINTS_CC = "dataPoints";
  static final String PROPERTY_FUNCTION_TYPE = "functionType";
  static final String PROPERTY_CONTENT = "content";
  static final String PROPERTY_NAME = "name";
  static final String PROPERTY_LOCATION = "location";
  static final String PROPERTY_MAINTYPE = "mainType";
  static final String PROPERTY_SUBLOCATIONS = "subLocations";
  static final String PROPERTY_COMPONENTS = "components";
}
