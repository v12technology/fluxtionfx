/*
 * Copyright (C) 2017 Greg Higgins (greg.higgins@V12technology.com)
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
package com.fluxtion.fx.reconciler.input;

import com.fluxtion.fx.event.ListenerRegisration;
import com.fluxtion.fx.event.TimingPulseEvent;
import com.fluxtion.fx.reconciler.ReconcileController;
import com.fluxtion.fx.reconciler.ReconcileSink;
import com.fluxtion.fx.reconciler.events.ControlSignals;
import static com.fluxtion.fx.reconciler.events.ControlSignals.CLEAR_CACHE_ACTION;
import static com.fluxtion.fx.reconciler.events.ControlSignals.PUBLISH_SUMMARY_ACTION;
import com.fluxtion.fx.reconciler.events.TradeAcknowledgement;
import com.fluxtion.fx.reconciler.extensions.ReconcileReportPublisher;
import com.fluxtion.fx.reconciler.extensions.ReconcileSummaryListener;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.lifecycle.EventHandler;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.MethodReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A reconciler facade that will write reconcile events to chronicle queue and
 * execute in a different thread. Events that cannot be serialised can be
 * executed with executeEventSynchronously, and will execute on the same
 * invoking thread as the chronicle event reader/dispatcher.
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class ChronicleQueueSource implements ReconcileController {

    private int sleepBetweenChronicleReads;
    private ScheduledExecutorService eventExecutor;
    private SingleChronicleQueue queue;
    private EventHandler eventHandler;
    private MethodReader methodReader;
    private ExcerptTailer tailer;
    private static final Logger LOGGER = LogManager.getFormatterLogger(ChronicleQueueSource.class);

    @Override
    public void run() {
        TimingPulseEvent pulse = new TimingPulseEvent(1);
        if (eventExecutor == null || eventExecutor.isShutdown()) {
            LOGGER.info("starting queue reader task");
            eventExecutor = Executors.newSingleThreadScheduledExecutor();
            replay();
            eventExecutor.scheduleAtFixedRate(() -> {
                while (methodReader.readOne()) {
                }
            }, sleepBetweenChronicleReads, sleepBetweenChronicleReads, TimeUnit.MILLISECONDS);
            eventExecutor.scheduleAtFixedRate(() -> {
                pulse.currentTimeMillis = System.currentTimeMillis();
                eventHandler.onEvent(pulse);
            }, sleepBetweenChronicleReads, sleepBetweenChronicleReads, TimeUnit.MILLISECONDS);

        }
    }

    @Override
    public void stop() throws InterruptedException {
        eventExecutor.awaitTermination(5, TimeUnit.SECONDS);
        eventExecutor = null;
    }

    @Override
    public void replay() {
        LOGGER.info("replaying events into reconciler");
        long now = System.currentTimeMillis();
        LongAdder adder = new LongAdder();
        executeAndWait(() -> {
            eventHandler.onEvent(CLEAR_CACHE_ACTION);
            tailer.toStart();
            while (methodReader.readOne()) {
                adder.increment();
            }
        });
        LOGGER.info("finished replaying %d events into reconciler in %d millis", adder.intValue() , (System.currentTimeMillis() - now) );
    }

    @Override
    public void clear() {
        executeAndWait(() -> {
            eventHandler.onEvent(CLEAR_CACHE_ACTION);
            queue.clear();
            tailer.toStart();
        });

    }

    private void executeAndWait(Runnable run) {
        try {
            if (eventExecutor == null || eventExecutor.isShutdown()) {
                run.run();
            } else {
                eventExecutor.submit(run).get();
            }
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException("could not exectue synchronous event", ex);
        }

    }

    /**
     * Execute an event synchronously, can be useful for events that contain
     * data that does not serialise such as passing references for listeners.
     * The synchronous events will be interleaved in the queued event stream
     * ensuring only one thread is pushing events into the
     *
     * @param e
     */
    public void executeEventSynchronously(Event e) {
        try {
            if (eventExecutor == null || eventExecutor.isShutdown()) {
                eventHandler.onEvent(e);
            } else {
                eventExecutor.submit(() -> eventHandler.onEvent(e)).get();
            }
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException("could not exectue synchronous event", ex);
        }
    }

    @Override
    public void publishSummaryUpdate() {
        executeEventSynchronously(PUBLISH_SUMMARY_ACTION);
    }

    @Override
    public void publishReports() {
        executeEventSynchronously(ControlSignals.PUBLISH_REPORT_ACTION);
    }

    @Override
    public void enableOutput(boolean enable) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void registerReconcileSummaryListener(ReconcileSummaryListener listener) {
        executeEventSynchronously(new ListenerRegisration<>(listener, ReconcileSummaryListener.class));
    }

    @Override
    public void registerReconcileReportPublisher(ReconcileReportPublisher listener) {
        executeEventSynchronously(new ListenerRegisration<>(listener, ReconcileReportPublisher.class));
    }

    public static class ChronicleQueueSourceBuilder {

        private final ChronicleQueueSource source = new ChronicleQueueSource();

        public ChronicleQueueSourceBuilder batchReadDelay(int milliseconds) {
            if (milliseconds < 1 || milliseconds > 25000) {
                throw new IllegalArgumentException("millisseond sleep between chronicle queue reads must be greater "
                        + "than o and less than 25,000");
            }
            source.sleepBetweenChronicleReads = milliseconds;
            return this;
        }

        public ChronicleQueueSourceBuilder eventHandler(EventHandler eventHandler) {
            source.eventHandler = eventHandler;
            return this;
        }

        public ChronicleQueueSourceBuilder chronicleFile(String filePath) {
            File queuePath = new File(filePath);
            try {
                LOGGER.info("storing queue at:" + queuePath.getCanonicalPath());
            } catch (IOException ex) {
                LOGGER.error("could not read queue path", ex);
            }
            source.queue = SingleChronicleQueueBuilder.binary(queuePath).build();
            source.tailer = source.queue.createTailer();
            source.methodReader = source.tailer.methodReader(new ReconcileSink() {
                @Override
                public void processTradeAcknowledgement(TradeAcknowledgement acknowledgedment) {
                    source.eventHandler.onEvent(acknowledgedment);
                }

                @Override
                public void timeUpdate(TimingPulseEvent timingEvent) {
                    source.eventHandler.onEvent(timingEvent);
                }
            });
            return this;
        }

        public ChronicleQueueSource build() {
            Objects.requireNonNull(source.queue, "must specify file location for chronicle queue");
            Objects.requireNonNull(source.eventHandler, "must provide an EventHandler to process messages");
            if (source.eventHandler instanceof Lifecycle) {
                ((Lifecycle) source.eventHandler).init();
            }
            return source;
        }

    }

}
