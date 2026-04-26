package de.matgroe.hassio.types;

import com.google.gson.annotations.SerializedName;

public abstract class Component {

  @SerializedName("platform")
  protected String platform;

  @SerializedName("name")
  protected String name;

  @SerializedName("entity_category")
  protected String entityCategory;

  @SerializedName("device_class")
  protected String deviceClass;

  @SerializedName("state_topic")
  protected String stateTopic;

  @SerializedName("unique_id")
  protected String uniqueId;

  @SerializedName("qos")
  protected int qos;

  public Component() {
    this.entityCategory = "diagnostic";
    this.qos = 0;
  }

  public String getPlatform() {
    return platform;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEntityCategory() {
    return entityCategory;
  }

  public void setEntityCategory(String entityCategory) {
    this.entityCategory = entityCategory;
  }

  public String getDeviceClass() {
    return deviceClass;
  }

  public void setDeviceClass(String deviceClass) {
    this.deviceClass = deviceClass;
  }

  public String getStateTopic() {
    return stateTopic;
  }

  public void setStateTopic(String stateTopic) {
    this.stateTopic = stateTopic;
  }

  public String getUniqueId() {
    return uniqueId;
  }

  public void setUniqueId(String uniqueId) {
    this.uniqueId = uniqueId;
  }

  public int getQos() {
    return qos;
  }

  public void setQos(int qos) {
    this.qos = qos;
  }
}
