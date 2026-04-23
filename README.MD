# GiraOne Bridge
The GiraOneBridge connects to a GiraOne Home Server on one side and an MQTT Broker on the other side and acts as a 
message bridge between the two systems. 

## Building
### Create Docker Image
> mvn spring-boot:build-image 
### Run Docker Image
>  docker run -v /home/matthias/workspace/giraone-bridge/:/config/  docker.io/matgroe/giraone-bridge:latest --spring.config.import=/config/application.yaml
> 

