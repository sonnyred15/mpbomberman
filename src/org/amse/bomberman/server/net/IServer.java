
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.net;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.GameStorage;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.Set;

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

    /**
     * Returns game storage for this server.
     * @return game storage for this server.
     */
    GameStorage getGameStorage();

    /**
     * Returns set of sessions on this server.
     * Usually it is <b>umodifiyable set</b>.
     * @return set of sessions on this server.
     */
    Set<ISession> getSessions();
}
