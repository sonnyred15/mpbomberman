
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl.sessions;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.protocol.requests.RequestExecutor;

import org.amse.bomberman.server.net.Session;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.net.Socket;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.amse.bomberman.server.net.SessionEndListener;

/**
 * Abstract class that represents basic functionality of session.
 *
 * <p>Actual realization can choose how and when to send answers on requests
 * and who would execute this requests.
 *
 * @see RequestExecutor
 * @author Kirilchuk V.E.
 */
public abstract class AbstractSession implements Session {

    /** Client socket of this session. It can`t be null. */
    protected final Socket clientSocket;

    /** Session id. In fact it can be not unique. */
    protected final long sessionId;

    protected final Set<SessionEndListener> listeners
            = new CopyOnWriteArraySet<SessionEndListener>();

    /** The mustEnd boolean. Tells if this session must terminate. */
    protected volatile boolean mustEnd;

    /**
     * Constructs AbstractSession with defined clientSocket, gameStorage,
     * sessionID and log.
     *
     * @param clientSocket client socket.
     * @param gameStorage game storage for this session.
     * @param sessionID session id. In fact it can be not unique.
     * @param log currently not used.
     */
    public AbstractSession(Socket clientSocket, long sessionID) {
        if(clientSocket == null) {
            throw new IllegalArgumentException("Client socket can`t be null.");
        }

        this.clientSocket = clientSocket;
        this.sessionId    = sessionID;
        this.mustEnd      = false;
    }

    public void addEndListener(SessionEndListener listener) {
        this.listeners.add(listener);
    }

    public void removeEndListener(SessionEndListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Checks if session must terminate.
     *
     * @return true if session must terminate, false - otherwise.
     */
    public boolean isMustEnd() {
        return this.mustEnd;
    }

    /**
     * This method must provide terminating of session.
     * Call on already terminated session can cause error or exception,
     * it depends on actual realization. After terminate session can`t be reused
     * and it`s thread must be in TERMINATED_STATE.
     * <p>By default, the second call on this method
     * will lead to RuntimeException.
     */
    public void terminateSession() {
        if (this.mustEnd) {
            throw new IllegalStateException("Already terminating.");
        }

        this.mustEnd = true;

        try {
            //force blocking io to unblock
            this.clientSocket.shutdownInput();
        } catch (IOException ex) {
            System.err.println("Session: terminateSession error. " + ex.getMessage());
        }
    }

    /**
     * Method that returns pseudo-unique id for this session.
     *
     * @return pseudo-unique id for this session.
     */
    public long getId() {
        return sessionId;
    }
}

