
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.net;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.List;
import org.amse.bomberman.server.gameinit.GameStorage;

/**
 * Interface that represents server.
 * Server is responsable for accepting clients, giving personal ISession
 * for them. Additionally server knows about games created by server`s clients.
 * @author Kirilchuk V.E.
 */
public interface IServer extends SessionEndListener {

    /**
     * Raise server on it`s port.
     * @throws IOException if any IO errors occurs while raising server.
     * @throws IllegalStateException if server was already raised.
     */
    void start() throws IOException, IllegalStateException;

    /**
     * Shutdown`s server, so it stop`s to listen on it`s port and unavailiable
     * for new clients.
     * @throws IOException if any IO errors occurs while shutdowning server.
     * @throws IllegalStateException if server was already shutdowned.
     */
    void shutdown() throws IOException, IllegalStateException;

    /**
     * Checks if server is shutdowned.
     * @return true if server is shutdowned, false otherwise.
     */
    boolean isShutdowned();

    /**
     * Returns port of this server.
     * @return port of this server.
     */
    int getPort();

    GameStorage getGameStorage();

    /**
     * Returns list of sessions on this server.
     * @return list of sessions on this server.
     */
    List<ISession> getSessions();

    /**
     * Write some message to server log.
     * @param message message to add to server log.
     */
    void writeToLog(String message);
}
