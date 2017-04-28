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

import com.fluxtion.fx.reconciler.helpers.ReportConfiguration;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public interface ConfigEvents {

    public static final String REPORT_CONFIG = "com.fluxtion.fx.reconciler.publishResults.config";
    
    public static ConfigurationEvent<ReportConfiguration> reportConfig(ReportConfiguration config){
        return new ConfigurationEvent<>(config, REPORT_CONFIG);
    }
    
}
