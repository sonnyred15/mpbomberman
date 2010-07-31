
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.net.ISession;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Command; 
import org.amse.bomberman.util.ILog;

//~--- JDK imports ------------------------------------------------------------

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import java.util.List;
import org.amse.bomberman.server.gameinit.GameStorage;
import org.amse.bomberman.server.net.CommandExecutor;
import org.amse.bomberman.server.net.SessionEndListener;
import org.amse.bomberman.util.ProtocolConstants;

/**
 *
 * @author Kirilchuk V.E.
 */
public abstract class AbstractSession extends Thread implements ISession {
    protected final ILog               log;    // it can be null. So use System.out.println() instead of log.println()
    protected final Socket             clientSocket;
    protected final GameStorage        gameStorage;
    protected final int                sessionID;
    protected volatile boolean         mustEnd;

    public AbstractSession(Socket clientSocket,
                           GameStorage gameStorage, int sessionID,
                           ILog log) {
        assert (clientSocket != null);
        assert (gameStorage != null);

        this.setDaemon(true);
        this.clientSocket = clientSocket;
        this.gameStorage = gameStorage;
        this.sessionID = sessionID;
        this.log = log;
        this.mustEnd = false;
    }

    public boolean isMustEnd() {
        return this.mustEnd;
    }

    @Override
    public void run() {
        setClientSocketTimeout(Constants.DEFAULT_CLIENT_TIMEOUT);

        BufferedReader in = null;
        try {
            InputStream       is = this.clientSocket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");

            in = new BufferedReader(isr);
            String clientQueryLine = null;
            System.out.println("Session: waiting queries from client...");
            while (!this.mustEnd) {
                clientQueryLine = in.readLine();    // throws IOException

                if (clientQueryLine == null) {      //EOF (client is OFF.)
                    break;
                }

                answerOnCommand(clientQueryLine);
            }
        } catch (SocketTimeoutException ex) {
            System.out.println("Session: terminated by socket timeout. "
                    + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("Session: run error. " + ex.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();                         //this will close socket too...
                } else {
                    this.clientSocket.close();          //will close "is".
                }
            } catch (IOException ex){
                ex.printStackTrace();
            }

            System.out.println("Session: freeing resources.");
            freeResources();

            System.out.println("Session: closing log...and end.");
            tryCloseLog();
        }
    }

    private void tryCloseLog() {
        try {
            if (this.log != null) {
                this.log.close(); // throws IOException                
            }
        } catch (IOException ex) {
            // can`t close log stream. Log wont be saved
            System.err.println("Session: run error. Can`t close log stream. " + "Log won`t be saved. " + ex.getMessage());
        }
    }

    private void setClientSocketTimeout(int timeout) {
        try {
            this.clientSocket.setSoTimeout(timeout); // throws SocketException
        } catch (SocketException ex) {
            System.err.println("Session: run error. " + ex.getMessage()); // Error in the underlaying TCP protocol.
        }
    }

    @Override
    public void terminateSession() {
        this.mustEnd = true;

        try {
            this.clientSocket.shutdownInput();
        } catch (IOException ex) {
            System.err.println("Session: terminateSession error. " + ex.getMessage());
        }
    }

    @Override
    public void sendAnswer(String shortAnswer) {
        BufferedWriter out = null;

        try {
            OutputStream       os = this.clientSocket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

            out = new BufferedWriter(osw);
            System.out.println("Session: sending answer...");
            out.write(shortAnswer);
            out.newLine();
            out.write(""); //TODO magic code...
            out.newLine();
            out.flush();
        } catch (IOException ex) {
            System.err.println("Session: sendAnswer error. " + ex.getMessage());
        }
    }

    /**
     * Send strings from linesToSend to client.
     * @param linesToSend lines to send.
     */
    @Override
    public void sendAnswer(List<String> linesToSend)
                                    throws IllegalArgumentException {
        assert(linesToSend!=null);
        assert(linesToSend.size()>0);

        BufferedWriter out = null;

        try {
            OutputStream os = this.clientSocket.getOutputStream();    // throws IOException
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

            out = new BufferedWriter(osw);
            System.out.println("Session: sending answer...");

            for (String string : linesToSend) {
                out.write(string);
                out.newLine();
            }

            out.write(""); //TODO magic code...
            out.newLine();
            out.flush();
        } catch (IOException ex) {
            System.err.println("Session: sendAnswer error. " + ex.getMessage());
        }
    }

    protected void answerOnCommand(String query) {//TODO what if query==null is it possible?
        System.out.println("Session: query received. query=" + query);

        if (query.length() == 0) {
            sendAnswer(Protocol.emptyQueryError());
            System.out.println("Session: answerOnCommand warning. " +
                       "Empty query received. Error on client side.");

            return;
        }

        Command  cmd = null;
        String[] queryArgs = query.split(ProtocolConstants.SPLIT_SYMBOL);

        try {
            int command = Integer.parseInt(queryArgs[0]);

            cmd = Command.valueOf(command);    // throws IllegalArgumentException
        } catch (NumberFormatException ex) {
            sendAnswer(Protocol.wrongQuery());
            System.out.println("Session: answerOnCommand error. " +
                       "Wrong first part of query. " +
                       "Wrong query from client. " + ex.getMessage());

            return;
        } catch (IllegalArgumentException ex) {
            sendAnswer(Protocol.wrongQuery("Not supported command."));
            System.out.println("Session: answerOnCommand error. " +
                       "Non supported command int from client. " +
                       ex.getMessage());

            return;
        }
        
        cmd.execute(this.getCommandExecutor(), queryArgs);
    }

    public GameStorage getGameStorage() {
        return this.gameStorage;
    }

    public int getID() {
        return sessionID;
    }

    protected abstract void freeResources();

    public  abstract CommandExecutor getCommandExecutor();

}
