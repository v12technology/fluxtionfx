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

    private static final Queue<Session> SESSIONS = new ConcurrentLinkedQueue<>();

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        SESSIONS.add(user);
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        SESSIONS.remove(user);
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
                session.getRemote().sendString(s);
            } catch (IOException ex) {
                Logger.getLogger(StatsPusher.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }    

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        //for processiing control messages
    }
}
