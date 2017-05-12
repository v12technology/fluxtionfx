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
package com.fluxtion.fx.reconciler;

import com.fluxtion.fx.reconciler.extensions.ReconcileReportPublisher;
import com.fluxtion.fx.reconciler.extensions.ReconcileSummaryListener;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public interface ReconcileController {

    /**
     * clear all state from the reconciler
     */
    void clear();

    /**
     * replay all existing messages into the reconciler. Do not send out reports, notifications or messages during the 
     * replay process. After the end of the replay process the reconciler will not be started, so new messages will not
     * be consumed.
     */
    void replay();

    /**
     * Start the reconciler, notifying it to consume messages from whatever endpoints are configured, if there are 
     * any unread messages in the queues these will be read in and the reconcile state rebuilt. Any reader threads 
     * the reconciler uses will be started at this point.
     * 
     */
    void run();

    /**
     * Stops the reconciler from consuming and processing any new message events. Any consumer threads the 
     * reconciler are paused/stopped at this point.
     * 
     * @throws InterruptedException 
     */
    void stop() throws InterruptedException;
    
    void registerReconcileSummaryListener(ReconcileSummaryListener listener);
    
    void registerReconcileReportPublisher(ReconcileReportPublisher listener);
    
    /**
     * Notify the reconciler to publish all reports.
     */
    void publishReports();
    
    /**
     * Enable or disable the 
     * @param enable 
     */
    void enableOutput(boolean enable);
    
}
