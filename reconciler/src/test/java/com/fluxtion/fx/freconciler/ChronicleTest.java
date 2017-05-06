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

import com.fluxtion.fx.reconciler.events.TradeAcknowledgement;
import com.fluxtion.runtime.lifecycle.EventHandler;
import java.io.File;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.io.IOTools;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.MethodReader;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class ChronicleTest {

    @Test
    @Ignore
    public void testTradeAck() {
        System.out.println("creating events");
        TradeAcknowledgement ack1 = new TradeAcknowledgement("EBS_NY", 2233, System.currentTimeMillis());
        File queuePath = new File(OS.TARGET, "testName-" + System.nanoTime());
        try {
            try (SingleChronicleQueue queue = SingleChronicleQueueBuilder.binary(queuePath).build()) {
                // use the queue
                EventHandler handler = queue.acquireAppender().methodWriter(EventHandler.class);
                System.out.println("writing event:");
                System.out.println(ack1);
                handler.onEvent(ack1);
                handler.onEvent(ack1);
                //read from queue
                com.fluxtion.fx.reconciler.test.generated.ReconcilerTest sep = new com.fluxtion.fx.reconciler.test.generated.ReconcilerTest();
                sep.init();
                MethodReader methodReader = queue.createTailer().methodReader(sep);
                System.out.println("reading events");
                methodReader.readOne();
                methodReader.readOne();
            }
        } finally {
            IOTools.shallowDeleteDirWithFiles(queuePath);
        }

    }
}
