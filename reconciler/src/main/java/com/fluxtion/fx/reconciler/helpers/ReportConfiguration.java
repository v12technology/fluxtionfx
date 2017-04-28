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
package com.fluxtion.fx.reconciler.helpers;

import java.util.HashMap;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class ReportConfiguration {
  
    public String reportDirectory;
    public String prefix;
    public String suffix;
    public HashMap map = new HashMap();
    
    /**
     * returns supplied default dir if reportDirectory is null
     * 
     * @param defaultDir
     * @return 
     */
    public String getReportDirectory(String defaultDir){
        return reportDirectory==null?defaultDir:reportDirectory;
    }
}
