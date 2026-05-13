/*
 * MIT License
 *
 * Copyright (c) 2026 Matthias Gröger
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
  @ValueSource(strings = {"WC - Deckenlicht", "Büro Raffstore Eckfenster Strasse"})
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
    int initialSize = project.lookupChannels().size();

    project.addChannel(GiraOneTestDataProvider.createGiraOneChannel(urn));
    assertEquals(initialSize + 1, project.lookupChannels().size());

    project.addChannel(GiraOneTestDataProvider.createGiraOneChannel(urn));
    assertEquals(initialSize + 1, project.lookupChannels().size());

    project.addChannel(GiraOneTestDataProvider.createGiraOneChannel(urn + "1"));
    assertEquals(initialSize + 2, project.lookupChannels().size());
  }
}
