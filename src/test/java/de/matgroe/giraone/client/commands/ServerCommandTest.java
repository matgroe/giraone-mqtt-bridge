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
package de.matgroe.giraone.client.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link de.matgroe.giraone.client.websocket.GiraOneWebsocketRequest} {@link RegisterApplication}
 * 
 * @author Matthias Groeger - Initial contribution
 */
class ServerCommandTest {
    private static final String APP_ID = "APP_ID_123";
    private static final String APP_TYPE = "APP_TYPE";
    private static final String INSTANCE_ID = "InstanceId";
    private static final String URN = "junit:test:blah-blah";

    @Test
    void shouldBuildGiraOneCommandRegisterApplication() {
        RegisterApplication cmd = RegisterApplication.builder().with(RegisterApplication::setApplicationId, APP_ID)
                .with(RegisterApplication::setApplicationType, APP_TYPE)
                .with(RegisterApplication::setInstanceId, INSTANCE_ID).build();

        assertEquals("RegisterApplication", cmd.getCommand());
        assertEquals(APP_ID, cmd.getApplicationId());
        assertEquals(APP_TYPE, cmd.getApplicationType());
        assertEquals(INSTANCE_ID, cmd.getInstanceId());
    }

    @Test
    void shouldBuildGiraOneCommandGetUIConfiguration() {
        GetUIConfiguration cmd = GetUIConfiguration.builder().with(GetUIConfiguration::setGuid, INSTANCE_ID).build();
        assertEquals("GetUIConfiguration", cmd.getCommand());
    }

    @Test
    void shouldBuildGiraOneCommandGetValue() {
        GetValue cmd = GetValue.builder().with(GetValue::setUrn, URN).build();
        assertEquals("GetValue", cmd.getCommand());
        assertEquals(URN, cmd.getUrn());
        assertNull(cmd.getId());
    }
}
