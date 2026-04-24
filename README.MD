# GiraOne Bridge
The GiraOneBridge connects to a GiraOne Home Server on one side and an MQTT Broker on the other side and acts as a 
message bridge between the two systems. 

## Building
### Build Application
> mvn package
 
### Create Docker Image
> docker build -t matgroe/hassio-giraone-mqtt-bridge .

### Run Docker Image
>  docker run -v /home/matthias/workspace/giraone-bridge/:/config/  docker.io/matgroe/hassio-giraone-mqtt-bridge:latest --spring.config.import=/config/application.yaml

#### Configuration
The Application as executed within a docker container need to be configured via environment variables.

| Variable      | Mandatory | Default | Description                                                       |
|---------------|-----------|---------|-------------------------------------------------------------------|
| GIRA_SERVER   | Yes       |         | GiraOneServer's hostname or IP of within your network.            |
| GIRA_USERNAME | Yes       |         | Username to auth against GiraOneServer                            |
| GIRA_PASSWORD | Yes       |         | Password                                                          |
|               |           |         |                                                                   |
| MQTT_BROKER   | Yes       |         | MQTT Broker's hostname or IP of within your network.              |
| MQTT_PORT     | No        | 1883    | MQTT Broker's portnumber                                          |
| MQTT_USERNAME | Yes       |         | Username                                                          |
| MQTT_PASSWORD | Yes       |         | Password                                                          |
|               |           |         |
| LOG_LEVEL     | No        | INFO    | Application Loglevel. One of TRACE, DEBUG, INFO, WARN, ERROR, OFF |
