package org.amse.bomberman.server.net;

import java.io.IOException;
import java.util.Set;
import org.amse.bomberman.server.ServiceContext;
import org.amse.bomberman.server.gameservice.GameStorage;

/**
 * Interface that represents server.
 * Server is responsable for accepting clients, giving personal ISession
 * for them. Additionally server knows about games created by server`s clients.
 * @author Kirilchuk V.E.
 */
public interface Server extends SessionEndListener {

    /**
     * Raise server on it`s port.
     * @throws IOException if any IO errors occurs while raising server.
     * @throws IllegalStateException if server was already raised.
     */
    void start(int port) throws IOException, IllegalStateException;

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
    boolean isStopped();

    /**
     * Returns the port number on which this socket is listening.
     * 
     * @returns: the port number to which this socket is listening
     * or -1 if the socket is not bound yet.
     */
    int getPort();

    /**
     * Returns game storage for this server.
     * @return game storage for this server.
     */
    ServiceContext getServiceContext();

    /**
     * Returns set of sessions on this server.
     * Usually it is <b>umodifiyable set</b>.
     * @return set of sessions on this server.
     */
    Set<Session> getSessions();
}
