/*
 * Copyright 2016 Lukáš Petrovický
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

package com.github.triceo.robozonky.app.version;

import java.io.FileNotFoundException;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class VersionRetrieverTest {

    @Test
    public void checkRetrieval() throws Exception {
        final VersionRetriever v = new VersionRetriever("com.github.triceo.robozonky", "robozonky-app");
        Assertions.assertThat(v.call()).isNotNull();
    }

    @Test(expected = FileNotFoundException.class)
    public void checkNonExistentUrl() throws Exception {
        new VersionRetriever("com.github.triceo.robozonky", "robozonky-nonexistent").call();
    }

}
