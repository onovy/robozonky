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

package com.github.triceo.robozonky.api.notifications;

import java.util.Collection;
import java.util.Collections;

import com.github.triceo.robozonky.api.strategies.LoanDescriptor;

/**
 * Fired immediately before the loans are submitted to the investing algorithm. May be followed by
 * {@link StrategyStartedEvent}, will eventually be followed by {@link ExecutionCompletedEvent}.
 */
public final class ExecutionStartedEvent extends Event {

    private final String username;
    private final Collection<LoanDescriptor> loanDescriptors;
    private final int balance;

    public ExecutionStartedEvent(final String username, final Collection<LoanDescriptor> loanDescriptors,
                                 final int balance) {
        this.username = username;
        this.loanDescriptors = Collections.unmodifiableCollection(loanDescriptors);
        this.balance = balance;
    }

    public String getUsername() {
        return this.username;
    }

    /**
     * @return Loans found on the marketplace that are available for robotic investment, not protected by CAPTCHA.
     */
    public Collection<LoanDescriptor> getLoanDescriptors() {
        return this.loanDescriptors;
    }

    /**
     *
     * @return Account balance at the beginning of the investment algorithm.
     */
    public int getBalance() {
        return this.balance;
    }

}
