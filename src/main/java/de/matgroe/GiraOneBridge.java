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
import de.matgroe.mqtt.GiraOneMqttBridge;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.function.Consumer;

@SpringBootApplication
public class GiraOneBridge implements CommandLineRunner {
    private final static String GDS_DEVICE_CHANNEL_URN = "urn:gds:dp:GiraOneServer.GIOSRVKX03:GDS-Device-Channel";
    private final static String GDS_DEVICE_DATAPOINT_READY = "Ready";
    private final static String GDS_DEVICE_DATAPOINT_LOCAL_TIME = "Local-Time";

    private final Logger logger = LoggerFactory.getLogger(GiraOneBridge.class);
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Autowired
    GiraOneClient giraOneClient;

    @Autowired
    GiraOneMqttBridge giraOneMqttBridge;

    @Autowired
    @Qualifier("giraInboundMessages")
    Subject<GiraOneValue> giraInbound;

    @Autowired
    @Qualifier("giraOutboundMessages")
    Subject<GiraOneValue> giraOutbound;

    public static void main(String[] args) {
        SpringApplication.run(GiraOneBridge.class, args);
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
            // Note: When initialization can NOT be done set the status with more details for further
            // analysis. See also class ThingStatusDetail for all available status details.
            // Add a description to give user information to understand why thing does not work as expected. E.g.
            // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
            // "Can not access device as username and/or password are invalid");
            //updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, exp.getMessage());
        }
        Thread.currentThread().join();
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
            case Disconnected -> giraOneMqttBridge.disconnect();
        }
    }

    void initializeGiraOneMqttBridge() {
        // Register at GiraOneClient to get all received Values and Events
        disposables.add(giraOneClient.observeGiraOneValues(this::onGiraOneValue));
        giraOneMqttBridge.connect();
        Thread thread = Thread.ofVirtual().start(() -> {
            giraOneClient.getGiraOneProject().lookupGiraOneDataPoints().forEach(giraOneClient::lookupGiraOneDatapointValue);
        });
    }

    void onGiraOneValue(GiraOneValue giraOneValue) {
        logger.info("onGiraOneValue :: {}", giraOneValue);
        giraOneMqttBridge.publish(giraOneValue);
    }

    public Disposable subscribeOnGiraOneDataPointValues(final String deviceUrnPattern, Consumer<GiraOneValue> consumer) {
        //return this.datapointValues.filter(f -> f.getDatapointUrn().matches(deviceUrnPattern)).subscribe(consumer);
        return Disposable.empty();
    }

}