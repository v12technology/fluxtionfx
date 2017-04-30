/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.fx.reconciler.webapp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import static spark.Spark.*;
import spark.template.velocity.VelocityTemplateEngine;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class Main {

    private static ConcurrentHashMap<String, ReconcilerStatus> reconcilerMap = new ConcurrentHashMap<>();
    private static volatile int count;
    private static String[] reconcilerStrArr = new String[]{"REUTERS_DC1", "Middle office", "ebs_ld4", "efx-sdp-ny"};

    public static void main(String[] args) {
        webSocket("/live-stats", StatsPusher.class);
        get("/reconcile-report/:id", Main::reconcileFReport);
        get("/reconcile-status", Main::reconcileStatus);
        get("/add", (Request rqst, Response rspns) -> {
            ReconcilerStatus status = new ReconcilerStatus("eurusd", count++, 1, 122);
            reconcilerMap.put(status.getId(), status);
            return reconcileStatus(rqst, rspns);
        });
        generateReconcileRecords();
    }

    private static Object reconcileStatus(Request req, Response res) {
        Map<String, Object> model = new HashMap<>();
        model.put("reconcilerstatus", reconcilerMap.values());
        return render(model, "reconcilerSummary.vsl");
        //reconcilerMap
    }

// declare this in a util-class
    public static String render(Map<String, Object> model, String templatePath) {
        return new VelocityTemplateEngine().render(new ModelAndView(model, templatePath));
    }

    public static Object reconcileFReport(Request req, Response res) {
        return "detailed report for: " + req.params(":id");
    }

    public static void generateReconcileRecords() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            for (String string : reconcilerStrArr) {
                ReconcilerStatus status = new ReconcilerStatus(string, ++count, count, count);
                reconcilerMap.put(status.getId(), status);
                StatsPusher.publishStats(status);
            }
        }, 2, 2, TimeUnit.SECONDS);
    }

}
