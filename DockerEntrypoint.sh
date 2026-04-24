#!/usr/bin/with-contenv bashio

echo
echo '###########################################################################################################'
echo '- Preparing Environment Settings'

export MQTT_HOST=$(bashio::config 'Mqtt_host')
export MQTT_PORT=$(bashio::config 'Mqtt_port')
export MQTT_USER=$(bashio::config 'Mqtt_user')
export MQTT_PASS=$(bashio::config 'Mqtt_pass')

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
