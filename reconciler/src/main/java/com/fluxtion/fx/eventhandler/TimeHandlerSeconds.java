/* 
 *  Copyright (C) [2016]-[2017] V12 Technology Limited
 *  
 *  This software is subject to the terms and conditions of its EULA, defined in the
 *  file "LICENCE.txt" and distributed with this software. All information contained
 *  herein is, and remains the property of V12 Technology Limited and its licensors, 
 *  if any. This source code may be protected by patents and patents pending and is 
 *  also protected by trade secret and copyright law. Dissemination or reproduction 
 *  of this material is strictly forbidden unless prior written permission is 
 *  obtained from V12 Technology Limited.  
 */
package com.fluxtion.fx.eventhandler;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.fx.event.TimingPulseEvent;

/**
 * provides second resolution timing. The time is driven by an external timing
 * pulse. The time is recorded at the beginning of the event cycle, so all nodes
 * will receive the same time value.
 *
 * @author Greg Higgins
 */
public class TimeHandlerSeconds {

    private long seconds;
    private long millis;
    private final double scale = 0.001;

    @EventHandler(filterId = 1)
    public void onTimingPulse(TimingPulseEvent pulse) {
        millis = pulse.currentTimeMillis<0?System.currentTimeMillis(): pulse.currentTimeMillis;
        seconds = (long) (millis * scale);
    }

    /**
     * Cached value of the current time in seconds, will always return the same
     * value for one event loop. THe value is renewed when a new
     * TimingPulseEvent is handled.
     *
     * @return current time in seconds
     */
    public long getSeconds() {
        return seconds;
    }
    
    public long getMillis(){
        return millis;
    }

}
