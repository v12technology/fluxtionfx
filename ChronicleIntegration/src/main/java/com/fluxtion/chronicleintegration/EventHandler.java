/* 
 * Copyright (C) 2016 Greg Higgins (greg.higgins@v12technology.com)
 * 
 * This software is subject to the terms and conditions of its EULA, defined in the
 * file "LICENCE.txt" and distributed with this software. All information contained
 * herein is, and remains the property of V12 Technology Limited and its licensors, 
 * if any. This source code may be protected by patents and patents pending and is 
 * also protected by trade secret and copyright law. Dissemination or reproduction 
 * of this material is strictly forbidden unless prior written permission is 
 * obtained from V12 Technology Limited.  
 * 
 */
package com.fluxtion.chronicleintegration;


/**
 * processes all events of type T. An EventHandler is a node in a Simple Event
 * Processor (SEP), that is the root for processing events in a SEP. Events can
 * only be processed by a SEP if there is an EventHandler registered for that
 * specific type of event.
 * @see com.fluxtion.runtime.event.Event
 * 
 * @author Greg Higgins
 * 
 * @param <T> The type of {@link com.fluxtion.runtime.event.Event Event} processed by this handler
 */
public interface EventHandler<T extends Event> {

    public static final EventHandler NULL_EVENTHANDLER = new EventHandler() {
        @Override
        public void onEvent(Event e) {
        }

        @Override
        public void afterEvent() {
        }
    };

    /**
     * Called when a new event e is ready to be processed.
     *
     * @param e the {@link com.fluxtion.runtime.event.Event Event} to process.
     */
    void onEvent(T e);

    /**
     * called when all nodes that depend upon this EventHadler have successfully
     * completed their processing.
     * 
     */
    default void afterEvent() {
    }
    
    /**
     * The class of the Event processed by this handler
     * 
     * @return Class of {@link com.fluxtion.runtime.event.Event Event} to process 
     */
    default Class<? extends Event> eventClass(){
        return null;
    }

}
