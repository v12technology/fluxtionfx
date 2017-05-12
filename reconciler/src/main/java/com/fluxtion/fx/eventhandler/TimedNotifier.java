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

import com.fluxtion.api.annotations.AfterEvent;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.fx.eventhandler.TimeHandlerSeconds;

/**
 * Fires an update when a schedule period has expired.
 * 
 * @author Greg Higgins
 */
public class TimedNotifier {

    //config variables
    public int periodInSeconds;
    public TimeHandlerSeconds timeHandler;
    //private state
    private boolean fireUpdate = false;
    private int period;
    private TimeHandlerSeconds time;
    private long previous;
    private long timeInSeconds;

    public TimedNotifier(int periodInSeconds, TimeHandlerSeconds timeHandler) {
        this.periodInSeconds = periodInSeconds;
        this.timeHandler = timeHandler;
    }

    public TimedNotifier() {
    }

    @OnEvent
    public boolean processTimePulse() {
        timeInSeconds = time.getSeconds();
        fireUpdate = (timeInSeconds - previous) >= period;
        if (fireUpdate) {
            previous = previous + period * ((int)(timeInSeconds - previous)/period);
            while((timeInSeconds - previous) >= period){
                previous = previous + period;
            }
        }
        return fireUpdate;
    }

    public int getPeriod() {
        return period;
    }

    public long timeInSeconds() {
        return timeInSeconds;
    }

    /**
     * returns true when the period has expired, resets on the next run
     * TImingPulse update
     *
     * @return firing status
     */
    public boolean isFired() {
        return fireUpdate;
    }

    @AfterEvent
    public void resetFiredFlag(){
        fireUpdate = false;
    }
    /**
     * Copy config to private variables
     */
    @Initialise
    public void init() {
        period = periodInSeconds;
        time = timeHandler;
        timeHandler = null;
        previous = 0;
        timeInSeconds = 0;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.periodInSeconds;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TimedNotifier other = (TimedNotifier) obj;
        if (this.periodInSeconds != other.periodInSeconds) {
            return false;
        }
        return true;
    }
    
    
}
