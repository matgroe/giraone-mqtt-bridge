#!/usr/bin/with-contenv bashio

echo
echo '###########################################################################################################'
echo '- Preparing Environment Settings'

set

export GIRA_SERVER=$(bashio::config 'GIRA_SERVER')
export GIRA_USER=$(bashio::config 'GIRA_USER')
export GIRA_PASSWORD=$(bashio::config 'GIRA_PASSWORD')
export LOG_LEVEL=$(bashio::config 'LOG_LEVEL')


if ! bashio::services.available "mqtt"; then
   bashio::exit.nok "No internal MQTT Broker found. Please install Mosquitto broker."
else
    export MQTT_HOST=$(bashio::services mqtt "host")
    export MQTT_PORT=$(bashio::services mqtt "port")
    export MQTT_USER=$(bashio::services mqtt "username")
    export MQTT_PASS=$(bashio::services mqtt "password")
    bashio::log.info "Configured'$MQTT_HOST' mqtt broker."
fi

echo '- Environment Settings:'
export

echo '- Starting Application:'
java -jar app.jar
