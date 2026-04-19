/*
 * GiraOne Bridge
 * Copyright (C) 2025 Matthias Gröger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.matgroe;

import de.matgroe.giraone.client.GiraOneClient;
import de.matgroe.giraone.client.GiraOneClientConnectionState;
import de.matgroe.giraone.client.GiraOneClientException;
import de.matgroe.giraone.client.types.GiraOneValue;
import de.matgroe.mqtt.MqttClient;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class GiraOneMqttBridge {
    private final static String GDS_DEVICE_CHANNEL_URN = "urn:gds:dp:GiraOneServer.GIOSRVKX03:GDS-Device-Channel";
    private final static String GDS_DEVICE_DATAPOINT_READY = "Ready";
    private final static String GDS_DEVICE_DATAPOINT_LOCAL_TIME = "Local-Time";

    private final Logger logger = LoggerFactory.getLogger(GiraOneMqttBridge.class);
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final GiraOneClient giraOneClient;
    private final MqttClient mqttClient;

    public GiraOneMqttBridge(GiraOneClient giraOneClient, MqttClient mqttClient) {
        this.giraOneClient = giraOneClient;
        this.mqttClient = mqttClient;
    }

    public boolean isExecuteable() {
        return true;
    }

    public void run(String... args) throws Exception {
        // Register at GiraOneClient for all Exceptions
        disposables.add(giraOneClient.observeOnGiraOneClientExceptions(this::onGiraOneClientException));

        // Register for ConnectionState changes
        disposables.add(this.giraOneClient.observeGiraOneConnectionState(this::onGiraOneClientConnectionStateChanged));

        // Register for GiraServer State Updates like time
        disposables.add(subscribeOnGiraOneDataPointValues(String.format("%s:.*", GDS_DEVICE_CHANNEL_URN), this::onDeviceChannelEvent));
        try {
            this.giraOneClient.connect();

        } catch (GiraOneClientException exp) {

        }
    }

    private void onDeviceChannelEvent(GiraOneValue value) {
        logger.info("onDeviceChannelEvent:: {}", value);
    }

    private void onGiraOneClientException(GiraOneClientException clientException) {
        logger.error("GiraOneClientException {}", clientException.getMessage(), clientException);
    }

    /**
     * Observing function for client connection state changes
     *
     * @param connectionState The {@link GiraOneClient}'s connection state.
     */
    void onGiraOneClientConnectionStateChanged(GiraOneClientConnectionState connectionState) {
        logger.info("GiraOneClientConnectionState changed to {}", connectionState);
        switch (connectionState) {
            case Connected -> initializeGiraOneMqttBridge();
            case Disconnected -> mqttClient.disconnect();
        }
    }

    void initializeGiraOneMqttBridge() {
        // Register at GiraOneClient to get all received Values and Events
        disposables.add(giraOneClient.observeGiraOneValues(this::onGiraOneValue));
        mqttClient.connect();
        Thread thread = Thread.ofVirtual().start(() -> {
            giraOneClient.getGiraOneProject().lookupGiraOneDataPoints().forEach(giraOneClient::lookupGiraOneDatapointValue);
        });
    }

    void onGiraOneValue(GiraOneValue giraOneValue) {
        logger.info("onGiraOneValue :: {}", giraOneValue);
        mqttClient.publish(giraOneValue);
    }

    public Disposable subscribeOnGiraOneDataPointValues(final String deviceUrnPattern, Consumer<GiraOneValue> consumer) {
        //return this.datapointValues.filter(f -> f.getDatapointUrn().matches(deviceUrnPattern)).subscribe(consumer);
        return Disposable.empty();
    }

}