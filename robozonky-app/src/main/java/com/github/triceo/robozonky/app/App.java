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

package com.github.triceo.robozonky.app;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.triceo.robozonky.api.ReturnCode;
import com.github.triceo.robozonky.api.notifications.RoboZonkyStartingEvent;
import com.github.triceo.robozonky.app.configuration.CommandLineInterface;
import com.github.triceo.robozonky.app.investing.InvestmentMode;
import com.github.triceo.robozonky.app.notifications.Events;
import com.github.triceo.robozonky.app.util.RuntimeExceptionHandler;
import com.github.triceo.robozonky.app.util.Scheduler;
import com.github.triceo.robozonky.internal.api.Defaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * You are required to exit this app by calling {@link #exit(ReturnCode)}.
 */
public class App {

    static {
        // add process identification to log files
        MDC.put("process_id", ManagementFactory.getRuntimeMXBean().getName());
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static final File ROBOZONKY_LOCK = new File(System.getProperty("java.io.tmpdir"), "robozonky.lock");
    private static final ShutdownHook SHUTDOWN_HOOKS = new ShutdownHook();

    public static final Semaphore DAEMON_ALLOWED_TO_TERMINATE = new Semaphore(1);

    static void exit(final ReturnCode returnCode) {
        App.LOGGER.trace("Exit requested with return code {}.", returnCode);
        App.exit(returnCode, null);
    }

    /**
     * Will terminate the application. Call this on every exit of the app to ensure proper termination. Failure to do
     * so may result in unpredictable behavior of this instance of RoboZonky or future ones.
     * @param returnCode Will be passed to {@link System#exit(int)}.
     * @param cause Exception that caused the application to exit, if any.
     */
    static void exit(final ReturnCode returnCode, final Throwable cause) {
        App.SHUTDOWN_HOOKS.execute(returnCode, cause);
        System.exit(returnCode.getCode());
    }

    static ReturnCode execute(final InvestmentMode mode) {
        try {
            return mode.get().map(r -> {
                App.LOGGER.info("RoboZonky {}invested into {} loans.", mode.isDryRun() ? "would have " : "", r.size());
                return ReturnCode.OK;
            }).orElse(ReturnCode.ERROR_SETUP);
        } finally {
            try {
                mode.close();
            } catch (final Exception ex) {
                App.LOGGER.debug("Failed cleaning up post investing.", ex);
            }
        }
    }

    static ReturnCode execute(final String[] args, final AtomicBoolean faultTolerant) {
        return CommandLineInterface.parse(args)
                .map(mode -> {
                    // startup the MBeans
                    App.SHUTDOWN_HOOKS.register(new Management());
                    // pressing Ctrl+C in daemon mode must result in proper RoboZonky shutdown
                    App.SHUTDOWN_HOOKS.register(() -> {
                        App.DAEMON_ALLOWED_TO_TERMINATE.acquireUninterruptibly();
                        return Optional.of((returnCode -> App.DAEMON_ALLOWED_TO_TERMINATE.release()));
                    });
                    // start RoboZonky update check
                    App.SHUTDOWN_HOOKS.register(new VersionChecker());
                    // notify of RoboZonky starting up
                    App.SHUTDOWN_HOOKS.register(new RoboZonkyStartupNotifier());
                    faultTolerant.set(mode.isFaultTolerant());
                    return App.execute(mode);
                }).orElse(ReturnCode.ERROR_WRONG_PARAMETERS);
    }

    public static void main(final String... args) {
        // make sure other RoboZonky processes are excluded
        if (!App.SHUTDOWN_HOOKS.register(new Exclusivity(App.ROBOZONKY_LOCK))) {
            App.exit(ReturnCode.ERROR_LOCK);
        }
        // and actually start running
        App.LOGGER.debug("Current working directory is '{}'.", new File("").getAbsolutePath());
        Events.fire(new RoboZonkyStartingEvent(Defaults.ROBOZONKY_VERSION));
        App.LOGGER.debug("Running {} Java v{} on {} v{} ({}, {} CPUs, {}, {}).", System.getProperty("java.vendor"),
                System.getProperty("java.version"), System.getProperty("os.name"), System.getProperty("os.version"),
                System.getProperty("os.arch"), Runtime.getRuntime().availableProcessors(), Locale.getDefault(),
                Charset.defaultCharset());
        // start the check for new version, making sure it is properly handled during execute
        App.SHUTDOWN_HOOKS.register(() -> Optional.of(returnCode -> Scheduler.BACKGROUND_SCHEDULER.shutdown()));
        // read the command line and execute the runtime
        final AtomicBoolean faultTolerant = new AtomicBoolean(false);
        try { // execute core code
            App.exit(App.execute(args, faultTolerant));
        } catch (final Throwable throwable) {
            final RuntimeExceptionHandler handler = new AppRuntimeExceptionHandler(faultTolerant.get());
            handler.handle(throwable);
        }
    }

}
