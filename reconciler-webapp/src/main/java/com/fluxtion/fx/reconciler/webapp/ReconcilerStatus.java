/* 
 * Copyright (C) 2017 V12 Technology Limited
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
package com.fluxtion.fx.reconciler.webapp;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class ReconcilerStatus {

    String id;
    int reconciled;
    int reconciling;
    int expired;

    public ReconcilerStatus(String id, int reconciled, int reconciling, int expired) {
        this.id = id;
        this.reconciled = reconciled;
        this.reconciling = reconciling;
        this.expired = expired;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getReconciled() {
        return reconciled;
    }

    public void setReconciled(int reconciled) {
        this.reconciled = reconciled;
    }

    public int getReconciling() {
        return reconciling;
    }

    public void setReconciling(int reconciling) {
        this.reconciling = reconciling;
    }

    public int getExpired() {
        return expired;
    }

    public void setExpired(int expired) {
        this.expired = expired;
    }
    
    
}
