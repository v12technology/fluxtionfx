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
 * Event class that feeds into a Simple Event Processor(SEP). Users should
 * extend this class to define their own events.
 *
 * To dispatch the events Fluxtion uses either a statically defined ID, where
 * the value of ID must be unique for the events in this SEP.
 * <pre>
 *     public static final int ID = 1;
 * </pre>
 *
 * If no ID is defined then the SEP uses the class name to perform a dispatch,
 * generally this will be less efficient at runtime but is easier for the
 * developer at compile time. When class name is used, uniqueness is guaranteed
 * by the fully qualified class name in this case.
 *
 * The efficiency of dispatch depends upon the target platform, so for some
 * targets class name dispatch may be more efficient.
 *
 * An event can provide a filter field as either an int or a String, this allow
 * eventhandlers to filter the type of events they receive. The eventhandler
 * decides at compile time whether it will filter using Strings or integers.
 *
 * @author Greg Higgins
 */
public abstract class Event {

    /**
     * default ID for an event when the user does not explicitly set an ID. Any
     * Event using this value for an ID will dispatch based on class name and
     * not on ID. User defined events should not use the value Integer.MAX_VALUE
     */
    public static final int NO_ID = Integer.MAX_VALUE;
    private final int id;
    protected int filterId;
    protected String filterString;

    public Event() {
        this(NO_ID);
    }

    public Event(int id) {
        this(id, NO_ID);
    }

    public Event(int id, int filterId) {
        this(id, filterId, "");
    }

    public Event(int id, String filterString) {
        this(id, NO_ID, filterString);
    }

    public Event(int id, int filterId, String filterString) {
        this.id = id;
        this.filterId = filterId;
        this.filterString = filterString;
    }

    /**
     * The unique int identifier for this event.
     *
     * @return id for this event as an integer
     */
    public final int eventId() {
        return id;
    }

    /**
     * The integer id of a filter for this event, can be used interchangeably
     * with filterString. The event handler decides whether it will filter using
     * Strings or integer's, calling this method if filtering is integer based.
     * Integer filtering will generally be more efficient than string filtering,
     * but this depends upon the underlying target platform processing
     * characteristics.
     *
     * @return optional event filter id as integer
     */
    public final int filterId() {
        return filterId;
    }

    /**
     * The String id of a filter for this event, can be used interchangeably
     * with filterId. The event handler decides whether it will filter using
     * Strings or integer's, calling this method if String filtering is string
     * based. Integer filtering will generally be more efficient than string
     * filtering, but this depends upon the underlying target platform
     * processing characteristics.
     *
     * @return optional event filter id as String
     */
    public final String filterString() {
        return filterString;
    }

}
