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
package com.fluxtion.chronicleintegration;

import java.util.Arrays;

/**
 * A TradeAcknowledgement event indicates a venue has acknowledged a Trade. The
 * TradeAcknowledgement is a filtered event where the filterString is the
 * venueId.
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class TradeAcknowledgement extends MarshallableEvent {

    public static int DEFAULT_CONSTRUCTOR_COUNT;
    public static boolean PRINT_CONSTRUCTOR_STACKTRACE = false;

    public int tradeId;
    public String tradeStringId;
    public long time;

    public TradeAcknowledgement() {
        super();
        DEFAULT_CONSTRUCTOR_COUNT++;
        System.out.println("TradeAcknowledgement::constructor");
        if (PRINT_CONSTRUCTOR_STACKTRACE) {
            System.out.println(Arrays.toString(Thread.currentThread().getStackTrace()).replace(',', '\n'));
        }
    }

    public TradeAcknowledgement(String venueId, int tradeId) {
        super();
        filterString = venueId;
        this.tradeId = tradeId;
    }

    public TradeAcknowledgement(String venueId, int tradeId, long time) {
        super();
        this.tradeId = tradeId;
        this.filterString = venueId;
        this.time = time;
    }

    public void setVenueId(String venueId) {
        filterString = venueId;
    }

    public String venueId() {
        return filterString;
    }

}
