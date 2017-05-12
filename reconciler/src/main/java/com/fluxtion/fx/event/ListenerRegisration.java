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
package com.fluxtion.fx.event;

import static com.fluxtion.fx.event.FxEventIdList.LISTENER_REGISTRATION;
import com.fluxtion.runtime.event.Event;

/**
 * A generic listener registration/de-registration event. 
 * 
 * @author Greg Higgins (greg.higgins@V12technology.com)
 * @param <T> The listener to register
 */
public class ListenerRegisration <T> extends Event{
    
    private final T listener;
    private final boolean register;

    public ListenerRegisration(T listener, Class listenerClass) {
        super(LISTENER_REGISTRATION, listenerClass.getCanonicalName());
        this.listener = listener;
        register = true;
    }

    public ListenerRegisration(T listener, Class listenerClass, boolean register) {
        super(LISTENER_REGISTRATION, listenerClass.getCanonicalName());
        this.listener = listener;
        this.register = register;
    }

    public T getListener() {
        return listener;
    }

    public boolean isRegister() {
        return register;
    }
    
}
