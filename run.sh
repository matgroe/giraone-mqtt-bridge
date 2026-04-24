#!/usr/bin/env bashio
echo
echo '###########################################################################################################'
echo '   Starting Container '

export

if ! bashio::services.available "mqtt" && ! bashio::config.exists 'mqtt_server'; then
    bashio::exit.nok "No internal MQTT service found and no MQTT server defined. Please install Mosquitto broker or specify your own."
else
    bashio::log.info "MQTT available, fetching server detail ..."
fi
#export MQTT_BROKER=$(bashio::services mqtt "host")
#export MQTT_PORT=$(bashio::services mqtt "port")
#export MQTT_USER=$(bashio::services mqtt "username")
#export MQTT_PASSWORD=$(bashio::services mqtt "password")

#export XMQTT_BROKER=$(bashio::services mqtt "host")
#export XMQTT_USER=$(bashio::services mqtt "username")
#export XMQTT_PASSWORD=$(bashio::services mqtt "password")


java -jar app.jar
