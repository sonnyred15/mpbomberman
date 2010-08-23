
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl.asynchro;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.protocol.RequestExecutor;

import org.amse.bomberman.server.gameinit.GameStorage;
import org.amse.bomberman.server.net.SessionEndListener;

//~--- JDK imports ------------------------------------------------------------

import java.net.Socket;

import java.util.List;
import org.amse.bomberman.server.net.tcpimpl.AbstractThreadSession;
import org.amse.bomberman.server.net.tcpimpl.Controller;

/**
 * This class represents asynchronous session between client and server
 * which process client requests.
 * 
 * @author Kirilchuk V.E.
 */
public class AsynchroSession extends AbstractThreadSession {
    private final Controller         controller;
    private final AsynchroSender     sender;
    private final SessionEndListener endListener;

    /**
     * Constructs asynchro session. This session can send messages to client
     * asynchronously and even send some messages without any requests
     * from client side. However, client must support asynchronous
     * receivers to work with this session.
     *
     * @param endListener listener of session end.
     * @param clientSocket socket of client of this session.
     * @param gameStorage game storage for this session.
     * @param sessionID unique id of this session.
     */
    public AsynchroSession(SessionEndListener endListener, Socket clientSocket,
                           GameStorage gameStorage, int sessionID) {
        super(clientSocket, gameStorage, sessionID);

        this.endListener = endListener;
        this.controller  = new Controller(this);
        this.mustEnd     = false;
        this.sender      = new AsynchroSender(this, clientSocket);

        this.sender.start();
    }

    /**
     * Freeing additional resources of this
     * session: if client was in game, then leave from game plus notifying
     * endListener about end.
     *
     * @see SessionEndListener
     */
    @Override
    protected void freeResources() {
        if (this.controller != null) { //Is al these checks are realy need?
            if (this.controller.getMyGame() != null) {    // without this check controller will print error.
                this.controller.tryLeave();
            }
        }

        if (this.endListener != null) {
            this.endListener.sessionTerminated(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RequestExecutor getRequestExecutor() {
        return controller;
    }

    /**
     * Asynchronously sending message to client.
     * 
     * @param message message to send to client.
     */
    @Override
    public void sendAnswer(String message) {
        this.sender.addToQueue(message);
    }

    /**
     * Asynchronously sending multiline message to client.
     *
     * @param message message to send to client.
     */
    @Override
    public void sendAnswer(List<String> message) {
        this.sender.addToQueue(message);
    }
}
