#!/usr/bin/with-contenv bashio

MQTT_BROKER=$(bashio::services mqtt "host")
MQTT_USER=$(bashio::services mqtt "username")
MQTT_PASSWORD=$(bashio::services mqtt "password")

java -jar app.jar
