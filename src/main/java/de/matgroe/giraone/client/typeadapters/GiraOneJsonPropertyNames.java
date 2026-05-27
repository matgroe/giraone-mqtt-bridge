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
  static final String PROPERTY_CHANNEL_URN = "channelUrn";
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
  static final String PROPERTY_PARAMETER = "parameters";
  static final String PROPERTY_ID = "id";
}
