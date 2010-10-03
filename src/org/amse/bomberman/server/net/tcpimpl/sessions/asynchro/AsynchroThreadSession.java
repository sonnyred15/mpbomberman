
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl.sessions.asynchro;

//~--- non-JDK imports --------------------------------------------------------
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import org.amse.bomberman.protocol.requests.RequestCommand;
import org.amse.bomberman.protocol.requests.RequestExecutor;

import org.amse.bomberman.server.gameservice.GameStorage;
import org.amse.bomberman.server.net.SessionEndListener;

//~--- JDK imports ------------------------------------------------------------

import java.net.Socket;
import java.util.ArrayList;

import java.util.List;
import org.amse.bomberman.protocol.requests.InvalidDataException;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.protocol.responses.ResponseCreator;
import org.amse.bomberman.server.net.tcpimpl.sessions.AbstractSession;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.Controller;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.IOUtilities;

/**
 * This class represents asynchronous session between client and server
 * which process client requests.
 * 
 * @author Kirilchuk V.E.
 */
public class AsynchroThreadSession extends AbstractSession {

    private final ResponseCreator protocol = new ResponseCreator();
    /** GameStorage for this session. */
    private final GameStorage gameStorage;
    private final Thread sessionThread;
    //
    private Controller controller;
    private AsynchroSender sender;

    /**
     * Constructs asynchro session. This session can send messages to client
     * asynchronously and even send some messages without any requests
     * from client side. However, client must support asynchronous
     * receivers to work with this session.
     *
     * @param creator listener of session end.
     * @param clientSocket socket of client of this session.
     * @param gameStorage game storage for this session.
     * @param sessionId unique id of this session.
     */
    public AsynchroThreadSession(SessionEndListener creator,
            Socket clientSocket,
            GameStorage gameStorage, long sessionId) {
        super(clientSocket, sessionId);

        this.gameStorage = gameStorage;
        this.listeners.add(creator);

        this.sessionThread = new SessionThread();
        this.sessionThread.setDaemon(true);
    }

    public void start() {
        this.controller = new Controller(this);
        this.listeners.add(controller);

        this.sender = new AsynchroSender(clientSocket, sessionId);
        this.listeners.add(sender);

        this.sessionThread.start();
    }

    public void send(ProtocolMessage<Integer, String> message) {
        this.sender.addToQueue(message);
    }

    /**
     * Method that returns current session game storage.
     *
     * @see GameStorage
     * @return game storage of this session.
     */
    public GameStorage getGameStorage() {
        return this.gameStorage;
    }

    /**
     * Freeing additional resources of this
     * session: if client was in game, then leave from game plus notifying
     * endListener about end.
     *
     * @see SessionEndListener
     */
    private void release() {
        for (SessionEndListener listener : listeners) {
            listener.sessionTerminated(this);
        }
    }

    private class SessionThread extends Thread {
        //

        private DataInputStream in = null;

        /**
         * Method in which session gets requests from client and process them.
         */
        @Override
        public void run() {
            setClientSocketTimeout(Constants.DEFAULT_CLIENT_TIMEOUT);
            try {
                in = initReader();
                sender.start();
                cyclicReadAndProcess(in);//main part
            } catch (SocketTimeoutException ex) {
                System.out.println("Session: terminated by socket timeout. "
                        + ex.getMessage());
            } catch (IOException ex) {
                System.err.println("Session: run error. " + ex.getMessage());
            } finally {
                release();
                closeConnection(in);
            }
        }

        private void cyclicReadAndProcess(DataInputStream in) throws IOException {
            while (!mustEnd) {

                ProtocolMessage<Integer, String> message = new ProtocolMessage<Integer, String>();
                int messageId = in.readInt();
                /* client want to disconnect */
                if (messageId == ProtocolConstants.DISCONNECT_MESSAGE_ID) {
                    break;
                }

                int dataCount = in.readInt();
                //throws IllegalArgumentExeption if dataCount < 0
                List<String> data = new ArrayList<String>(dataCount);
                for (int i = 0; i < dataCount; ++i) {
                    data.add(in.readUTF());
                }
                message.setMessageId(messageId);
                message.setData(data);

                process(message);
            }
        }

        /**
         * Processing query. Maybe sending response - <b>it depends</b> on realization
         * of RequestExecutor and actual realization of <i>sendAnswer(...)</i> method.
         *
         * @see RequestExecutor
         * @param query request to process.
         */
        private void process(ProtocolMessage<Integer, String> message) {
            RequestCommand cmd = null;
            try {
                int commandId = message.getMessageId();
                cmd = RequestCommand.valueOf(commandId); // throws IllegalArgumentException
                cmd.execute(controller, message.getData());
            } catch (IllegalArgumentException ex) {
                send(protocol.notOk(ProtocolConstants.INVALID_REQUEST_MESSAGE_ID,
                        "Not supported command."));
                System.out.println("Session: answerOnCommand error. "
                        + "Non supported command int from client. "
                        + ex.getMessage());
            } catch (InvalidDataException ex) {
                send(protocol.notOk(ProtocolConstants.INVALID_REQUEST_MESSAGE_ID,
                        ex.getMessage()));
            }
        }

        private void closeConnection(Closeable in) {
            try {
                IOUtilities.close(in);
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException ex) {
                System.err.println("Session: terminating error. IOException "
                        + "while closing resourses. " + ex.getMessage());
            }
            System.out.println("Session: terminated.");
        }

        private void setClientSocketTimeout(int timeout) {
            try {
                clientSocket.setSoTimeout(timeout); // throws SocketException
            } catch (SocketException ex) {
                System.err.println("Session: run error. " + ex.getMessage()); // Error in the underlaying TCP protocol.
            }
        }

        private DataInputStream initReader() throws IOException {
            InputStream is = clientSocket.getInputStream();
            return new DataInputStream(new BufferedInputStream(is));
        }
    }
}
