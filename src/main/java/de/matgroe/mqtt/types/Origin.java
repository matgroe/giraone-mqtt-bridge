package de.matgroe.mqtt.types;

import com.google.gson.annotations.SerializedName;

public class Origin {
    @SerializedName("name")
    private String name;

    @SerializedName("sw")
    private String swVersion;

    @SerializedName("url")
    private String supportUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSwVersion() {
        return swVersion;
    }

    public void setSwVersion(String swVersion) {
        this.swVersion = swVersion;
    }

    public String getSupportUrl() {
        return supportUrl;
    }

    public void setSupportUrl(String supportUrl) {
        this.supportUrl = supportUrl;
    }
}
