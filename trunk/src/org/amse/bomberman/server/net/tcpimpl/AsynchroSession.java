
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.GameStorage;
import org.amse.bomberman.protocol.RequestExecutor;
import org.amse.bomberman.server.net.SessionEndListener;
import org.amse.bomberman.util.ILog;

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
public class AsynchroSession extends AbstractSession {
    private final Controller         controller;
    private final SingleNotificator  notificator;
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
                           GameStorage gameStorage, int sessionID, ILog log) {
        super(clientSocket, gameStorage, sessionID, log);

        this.endListener = endListener;
        this.controller  = new Controller(this);
        this.mustEnd     = false;
        this.notificator = new SingleNotificator(this);

        this.notificator.start();
    }

    @Override
    protected void freeResources() {
        if (this.controller != null) {
            if (this.controller.getMyGame() != null) {    // without this check controller will print error.
                this.controller.tryLeave();
            }
        }

        if (this.endListener != null) {
            this.endListener.sessionTerminated(this);
        }
    }

    /**
     * Method
     *
     * @return
     */
    @Override
    public RequestExecutor getRequestExecutor() {
        return controller;
    }

    /**
     * Method
     *
     * @param shortAnswer
     */
    @Override
    public void sendAnswer(String shortAnswer) {    // TODO in asychro session answers must be asynchronous...
        BufferedWriter out = null;

        try {
            out = initWriter();

            System.out.println("Session: sending answer...");
            out.write(shortAnswer);
            out.newLine();
            out.write("");    // TODO magic code...
            out.newLine();
            out.flush();
        } catch (IOException ex) {
            System.err.println("Session: sendAnswer error. " + ex.getMessage());
        }
    }

    /**
     * Send strings from linesToSend to client.
     * @param linesToSend lines to send.
     *
     * @throws IllegalArgumentException
     */
    @Override
    public void sendAnswer(List<String> linesToSend) throws IllegalArgumentException {
        assert (linesToSend != null);
        assert (linesToSend.size() > 0);

        BufferedWriter out = null;

        try {
            out = initWriter();

            System.out.println("Session: sending answer...");

            for (String string : linesToSend) {
                out.write(string);
                out.newLine();
            }

            out.write("");    // TODO magic code...
            out.newLine();
            out.flush();
        } catch (IOException ex) {
            System.err.println("Session: sendAnswer error. " + ex.getMessage());
        }
    }

    private BufferedWriter initWriter() throws UnsupportedEncodingException, IOException {
        BufferedWriter     out;
        OutputStream       os  = this.clientSocket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

        out = new BufferedWriter(osw);

        return out;
    }
}
