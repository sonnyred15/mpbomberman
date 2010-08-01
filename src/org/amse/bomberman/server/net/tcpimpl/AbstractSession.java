
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.GameStorage;
import org.amse.bomberman.server.net.ISession;
import org.amse.bomberman.protocol.ResponseCreator;
import org.amse.bomberman.protocol.RequestCommand;
import org.amse.bomberman.protocol.RequestExecutor;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.ILog;
import org.amse.bomberman.protocol.ProtocolConstants;

//~--- JDK imports ------------------------------------------------------------

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Abstract class that represents basic functionality of session.
 *
 * <p>Actual realization can choose how and when to send answers on requests
 * and who would execute this requests.
 *
 * @see RequestExecutor
 * @author Kirilchuk V.E.
 */
public abstract class AbstractSession extends Thread implements ISession {
    private final ResponseCreator protocol = new ResponseCreator();

    /** Client socket of this session. It can`t be null. */
    protected final Socket clientSocket;

    /** GameStorage for this session. */
    protected final GameStorage gameStorage;

    /** Session id. In fact it can be not unique. */
    protected final int sessionID;

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
    public AbstractSession(Socket clientSocket, GameStorage gameStorage, int sessionID, ILog log) {
        assert (clientSocket != null);
        assert (gameStorage != null);

        this.setDaemon(true);

        this.clientSocket = clientSocket;
        this.gameStorage  = gameStorage;
        this.sessionID    = sessionID;
        this.mustEnd      = false;
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
     * Method in which session gets requests from client and process them.
     */
    @Override
    public void run() {
        setClientSocketTimeout(Constants.DEFAULT_CLIENT_TIMEOUT);

        BufferedReader in = null;

        try {
            in = this.initReader();

            String clientQueryLine = null;

            System.out.println("Session: waiting queries from client...");
            this.readAndProcess(in, clientQueryLine);
        } catch (SocketTimeoutException ex) {
            System.out.println("Session: terminated by socket timeout. " + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("Session: run error. " + ex.getMessage());
        } finally {
            this.finallyBlock(in);
        }
    }

    /* Main part. Read and process request */
    private void readAndProcess(BufferedReader in, String clientQueryLine) throws IOException {
        while ( !this.mustEnd) {
            clientQueryLine = in.readLine();    // throws IOException

            if (clientQueryLine == null) {      // EOF (client is OFF.)
                break;
            }

            process(clientQueryLine);           // here query can`t be null!
        }
    }

    private BufferedReader initReader() throws IOException, UnsupportedEncodingException {
        BufferedReader    in;
        InputStream       is  = this.clientSocket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");

        in = new BufferedReader(isr);

        return in;
    }

    private void finallyBlock(BufferedReader in) {
        System.out.println("Session: freeing resources.");

        try {
            if (in != null) {
                in.close();    // this will close socket too...

                assert this.clientSocket.isClosed();
            } else {
                this.clientSocket.close();
            }
        } catch (IOException ex) {
            System.err.println("Session: terminating error. IOException "
                               + "while closing resourses. " + ex.getMessage());
        }

        freeResources();
        System.out.println("Session: terminated.");
    }

    private void setClientSocketTimeout(int timeout) {
        try {
            this.clientSocket.setSoTimeout(timeout);    // throws SocketException
        } catch (SocketException ex) {
            System.err.println("Session: run error. " + ex.getMessage());    // Error in the underlaying TCP protocol.
        }
    }

    /**
     * Method description
     */
    @Override
    public void terminateSession() {
        this.mustEnd = true;

        try {
            this.clientSocket.shutdownInput();
        } catch (IOException ex) {
            System.err.println("Session: terminateSession error. " + ex.getMessage());
        }
    }

    /**
     * Processing query. Maybe sending response - <b>it depends</b> on realization
     * of RequestExecutor and actual realization of <i>sendAnswer(...)</i> method.
     *
     * @see RequestExecutor
     * @param query request to process.
     */
    protected void process(String query) {
        System.out.println("Session: query received. query=" + query);

        if (query.length() == 0) {
            sendAnswer(protocol.emptyQueryError());
            System.out.println("Session: answerOnCommand warning. "
                               + "Empty query received. Error on client side.");

            return;
        }

        RequestCommand cmd       = null;
        String[]       queryArgs = query.split(ProtocolConstants.SPLIT_SYMBOL);

        try {
            int command = Integer.parseInt(queryArgs[0]);

            cmd = RequestCommand.valueOf(command);    // throws IllegalArgumentException
        } catch (NumberFormatException ex) {
            sendAnswer(protocol.wrongQuery());
            System.out.println("Session: answerOnCommand error. " + "Wrong first part of query. "
                               + "Wrong query from client. " + ex.getMessage());

            return;
        } catch (IllegalArgumentException ex) {
            sendAnswer(protocol.wrongQuery("Not supported command."));
            System.out.println("Session: answerOnCommand error. "
                               + "Non supported command int from client. " + ex.getMessage());

            return;
        }

        cmd.execute(this.getRequestExecutor(), queryArgs);
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
     * Method that returns pseudo-unique id for this session.
     *
     * @return pseudo-unique id for this session.
     */
    public int getID() {
        return sessionID;
    }

    protected abstract void freeResources();

    protected abstract RequestExecutor getRequestExecutor();
}
