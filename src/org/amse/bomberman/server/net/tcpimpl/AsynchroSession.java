
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.protocol.RequestExecutor;

import org.amse.bomberman.server.gameinit.GameStorage;
import org.amse.bomberman.server.net.SessionEndListener;

//~--- JDK imports ------------------------------------------------------------

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.io.UnsupportedEncodingException;

import java.net.Socket;

import java.util.List;

/**
 *
 * @author Kirilchuk V.E.
 */
public class AsynchroSession extends AbstractThreadSession {
    private final Controller         controller;
    private final AsynchroSender sender;
    private final SessionEndListener endListener;

    /**
     * Constructs
     *
     * @param endListener
     * @param clientSocket
     * @param gameStorage
     * @param sessionID
     * @param log
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
