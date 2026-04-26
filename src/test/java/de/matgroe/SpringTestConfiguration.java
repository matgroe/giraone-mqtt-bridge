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

import static org.mockito.Mockito.when;

import de.matgroe.giraone.GiraOneTestDataProvider;
import de.matgroe.giraone.client.GiraOneClient;
import de.matgroe.giraone.client.types.GiraOneValue;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuration Bean for Spring Tests */
@Configuration
public class SpringTestConfiguration {

  @Bean("giraInboundMessages")
  Subject<GiraOneValue> giraInbound() {
    return PublishSubject.create();
  }

  @Bean("giraOutboundMessages")
  Subject<GiraOneValue> giraOutbound() {
    return PublishSubject.create();
  }

  @Bean
  GiraOneClient mockGiraOneClient() {
    GiraOneClient giraOneClient = Mockito.mock(GiraOneClient.class);
    when(giraOneClient.getGiraOneProject())
        .thenReturn(GiraOneTestDataProvider.createGiraOneProject());
    when(giraOneClient.lookupGiraOneDeviceConfiguration())
        .thenReturn(GiraOneTestDataProvider.createGiraOneDeviceConfiguration());
    return giraOneClient;
  }
}
