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
package de.matgroe.giraone.client.types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.matgroe.giraone.GiraOneTestDataProvider;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test class for {@link GiraOneProject}.
 *
 * @author Matthias Groeger - Initial contribution
 */
class GiraOneProjectTest {

  @DisplayName("should find existing channel by channelViewUrn")
  @Test
  void shouldFindChannelByChannelViewUrn() {
    GiraOneProject project = GiraOneTestDataProvider.createGiraOneProject();
    String urn = "urn:gds:chv:KNXheating2Fcooling-Heating-Cooling-Switchable-9";
    Optional<GiraOneChannel> channel = project.lookupChannelByUrn(urn);
    assertTrue(channel.isPresent());
    assertEquals(urn, channel.get().getUrn());
  }

  @DisplayName("should find a channel by it's name")
  @ParameterizedTest
  @ValueSource(strings = {"WC Deckenlicht", "Eckfenster Bad Links"})
  void testLookupChannelByName(String name) {
    GiraOneProject project = GiraOneTestDataProvider.createGiraOneProject();
    Optional<GiraOneChannel> channel = project.lookupChannelByName(name.toLowerCase());
    assertFalse(channel.isEmpty());
    assertEquals(name, channel.get().getName());
  }

  @Test
  void testLookupGiraOneChannelDataPoints() {
    String urn =
        "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxHvacActuator6-gang-1.Heatingactuator-1:Set-Point";
    GiraOneProject project = GiraOneTestDataProvider.createGiraOneProject();
    GiraOneDataPoint dp = project.lookupGiraOneDataPoint(urn).orElse(null);
    assertNotNull(dp);
    assertEquals(
        GiraOneURN.of(
            "urn:gds:dp:GiraOneServer.GIOSRVKX03:KnxHvacActuator6-gang-1.Heatingactuator-1:Set-Point"),
        dp.getUrn());
    assertEquals("Set-Point", dp.getName());
  }

  @DisplayName("should store no GiraOneChannel duplicates")
  @Test
  void shouldStoreNoDuplicateChannels() {
    String urn = "urn:gds:chv:KNXheating2Fcooling-Heating-Cooling-Switchable-9";

    GiraOneProject project = new GiraOneProject();
    project.addChannel(GiraOneTestDataProvider.createGiraOneChannel(urn));
    assertEquals(1, project.lookupChannels().size());

    project.addChannel(GiraOneTestDataProvider.createGiraOneChannel(urn));
    assertEquals(1, project.lookupChannels().size());

    project.addChannel(GiraOneTestDataProvider.createGiraOneChannel(urn + "1"));
    assertEquals(2, project.lookupChannels().size());
  }
}
