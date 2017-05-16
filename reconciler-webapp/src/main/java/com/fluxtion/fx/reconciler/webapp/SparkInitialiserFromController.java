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
package com.fluxtion.fx.reconciler.webapp;

import com.fluxtion.fx.reconciler.ReconcileController;
import com.fluxtion.fx.reconciler.extensions.ReconcileSummaryListener;
import com.fluxtion.fx.reconciler.helpers.SynchronousJsonReportPublisher;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import static spark.Spark.webSocket;
import static spark.Spark.*;
import spark.template.velocity.VelocityTemplateEngine;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class SparkInitialiserFromController implements ReconcileSummaryListener {

    private final ReconcileController reconciler;
    private final ConcurrentHashMap<String, ReconcilerStatus> reconcilerMap;
    private final Map<String, Object> velocityMap;
    private static final Logger LOGGER = LogManager.getFormatterLogger(SparkInitialiserFromController.class);

    public SparkInitialiserFromController(ReconcileController reconciler) {
        this.reconciler = reconciler;
        reconcilerMap = new ConcurrentHashMap<>();
        velocityMap = new HashMap<>();
        velocityMap.put("reconcilerstatus", reconcilerMap.values());
    }

    public void init() {
        LOGGER.info("initialising webapp");
        staticFiles.externalLocation("public");
        webSocket("/live-stats", StatsPusher.class);
        get("/reconcile-status", (req, resp) -> render(velocityMap, "reconcilerSummary.vsl"));
        reconciler.registerReconcileSummaryListener(this);
        reconciler.registerReconcileReportPublisher(new SynchronousJsonReportPublisher());
        long now = System.currentTimeMillis();
        reconciler.publishSummaryUpdate();
        reconciler.publishReports();
        LOGGER.info("completed publishing reports %d millis", (System.currentTimeMillis() - now));
    }

    public String render(Map<String, Object> model, String templatePath) {
        return new VelocityTemplateEngine().render(new ModelAndView(model, templatePath));
    }

    @Override
    public void reconcileSummary(String reconcilerId, int matchedTrades, int reconcilingTrades, int unMatchedTrades) {
        reconcilerMap.compute(reconcilerId, (id, status) -> {
            if (status == null) {
                status = new ReconcilerStatus(id, matchedTrades, reconcilingTrades, unMatchedTrades);
            } else {
                status.expired = unMatchedTrades;
                status.reconciled = matchedTrades;
                status.reconciling = reconcilingTrades;
            }
            StatsPusher.publishStats(status);
            return status;
        });
        //set routes
    }

    private Object reconcileStatus(Request req, Response res) {
        return render(velocityMap, "reconcilerSummary.vsl");
    }
}
