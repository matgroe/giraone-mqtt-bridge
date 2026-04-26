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
package de.matgroe.giraone.client.webservice;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.matgroe.giraone.GiraOneClientProperties;
import de.matgroe.giraone.client.GiraOneClientException;
import de.matgroe.giraone.client.GiraOneCommand;
import de.matgroe.giraone.client.GiraOneCommandResponse;
import de.matgroe.giraone.client.GiraOneCommunicationException;
import de.matgroe.giraone.client.GiraOneTypeMapperFactory;
import de.matgroe.giraone.client.commands.AuthenticateSession;
import de.matgroe.giraone.client.commands.DiagnosticGetValue;
import de.matgroe.giraone.client.commands.DiagnosticSetValue;
import de.matgroe.giraone.client.commands.GetDiagnosticDeviceList;
import de.matgroe.giraone.client.commands.GetPasswordSalt;
import de.matgroe.giraone.client.types.GiraOneComponentCollection;
import de.matgroe.giraone.client.types.GiraOneDataPoint;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class gives access the the Gira One Server as offered by the http interface.
 *
 * @author Matthias Gröger - Initial contribution
 */
public class GiraOneWebserviceClient {
  private static final String ERR_COMMUNICATION = "ERR_COMMUNICATION";
  private static final int ERR_COMMUNICATION_CODE = 10000;

  private static final String TEMPLATE_WEBSERVICE_URL = "http://%s/webservice";

  private final Logger logger = LoggerFactory.getLogger(GiraOneWebserviceClient.class);
  private final GiraOneClientProperties clientConfiguration;
  private final HttpClient.Builder clientBuilder;
  private final URI webserviceUri;
  private final Gson gson;

  /**
   * Constructor
   *
   * @param config A {@link GiraOneClientProperties} object
   */
  public GiraOneWebserviceClient(final GiraOneClientProperties config) {
    Objects.requireNonNull(config.hostname, "GiraOneClientProperties 'hostname' must not be null");
    Objects.requireNonNull(config.username, "GiraOneClientProperties 'username' must not be null");
    Objects.requireNonNull(config.password, "GiraOneClientProperties 'password' must not be null");
    this.clientConfiguration = config;
    this.gson = GiraOneTypeMapperFactory.createGson();
    this.clientBuilder =
        HttpClient.newBuilder()
            .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
            .proxy(ProxySelector.getDefault());
    try {
      this.webserviceUri =
          new URI(String.format(TEMPLATE_WEBSERVICE_URL, this.clientConfiguration.hostname));
    } catch (URISyntaxException e) {
      throw new GiraOneClientException("Cannot format webservice URI", e);
    }
  }

  /**
   * Establish a new Websocket connection to the Gira One Server.
   *
   * @throws GiraOneClientException
   */
  public void connect() throws GiraOneCommunicationException {
    this.authenticateUser(clientConfiguration.username, clientConfiguration.password);
  }

  protected String doPost(final String body) throws IOException {
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(this.webserviceUri)
            .headers("Content-Type", "text/plain;charset=UTF-8")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

    try (HttpClient client = clientBuilder.build()) {
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() != 200) {
        throw new IOException(
            String.format(
                "The HTTP Post to {%s} failed with status code {%d}",
                this.webserviceUri, response.statusCode()));
      }
      return response.body();
    } catch (Exception e) {
      throw new IOException(String.format("Cannot post {%s} to {%s}", body, this.webserviceUri), e);
    }
  }

  /**
   * Sends a {@link GiraOneCommand} to Gira One Server and provides server's command response as
   * {@link GiraOneCommandResponse} object.
   *
   * @param command The command to send
   * @throws {@link GiraOneCommunicationException} in case of an error.
   */
  public GiraOneCommandResponse execute(GiraOneCommand command)
      throws GiraOneCommunicationException {
    try {
      String message =
          gson.toJson(new GiraOneWebserviceRequest(command), GiraOneWebserviceRequest.class);
      logger.trace("SEND command :: {}", message);
      String body = this.doPost(message);

      logger.trace("RCV command response :: {}", body);
      JsonElement responseElement = JsonParser.parseString(body);
      if (responseElement != null && responseElement.isJsonObject()) {
        JsonObject responseObject = responseElement.getAsJsonObject();
        if (responseObject.has("error")) {
          throw new GiraOneCommunicationException(
              command,
              responseObject.get("error").getAsString(),
              responseObject.get("id").getAsInt());
        }
        return gson.fromJson(body, GiraOneWebserviceResponse.class);
      }
    } catch (IOException exp) {
      throw new GiraOneCommunicationException(command, exp.getMessage(), exp);
    }
    throw new GiraOneCommunicationException(command, ERR_COMMUNICATION, ERR_COMMUNICATION_CODE);
  }

  /**
   * initiates a http session on gira server and authenticates against the given user credentials
   *
   * @param username - the GiraOne User
   * @param password - the user's password
   */
  private void authenticateUser(final String username, final String password)
      throws GiraOneCommunicationException {
    GiraOneCommandResponse saltAsJson =
        this.execute(
            GetPasswordSalt.builder().with(GetPasswordSalt::setUsername, username).build());

    GiraOneWebserviceSession session = saltAsJson.getReply(GiraOneWebserviceSession.class);

    GiraOneWebserviceAuthentication auth = new GiraOneWebserviceAuthentication();
    String token = auth.computeAuthToken(session, password);

    this.execute(
        AuthenticateSession.builder()
            .with(AuthenticateSession::setUsername, username)
            .with(AuthenticateSession::setToken, token)
            .build());
  }

  public GiraOneComponentCollection lookupGiraOneComponentCollection()
      throws GiraOneCommunicationException {
    GiraOneCommandResponse response = this.execute(GetDiagnosticDeviceList.builder().build());
    return response.getReply(GiraOneComponentCollection.class);
  }

  public void changeGiraOneDataPointValue(final GiraOneDataPoint dataPoint, Object value)
      throws GiraOneCommunicationException {
    this.execute(
        DiagnosticSetValue.builder()
            .with(DiagnosticSetValue::setValue, value)
            .with(DiagnosticSetValue::setUrn, dataPoint.getUrn())
            .build());
  }

  public Object readGiraOneDataPointValue(final GiraOneDataPoint dataPoint)
      throws GiraOneCommunicationException {
    return this.execute(
        DiagnosticGetValue.builder().with(DiagnosticGetValue::setUrn, dataPoint.getUrn()).build());
  }
}
