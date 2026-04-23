FROM alpine/java:21-jre
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

LABEL \
    io.hass.type=app
    io.hass.name="The Name" \
    io.hass.description="Bridge between GiraOne Server and MQTT." \
    org.opencontainers.image.title="Home Assistant App: GiraOneMQTTBridge" \
    org.opencontainers.image.description="Bridge between GiraOne Server and MQTT." \
    org.opencontainers.image.source="https://github.com/home-assistant/apps-example" \
    org.opencontainers.image.licenses="Apache License 2.0"
