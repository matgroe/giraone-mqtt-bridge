FROM ghcr.io/home-assistant/base:latest

# Install requirements for app
RUN \
  apk add --no-cache \
    openjdk21

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app.jar

COPY DockerEntrypoint.sh /entrypoint.sh
RUN chmod a+x /entrypoint.sh
CMD [ "/entrypoint.sh" ]

LABEL \
    io.hass.version="VERSION" \
    io.hass.type="app" \
    io.hass.arch="amd64" \
    io.hass.description="Bridge between GiraOne Server and MQTT." \
    org.opencontainers.image.licenses="Apache License 2.0"
