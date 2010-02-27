/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl;

import java.util.List;
import org.amse.bomberman.server.net.ISession;

/**
 *
 * @author Kirilchuk V.E.
 */
public class AsynchroServer extends Server {

    @Override
    public void notifyAllClients(String message) {
        for (ISession session : sessions) {
            session.sendAnswer(message);
        }
    }

    @Override
    public void notifyAllClients(List<String> messages) {
        for (ISession session : sessions) {
            session.sendAnswer(messages);
        }
    }

    @Override
    public void notifySomeClients(List<ISession> sessions, List<String> messages) {
        for (ISession session : sessions) {
            session.sendAnswer(messages);
        }
    }
}
