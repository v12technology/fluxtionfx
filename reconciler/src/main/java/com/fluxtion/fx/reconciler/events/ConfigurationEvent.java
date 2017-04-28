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
package com.fluxtion.fx.reconciler.events;

import static com.fluxtion.fx.event.FxEventIdList.CONFIGURAION_EVENT;
import com.fluxtion.runtime.event.Event;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 * @param <T>
 */
public class ConfigurationEvent<T> extends Event {

    public static final int ID = CONFIGURAION_EVENT;
    private T configuration;

    public ConfigurationEvent(T configuration, String confgiKey) {
        super(CONFIGURAION_EVENT, confgiKey);
        this.configuration = configuration;
    }

    public T getConfiguration() {
        return configuration;
    }

}
