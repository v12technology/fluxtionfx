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

/**
 * Event ID's
 * 
 * @author greg
 */
public interface FxEventIdList {

   public static final int OFFSET = 200;  
    public static final int CUSTOMER_PRICE = 1;
    public static final int CUSTOMER_ORDER = 2;
    public static final int CUSTOMER_ORDER_REJECT = 3;
    public static final int CUSTOMER_ORDER_ACCEPT = 4;
    public static final int TIMING_PULSE = 5;
    public static final int BREACH_NOTIFICATION = 6;
    public static final int LISTENER_REGISTRATION = 7;
    public static final int CONFIGURAION_EVENT = OFFSET + 8;
    
}
