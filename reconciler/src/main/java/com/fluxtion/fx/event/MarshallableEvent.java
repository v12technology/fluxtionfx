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
package com.fluxtion.fx.event;

import com.fluxtion.runtime.event.Event;
import net.openhft.chronicle.wire.Marshallable;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public abstract class MarshallableEvent extends Event implements Marshallable {

    public MarshallableEvent() {
    }

    public MarshallableEvent(int id) {
        super(id);
        System.out.println("MarshallableEvent::constructor");
    }

    public MarshallableEvent(int id, int filterId) {
        super(id, filterId);
    }

    public MarshallableEvent(int id, String filterString) {
        super(id, filterString);
    }

    public MarshallableEvent(int id, int filterId, String filterString) {
        super(id, filterId, filterString);
    }

    @Override
    public boolean equals(Object o) {
        return Marshallable.$equals(this, o);
    }

    @Override
    public int hashCode() {
        return Marshallable.$hashCode(this);
    }

    @Override
    public String toString() {
        return Marshallable.$toString(this);
    }
}
