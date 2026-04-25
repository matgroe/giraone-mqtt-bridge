package de.matgroe;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix="application")
public class GiraOneMqttApplicationProperties {
    @Value("${application.name}")
    @NotEmpty
    private String name;

    @Value("${application.url}")
    private String url;

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
