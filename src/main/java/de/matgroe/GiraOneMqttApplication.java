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
