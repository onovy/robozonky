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

package com.github.triceo.robozonky.app.investing;

import java.util.OptionalInt;

public class ZonkyResponse {

    private final ZonkyResponseType type;
    private final Integer confirmedAmount;

    public ZonkyResponse(final ZonkyResponseType responseType) {
        this.type = responseType;
        this.confirmedAmount = null;
    }

    public ZonkyResponse(final int confirmedAmount) {
        this.type = ZonkyResponseType.INVESTED;
        this.confirmedAmount = confirmedAmount;
    }

    /**
     *
     * @return The type of response received from the investment routine.
     */
    public ZonkyResponseType getType() {
        return type;
    }

    /**
     *
     * @return Will contain the confirmed invested amount if and only if {@link #getType()} returns
     * {@link ZonkyResponseType#INVESTED}.
     */
    public OptionalInt getConfirmedAmount() {
        return confirmedAmount == null ? OptionalInt.empty() : OptionalInt.of(confirmedAmount);
    }
}
