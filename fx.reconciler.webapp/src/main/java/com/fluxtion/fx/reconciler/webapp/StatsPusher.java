/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.fx.reconciler.webapp;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
@WebSocket
public class StatsPusher {

    private String sender, msg;
    private static final Queue<Session> SESSIONS = new ConcurrentLinkedQueue<>();

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        SESSIONS.add(user);
        System.out.println("new session connection:" + user);
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        SESSIONS.remove(user);
        System.out.println("closed session connection:" + user);
    }
    
    public static void publishStats(ReconcilerStatus status){
        SESSIONS.stream().filter(Session::isOpen).forEach((session) -> {
            try {
                JSONObject json = new JSONObject().put("id", status.id)
                        .put("expired", status.getExpired())
                        .put("reconciled", status.getReconciled())
                        .put("reconciling", status.getReconciling())
                        ;
                String s = String.valueOf(json);
//                System.out.println("sending:" + s);
                session.getRemote().sendString(s);
            } catch (IOException ex) {
                Logger.getLogger(StatsPusher.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }    

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {

    }
}
