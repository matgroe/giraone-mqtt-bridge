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
 */ package de.matgroe.hassio.types;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Collection;

public class Device {
  @SerializedName("cu")
  private String configurationUrl;

  @SerializedName("cns")
  private String connections;

  @SerializedName("ids")
  private Collection<String> identifiers = new ArrayList<>();

  @SerializedName("name")
  private String name;

  @SerializedName("mf")
  private String manufacturer;

  @SerializedName("mdl")
  private String model;

  @SerializedName("mdl_id")
  private String modelId;

  @SerializedName("hw")
  private String hwVersion;

  @SerializedName("sw")
  private String swVersion;

  @SerializedName("sa")
  private String suggestedArea;

  @SerializedName("sn")
  private String serialNumber;

  public String getConfigurationUrl() {
    return configurationUrl;
  }

  public void setConfigurationUrl(String configurationUrl) {
    this.configurationUrl = configurationUrl;
  }

  public String getConnections() {
    return connections;
  }

  public void setConnections(String connections) {
    this.connections = connections;
  }

  public Collection<String> getIdentifiers() {
    return identifiers;
  }

  public void addIdentifier(String identifier) {
    this.identifiers.add(identifier);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getManufacturer() {
    return manufacturer;
  }

  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getModelId() {
    return modelId;
  }

  public void setModelId(String modelId) {
    this.modelId = modelId;
  }

  public String getHwVersion() {
    return hwVersion;
  }

  public void setHwVersion(String hwVersion) {
    this.hwVersion = hwVersion;
  }

  public String getSwVersion() {
    return swVersion;
  }

  public void setSwVersion(String swVersion) {
    this.swVersion = swVersion;
  }

  public String getSuggestedArea() {
    return suggestedArea;
  }

  public void setSuggestedArea(String suggestedArea) {
    this.suggestedArea = suggestedArea;
  }

  public String getSerialNumber() {
    return serialNumber;
  }

  public void setSerialNumber(String serialNumber) {
    this.serialNumber = serialNumber;
  }
}
