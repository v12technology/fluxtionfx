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

import com.fluxtion.fx.reconciler.events.TradeAcknowledgement;

/**
 * A listener interface notified upon receipt of a TradeAcknowledgement event.
 * 
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public interface TradeAcknowledgementListener {
    public static final String TA_LISTENER = "com.fluxtion.fx.reconciler.extensions.TradeAcknowledgementListener";
    
    /**
     * Notification of a new TradeAcknowledgementListener event
     * @param tradeAcknowledgement the new acknowledgement
     */
    void processAcknowledgemnt(TradeAcknowledgement tradeAcknowledgement);
}
