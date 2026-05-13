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
package de.matgroe;

import de.matgroe.bridge.GiraOneMqttBridge;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GiraOneMqttApplication implements CommandLineRunner {
  private final Logger logger = LoggerFactory.getLogger(GiraOneMqttApplication.class);

  @Autowired private GiraOneMqttBridge theBridge;

  public static void main(String[] args) {
    try {
      SpringApplication.run(GiraOneMqttApplication.class, args);
    } catch (UnsatisfiedDependencyException e) {
      dumpEnvironmentInfo(args);
    }
  }

  private static void dumpEnvironmentInfo(String... args) {
    Logger logger = LoggerFactory.getLogger(GiraOneMqttApplication.class);
    for (String arg : args) {
      logger.debug("Argument: {}", arg);
    }

    Map<String, String> env = System.getenv();
    for (String envName : env.keySet()) {
      logger.debug("Environment: {}={}", envName, env.get(envName));
    }
  }

  public void run(String... args) throws Exception {
    dumpEnvironmentInfo(args);
    theBridge.initialize();
    {
      Thread.sleep(2000);
    }
    while (theBridge.isExecuteable())
      ;
    System.exit(1);
  }
}
