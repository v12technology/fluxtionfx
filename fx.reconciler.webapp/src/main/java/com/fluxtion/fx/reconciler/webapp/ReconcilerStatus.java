/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
