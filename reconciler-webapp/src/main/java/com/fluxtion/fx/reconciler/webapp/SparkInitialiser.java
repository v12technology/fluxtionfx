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

import com.fluxtion.fx.event.ListenerRegisration;
import com.fluxtion.fx.reconciler.extensions.ReconcileReportPublisher;
import com.fluxtion.fx.reconciler.extensions.ReconcileSummaryListener;
import com.fluxtion.fx.reconciler.helpers.SynchronousJsonReportPublisher;
import com.fluxtion.runtime.lifecycle.EventHandler;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
public class SparkInitialiser implements ReconcileSummaryListener {

    private final EventHandler reconciler;
    private final ConcurrentHashMap<String, ReconcilerStatus> reconcilerMap;
    private final Map<String, Object> velocityMap;

    public SparkInitialiser(EventHandler reconciler) {
        this.reconciler = reconciler;
        reconcilerMap = new ConcurrentHashMap<>();
        velocityMap = new HashMap<>();
        velocityMap.put("reconcilerstatus", reconcilerMap.values());
    }

    public void init() {
        if (reconciler instanceof Lifecycle) {
            ((Lifecycle) reconciler).init();
        }
        staticFiles.externalLocation("public");
        webSocket("/live-stats", StatsPusher.class);
        get("/reconcile-status", (req, resp) -> render(velocityMap, "reconcilerSummary.vsl"));
        reconciler.onEvent(new ListenerRegisration<>(this, ReconcileSummaryListener.class));
        SynchronousJsonReportPublisher publisher = new SynchronousJsonReportPublisher();
        reconciler.onEvent(new ListenerRegisration(publisher, ReconcileReportPublisher.class));
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
