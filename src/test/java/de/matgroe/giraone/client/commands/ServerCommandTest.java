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
package de.matgroe.giraone.client.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link de.matgroe.giraone.client.websocket.GiraOneWebsocketRequest} {@link
 * RegisterApplication}
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
    RegisterApplication cmd =
        RegisterApplication.builder()
            .with(RegisterApplication::setApplicationId, APP_ID)
            .with(RegisterApplication::setApplicationType, APP_TYPE)
            .with(RegisterApplication::setInstanceId, INSTANCE_ID)
            .build();

    assertEquals("RegisterApplication", cmd.getCommand());
    assertEquals(APP_ID, cmd.getApplicationId());
    assertEquals(APP_TYPE, cmd.getApplicationType());
    assertEquals(INSTANCE_ID, cmd.getInstanceId());
  }

  @Test
  void shouldBuildGiraOneCommandGetUIConfiguration() {
    GetUIConfiguration cmd =
        GetUIConfiguration.builder().with(GetUIConfiguration::setGuid, INSTANCE_ID).build();
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
