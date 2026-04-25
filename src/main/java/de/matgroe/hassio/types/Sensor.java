package de.matgroe.hassio.types;

import com.google.gson.annotations.SerializedName;

public class Sensor extends Component {

    @SerializedName("state_class")
    protected String stateClass;

    @SerializedName("unit_of_measurement")
    protected String unitOfMeasurement;

    @SerializedName("suggested_display_precision")
    protected String suggestedDisplayPrecision;

    public Sensor() {
        this.platform = "sensor";
        this.stateClass = "measurement";
    }

    public String getStateClass() {
        return stateClass;
    }

    public void setStateClass(String stateClass) {
        this.stateClass = stateClass;
    }

    public String getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    public void setUnitOfMeasurement(String unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
    }

    public String getSuggestedDisplayPrecision() {
        return suggestedDisplayPrecision;
    }

    public void setSuggestedDisplayPrecision(String suggestedDisplayPrecision) {
        this.suggestedDisplayPrecision = suggestedDisplayPrecision;
    }
}
