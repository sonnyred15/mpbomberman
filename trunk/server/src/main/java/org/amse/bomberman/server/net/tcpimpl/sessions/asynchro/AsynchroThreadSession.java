package org.amse.bomberman.server.net.tcpimpl.sessions.asynchro;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.amse.bomberman.server.net.tcpimpl.sessions.control.RequestCommand;
import org.amse.bomberman.server.net.tcpimpl.sessions.control.RequestExecutor;
import org.amse.bomberman.server.gameservice.GameStorage;
import org.amse.bomberman.server.net.SessionEndListener;
import org.amse.bomberman.protocol.InvalidDataException;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.server.ServiceContext;
import org.amse.bomberman.server.net.tcpimpl.sessions.AbstractSession;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.Controller;
import org.amse.bomberman.server.protocol.ResponseCreator;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.IOUtilities;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * This class represents asynchronous session between client and server
 * which process client requests.
 * 
 * @author Kirilchuk V.E.
 */
public class AsynchroThreadSession extends AbstractSession {

    private static final Logger LOG = LoggerFactory.getLogger(AsynchroThreadSession.class);
    
    private final ResponseCreator protocol = new ResponseCreator();
    /** GameStorage for this session. */
    private final ServiceContext context;
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
    public AsynchroThreadSession(Socket clientSocket, long sessionId, ServiceContext context) {
        super(clientSocket, sessionId);

        this.context = context;

        this.sessionThread = new SessionThread();
        this.sessionThread.setDaemon(true);
    }

    @Override
    public void start() {
        this.controller = new Controller(this, protocol);
        this.listeners.add(controller);
        this.context.getGameStorage().addListener(controller);

        this.sender = new AsynchroSender(clientSocket, sessionId);
        this.listeners.add(sender);

        this.sessionThread.start();
    }

    @Override
    public void send(ProtocolMessage message) {
        this.sender.addToQueue(message);
    }

    /**
     * Method that returns current session game storage.
     *
     * @see GameStorage
     * @return game storage of this session.
     */
    @Override
    public ServiceContext getServiceContext() {
        return context;
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
            try {
                listener.sessionTerminated(this);
            } catch (RuntimeException ex) {//good practice =)
                ex.printStackTrace();
            }
        }
        clearEndListeners();//doesn`t need anymore
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
                LOG.warn("Session: terminated by socket timeout.", ex);
            } catch (IOException ex) {
                LOG.warn("Session: run error. ", ex.getMessage());
            } finally {
                release();
                IOUtilities.close(in);
            }

            LOG.info("Session: input listening thread shutdowned.");
        }

        private void cyclicReadAndProcess(DataInputStream in) throws IOException {
            while (!mustEnd) {

                ProtocolMessage message = new ProtocolMessage();
                int messageId = in.readInt();
                /* client want to disconnect */
                if (messageId == ProtocolConstants.DISCONNECT_MESSAGE_ID) {
                    break;
                }

                int dataCount = in.readInt();
                if(dataCount < 0) {
                    LOG.warn("Client sended negative dataCount. Disconnect.");
                    break;
                }
                
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
        private void process(ProtocolMessage message) {
            RequestCommand cmd = null;
            try {
                int commandId = message.getMessageId();
                cmd = RequestCommand.valueOf(commandId); // throws IllegalArgumentException
                cmd.execute(controller, message.getData());
            } catch (IllegalArgumentException ex) {
                send(protocol.notOk(ProtocolConstants.INVALID_REQUEST_MESSAGE_ID,
                        "Not supported command."));
                LOG.warn("Session: answerOnCommand error. "
                        + "Non supported command int from client.", ex);
            } catch (InvalidDataException ex) {
                send(protocol.notOk(ProtocolConstants.INVALID_REQUEST_MESSAGE_ID,
                        ex.getMessage()));
            }
        }

        private void setClientSocketTimeout(int timeout) {
            try {
                clientSocket.setSoTimeout(timeout); // throws SocketException
            } catch (SocketException ex) {
                LOG.warn("Session: run error.", ex); // Error in the underlaying TCP protocol.
            }
        }

        private DataInputStream initReader() throws IOException {
            InputStream is = clientSocket.getInputStream();
            return new DataInputStream(new BufferedInputStream(is));
        }
    }
}
