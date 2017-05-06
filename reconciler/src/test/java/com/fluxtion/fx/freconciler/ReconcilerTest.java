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
package com.fluxtion.fx.freconciler;

import com.fluxtion.fx.event.ListenerRegisration;
import com.fluxtion.fx.event.TimingPulseEvent;
import com.fluxtion.fx.freconciler.SummaryListener.SummaryDetails;
import com.fluxtion.fx.reconciler.events.ConfigEvents;
import com.fluxtion.fx.reconciler.events.ControlSignals;
import com.fluxtion.fx.reconciler.events.TradeAcknowledgement;
import com.fluxtion.fx.reconciler.extensions.ReconcileReportPublisher;
import com.fluxtion.fx.reconciler.extensions.ReconcileStatusCache;
import com.fluxtion.fx.reconciler.extensions.ReconcileSummaryListener;
import com.fluxtion.fx.reconciler.helpers.SynchronousJsonReportPublisher;
import com.fluxtion.fx.reconciler.helpers.ConcurrentHashMapReconcileCache;
import com.fluxtion.fx.reconciler.helpers.ReconcileStatus;
import static com.fluxtion.fx.reconciler.helpers.ReconcileStatus.Status.EXPIRED_RECONCILE;
import static com.fluxtion.fx.reconciler.helpers.ReconcileStatus.Status.RECONCILED;
import static com.fluxtion.fx.reconciler.helpers.ReconcileStatus.Status.RECONCILING;
import com.fluxtion.fx.reconciler.helpers.ReportConfiguration;
import com.fluxtion.fx.reconciler.nodes.ReconcileCache;
import com.fluxtion.fx.reconciler.nodes.TradeReconciler;
import com.fluxtion.runtime.lifecycle.EventHandler;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import static java.util.Arrays.stream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import net.vidageek.mirror.dsl.Mirror;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.collection.IsArrayContainingInAnyOrder.arrayContainingInAnyOrder;
import static org.hamcrest.text.IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Tests for
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class ReconcilerTest {

    private EventHandler reconciler;
    private ReconcileCache cacheManager;
    private ReconcileStatusCache cacheTarget;
    private Map<TestReconcileCache.ReconcileKey, ReconcileStatus> key2Status;

    @Before
    public void beforeTest() throws Exception {
        reconciler = (EventHandler & Lifecycle) Class.forName("com.fluxtion.fx.reconciler.test.generated.ReconcilerTest").newInstance();
        ((Lifecycle) reconciler).init();
        Field field = new Mirror().on(reconciler.getClass()).reflect().field("reconcileCache_Global");
        field.setAccessible(true);
        cacheManager = (ReconcileCache) field.get(reconciler);
        cacheTarget = new ConcurrentHashMapReconcileCache();
        reconciler.onEvent(new ListenerRegisration(cacheTarget, ReconcileStatusCache.class));
        key2Status = ((ConcurrentHashMapReconcileCache) cacheTarget).key2Status;
    }

    @Test
    public void testReconcilerIds() {
        assertThat(stream(cacheManager.reconcilers).map(i -> i.id).toArray(),
                arrayContainingInAnyOrder("REUTERS_DC1", "EBS_LD4", "EBS_NY2", "FXALL_NY3", "MIDDLE_OFFICE"));
    }

    @Test
    public void testSimpleReconcile() {
        cacheSize(0);
        reconciler.onEvent(new TradeAcknowledgement("NY_2", 200, 2000));
        cacheSize(1);
        recordStatus(RECONCILING, "EBS_NY2", 200);
        reconciler.onEvent(new TradeAcknowledgement("MiddleOffice_NY2", 200, 3000));
        recordStatus(RECONCILED, "EBS_NY2", 200);
        cacheSize(1);
    }

    @Test
    public void testSimpleOptionalReconcile() {
        cacheSize(0);
        reconciler.onEvent(new TradeAcknowledgement("MiddleOffice_efx", 200, 2000));
        cacheSize(1);
        recordStatus(RECONCILING, "MIDDLE_OFFICE", 200);
        reconciler.onEvent(new TradeAcknowledgement("sdp", 200, 3000));
        recordStatus(RECONCILED, "MIDDLE_OFFICE", 200);
        cacheSize(1);
    }

    @Test
    public void testSummaryStatistics() {
        final TimingPulseEvent timingPulseEvent = new TimingPulseEvent(1);
        timingPulseEvent.setCurrentTimeMillis(1 * 1000);
        reconciler.onEvent(timingPulseEvent);
        //match a trade
        reconciler.onEvent(new TradeAcknowledgement("NY_2", 200, 2000));
        cacheSize(1);
        recordStatus(RECONCILING, "EBS_NY2", 200);
        reconciler.onEvent(new TradeAcknowledgement("MiddleOffice_NY2", 200, 3000));
        recordStatus(RECONCILED, "EBS_NY2", 200);
        //expire a trade
        timingPulseEvent.setCurrentTimeMillis(10 * 1000);
        reconciler.onEvent(timingPulseEvent);
        reconciler.onEvent(new TradeAcknowledgement("NY_2", 11, 10 * 1000));
        timingPulseEvent.setCurrentTimeMillis(21 * 1000);
        reconciler.onEvent(timingPulseEvent);
        cacheSize(2);
        recordStatus(EXPIRED_RECONCILE, "EBS_NY2", 11);
        //test summary
        TradeReconciler reconciler_EBS_NY2 = getReconcilerById("EBS_NY2");
        assertThat(reconciler_EBS_NY2.getReconcile_expired(), is(1));
        assertThat(reconciler_EBS_NY2.getReconciled(), is(1));
        assertThat(reconciler_EBS_NY2.getReconciling(), is(0));
    }

    @Test
    public void testSImpleTimeout() {
        final TimingPulseEvent timingPulseEvent = new TimingPulseEvent(1);
        timingPulseEvent.setCurrentTimeMillis(1 * 1000);
        reconciler.onEvent(timingPulseEvent);
        timingPulseEvent.setCurrentTimeMillis(10 * 1000);
        reconciler.onEvent(timingPulseEvent);
        reconciler.onEvent(new TradeAcknowledgement("NY_2", 200, 10 * 1000));
        timingPulseEvent.setCurrentTimeMillis(21 * 1000);
        reconciler.onEvent(timingPulseEvent);
        cacheSize(1);
        recordStatus(EXPIRED_RECONCILE, "EBS_NY2", 200);
    }

    @Test
    public void testSummaryListener() {
        SummaryListener summaryListener = new SummaryListener();
        reconciler.onEvent(new ListenerRegisration(summaryListener, ReconcileSummaryListener.class));
        final TimingPulseEvent timingPulseEvent = new TimingPulseEvent(1);
        timingPulseEvent.setCurrentTimeMillis(1 * 1000);
        reconciler.onEvent(timingPulseEvent);
        summarySize(summaryListener, 5);
        summaryListener.reconciler2Update.clear();
        //match a trade
        reconciler.onEvent(new TradeAcknowledgement("NY_2", 200, 2000));
        timingPulseEvent.setCurrentTimeMillis(2 * 1000);
        reconciler.onEvent(timingPulseEvent);
        summarySize(summaryListener, 1);

        reconciler.onEvent(new TradeAcknowledgement("MiddleOffice_NY2", 200, 3000));
        timingPulseEvent.setCurrentTimeMillis(3 * 1000);
        reconciler.onEvent(timingPulseEvent);
        summarySize(summaryListener, 2);
        checkSummary(summaryListener, new SummaryDetails("EBS_NY2", 1, 0, 0));

        //expire a trade
        timingPulseEvent.setCurrentTimeMillis(10 * 1000);
        reconciler.onEvent(timingPulseEvent);
        //no update sent as time hasnt moved
        checkSummary(summaryListener, new SummaryDetails("EBS_NY2", 1, 0, 0));
        summarySize(summaryListener, 5);

        reconciler.onEvent(new TradeAcknowledgement("NY_2", 11, 10 * 1000));
        timingPulseEvent.setCurrentTimeMillis(21 * 1000);
        reconciler.onEvent(timingPulseEvent);
        checkSummary(summaryListener, new SummaryDetails("EBS_NY2", 1, 0, 1));
        summarySize(summaryListener, 5);
    }

    @Test
    public void testClear() {
        SummaryListener summaryListener = new SummaryListener();
//        summaryListener.logToConsole = true;
        reconciler.onEvent(new ListenerRegisration(summaryListener, ReconcileSummaryListener.class));
        final TimingPulseEvent timingPulseEvent = new TimingPulseEvent(1);
        timingPulseEvent.setCurrentTimeMillis(1 * 1000);
        reconciler.onEvent(timingPulseEvent);
        summarySize(summaryListener, 5);
        summaryListener.reconciler2Update.clear();
        //match a trade
        reconciler.onEvent(new TradeAcknowledgement("NY_2", 200, 2000));
        timingPulseEvent.setCurrentTimeMillis(2 * 1000);
        reconciler.onEvent(timingPulseEvent);
        summarySize(summaryListener, 1);

        reconciler.onEvent(new TradeAcknowledgement("MiddleOffice_NY2", 200, 3000));
        timingPulseEvent.setCurrentTimeMillis(3 * 1000);
        reconciler.onEvent(timingPulseEvent);
        summarySize(summaryListener, 2);
        checkSummary(summaryListener, new SummaryDetails("EBS_NY2", 1, 0, 0));

        //expire a trade
        timingPulseEvent.setCurrentTimeMillis(10 * 1000);
        reconciler.onEvent(timingPulseEvent);
        //no update sent as time hasnt moved
        checkSummary(summaryListener, new SummaryDetails("EBS_NY2", 1, 0, 0));
        summarySize(summaryListener, 5);

        reconciler.onEvent(new TradeAcknowledgement("NY_2", 11, 10 * 1000));
        timingPulseEvent.setCurrentTimeMillis(21 * 1000);
        reconciler.onEvent(timingPulseEvent);
        checkSummary(summaryListener, new SummaryDetails("EBS_NY2", 1, 0, 1));
        summarySize(summaryListener, 5);

        //clear
        reconciler.onEvent(ControlSignals.CLEAR_CACHE_ACTION);
        reconciler.onEvent(ControlSignals.PUBLISH_SUMMARY_ACTION);
        summarySize(summaryListener, 5);
        checkSummary(summaryListener, new SummaryDetails("EBS_NY2", 0, 0, 0));
        cacheSize(0);

    }

//    @Test
//    public void dumpReconcileRecordsAsJson() {
//        TestReportPublisher publisher = new TestReportPublisher();
//        reconciler.onEvent(new ListenerRegisration(publisher, ReconcileReportPublisher.class));
//
//        final TimingPulseEvent timingPulseEvent = new TimingPulseEvent(1);
//        timingPulseEvent.setCurrentTimeMillis(1 * 1000);
//        reconciler.onEvent(timingPulseEvent);
//        //match a trade
//        reconciler.onEvent(new TradeAcknowledgement("NY_2", 200, 2000));
//        timingPulseEvent.setCurrentTimeMillis(2 * 1000);
//        reconciler.onEvent(timingPulseEvent);
//        reconciler.onEvent(new TradeAcknowledgement("MiddleOffice_NY2", 200, 3000));
//        timingPulseEvent.setCurrentTimeMillis(3 * 1000);
//        reconciler.onEvent(timingPulseEvent);
//        //expire a trade
//        timingPulseEvent.setCurrentTimeMillis(10 * 1000);
//        reconciler.onEvent(timingPulseEvent);
//        reconciler.onEvent(new TradeAcknowledgement("NY_2", 11, 10 * 1000));
//        timingPulseEvent.setCurrentTimeMillis(21 * 1000);
//        reconciler.onEvent(timingPulseEvent);
//    }

    @Test
    public void dumpReconcileRecordsAsJsonWithAsyncJsonReportPublisher() throws IOException {
        SynchronousJsonReportPublisher publisher = new SynchronousJsonReportPublisher();
        publisher.arrayFormat = false;
        reconciler.onEvent(new ListenerRegisration(publisher, ReconcileReportPublisher.class));

        ReportConfiguration config = new ReportConfiguration();
        config.reportDirectory = "target/generated-test-sources/reports/test1";
        reconciler.onEvent(ConfigEvents.reportConfig(config));

        final TimingPulseEvent timingPulseEvent = new TimingPulseEvent(1);
        timingPulseEvent.setCurrentTimeMillis(1 * 1000);
        reconciler.onEvent(timingPulseEvent);
        //match a trade
        reconciler.onEvent(new TradeAcknowledgement("NY_2", 200, 2000));
        timingPulseEvent.setCurrentTimeMillis(2 * 1000);
        reconciler.onEvent(timingPulseEvent);
        reconciler.onEvent(new TradeAcknowledgement("MiddleOffice_NY2", 200, 3000));
        timingPulseEvent.setCurrentTimeMillis(3 * 1000);
        reconciler.onEvent(timingPulseEvent);
        //expire a trade
        timingPulseEvent.setCurrentTimeMillis(10 * 1000);
        reconciler.onEvent(timingPulseEvent);
        reconciler.onEvent(new TradeAcknowledgement("NY_2", 11, 10 * 1000));
        timingPulseEvent.setCurrentTimeMillis(21 * 1000);
        reconciler.onEvent(timingPulseEvent);

        String fileContents = Files.lines(Paths.get("target/generated-test-sources/reports/test1/reconcilerReport_EBS_NY2.json")).collect(Collectors.joining("\n"));

        assertThat(fileContents, equalToIgnoringWhiteSpace("{\"reconciler\": \"EBS_NY2\", \"records\":[\n"
                + "{\"tradeId\": 11, \"status\": \"EXPIRED_RECONCILE\", \"acks\": [{\"venue\": \"NY_2\", \"ackTime\": 10000}, {\"venue\": \"MiddleOffice_NY2\", \"ackTime\": -1}]},\n"
                + "{\"tradeId\": 200, \"status\": \"RECONCILED\", \"acks\": [{\"venue\": \"NY_2\", \"ackTime\": 2000}, {\"venue\": \"MiddleOffice_NY2\", \"ackTime\": 3000}]}\n"
                + "]}"));
    }

    protected void summarySize(SummaryListener summaryListener, int expectedize) {
        HashMap<String, SummaryListener.SummaryDetails> reconciler2Update = summaryListener.reconciler2Update;
        assertThat(reconciler2Update.size(), is(expectedize));

    }

    protected void checkSummary(SummaryListener summaryListener, SummaryDetails details) {
        HashMap<String, SummaryListener.SummaryDetails> reconciler2Update = summaryListener.reconciler2Update;
        SummaryListener.SummaryDetails storedDetails = reconciler2Update.get(details.reconcilerId);
        assertThat(storedDetails, is(details));
    }

    protected void cacheSize(int expectedSize) {
        assertThat(key2Status.size(), is(expectedSize));
    }

    protected void recordStatus(ReconcileStatus.Status status, String reconcilerId, int tradeId) {
        ReconcileStatus recRecord = key2Status.get(new TestReconcileCache.ReconcileKey(reconcilerId, tradeId));
        assertNotNull(recRecord);
        assertThat(recRecord.status(), is(status));
    }

    protected TradeReconciler getReconcilerById(String id) {
        TradeReconciler matchingReconciler = null;
        for (TradeReconciler rec : cacheManager.reconcilers) {
            if (rec.id.equals(id)) {
                matchingReconciler = rec;
                break;
            }
        }
        return matchingReconciler;
    }
}
