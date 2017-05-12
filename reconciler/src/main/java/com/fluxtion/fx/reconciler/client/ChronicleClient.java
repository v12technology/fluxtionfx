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
package com.fluxtion.fx.reconciler.client;

import com.fluxtion.fx.event.TimingPulseEvent;
import com.fluxtion.fx.reconciler.ReconcileSink;
import com.fluxtion.fx.reconciler.events.TradeAcknowledgement;
import java.io.File;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;

/**
 * publishes TradeAcknowledgement event messages to a local chronicle queue.
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class ChronicleClient implements ReconcileSink {

    private final ReconcileSink chronicleSink;

    public ChronicleClient(String filePath){
        File queuePath = new File(OS.TARGET, filePath);
        SingleChronicleQueue queue = SingleChronicleQueueBuilder.binary(queuePath).build(); 
        chronicleSink = queue.acquireAppender().methodWriter(ReconcileSink.class);
    }
    
    @Override
    public void processTradeAcknowledgement(TradeAcknowledgement acknowledgedment) {
        chronicleSink.processTradeAcknowledgement(acknowledgedment);
    }

    @Override
    public void timeUpdate(TimingPulseEvent timingEvent) {
        chronicleSink.timeUpdate(timingEvent);
    }
    
}
