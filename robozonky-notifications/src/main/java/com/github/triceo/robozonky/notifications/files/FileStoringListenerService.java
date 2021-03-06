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

package com.github.triceo.robozonky.notifications.files;

import java.util.Objects;

import com.github.triceo.robozonky.api.Refreshable;
import com.github.triceo.robozonky.api.notifications.Event;
import com.github.triceo.robozonky.api.notifications.EventListener;
import com.github.triceo.robozonky.api.notifications.InvestmentDelegatedEvent;
import com.github.triceo.robozonky.api.notifications.InvestmentMadeEvent;
import com.github.triceo.robozonky.api.notifications.InvestmentRejectedEvent;
import com.github.triceo.robozonky.api.notifications.ListenerService;

public final class FileStoringListenerService implements ListenerService {

    @Override
    public <T extends Event> Refreshable<EventListener<T>> findListener(final Class<T> eventType) {
        if (Objects.equals(eventType, InvestmentMadeEvent.class)) {
            return Refreshable.createImmutable((EventListener<T>) new InvestmentMadeEventListener());
        } else if (Objects.equals(eventType, InvestmentRejectedEvent.class)) {
            return Refreshable.createImmutable((EventListener<T>) new InvestmentRejectedEventListener());
        } else if (Objects.equals(eventType, InvestmentDelegatedEvent.class)) {
            return Refreshable.createImmutable((EventListener<T>) new InvestmentDelegatedEventListener());
        }
        return Refreshable.createImmutable(null);
    }
}
