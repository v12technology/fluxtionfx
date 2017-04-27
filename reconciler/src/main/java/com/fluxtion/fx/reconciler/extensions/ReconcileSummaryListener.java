/* 
 * Copyright (C) 2017 V12 Technology Limited
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
package com.fluxtion.fx.reconciler.extensions;

/**
 * A listener that is notified of summary state of reconciliation.
 * 
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public interface ReconcileSummaryListener {
    
    public static final String RECONCILE_LISTENER = "com.fluxtion.fx.reconciler.extensions.ReconcileSummaryListener";
    
    /**
     * Summary update notification
     * @param reconcilerId reconciler identifier
     * @param matchedTrades number of matched trades
     * @param reconcilingTrades number of trades awaiting reconciliation and not timed out
     * @param unMatchedTrades number of trades, un-reconciled within expiry time
     */
    public void reconcileSummary(String reconcilerId, int matchedTrades, int reconcilingTrades, int unMatchedTrades);   
    
}
