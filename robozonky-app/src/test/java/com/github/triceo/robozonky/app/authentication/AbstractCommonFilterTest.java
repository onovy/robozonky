/*
 * Copyright 2017 Lukáš Petrovický
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.triceo.robozonky.app.authentication;

import java.io.IOException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.MultivaluedHashMap;

import com.github.triceo.robozonky.internal.api.Defaults;
import com.github.triceo.robozonky.internal.api.RoboZonkyFilter;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;

abstract class AbstractCommonFilterTest {

    protected abstract RoboZonkyFilter getTestedFilter();

    @Test
    public void wasUserAgentHeaderAdded() throws IOException {
        final ClientRequestContext crc = Mockito.mock(ClientRequestContext.class);
        Mockito.when(crc.getHeaders()).thenReturn(new MultivaluedHashMap<>());

        this.getTestedFilter().filter(crc);
        Assertions.assertThat(crc.getHeaders().getFirst("User-Agent")).isEqualTo(Defaults.ROBOZONKY_USER_AGENT);
    }

}
