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
