/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl;

import org.amse.bomberman.server.net.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.server.ServerChangeListener;
import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.ILog;
import org.amse.bomberman.util.impl.ConsoleLog;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Server implements IServer {

    protected ILog log = new ConsoleLog(); // could be never initialized. Use writeToLog(...) instead of log.println(...)
    protected final int port;
    protected final List<Game> games = new CopyOnWriteArrayList<Game>();
    protected ServerSocket serverSocket;
    protected boolean shutdowned = true; //true until we start accepting clients.
    protected Thread listeningThread;
    protected final List<ISession> sessions = Collections.synchronizedList(new LinkedList<ISession>());
    protected int sessionCounter = 0; //need to generate name of log files.
    protected long startTime;
    protected ServerChangeListener changeListener;
    /**
     * Constructor with default port.
     */
    public Server() {
        this(Constants.DEFAULT_PORT);
    }

    /**
     * Main constructor that creating Server object with port param.
     * @param port Free port number. Port must be between 0 and 65535, inclusive.
     * '0' for random free port. 0 is not reccomended cause clients must know
     * actual port number to connect.
     */
    public Server(int port) {
        this.port = port;
    }

    /**
     * Starts listening thread where ServerSocket.accept() method is using.
     * 
     */
    public synchronized void start() throws IOException, IllegalStateException {
        try {

            if (shutdowned) {
                this.shutdowned = false;
                this.serverSocket = new ServerSocket(port, 0); // throws IOExeption,SecurityException
                this.listeningThread = new Thread(new SocketListen(this));
                this.listeningThread.start();                
            } else {
                throw new IllegalStateException("Server: start error. Already accepting. Can`t raise.");
            }

        } catch (IOException ex) {
            writeToLog("Server: start error. " + ex.getMessage());
            throw ex;
        } catch (SecurityException ex) {
            writeToLog("Server: start error. " + ex.getMessage());
            throw ex;
        }

        this.startTime = System.currentTimeMillis();
        if(this.changeListener != null){
            this.changeListener.switchedState(true);
        }
        writeToLog("Server: started.");
    }

    /**
     * Closing server socket and ending listening thread and clients threads
     * by calling interrupt()
     * @throws java.io.IOException if an error occurs when closing socket.
     * @throws IllegalStateException if we are trying to shutdown not raised server.
     */
    public synchronized void shutdown() throws IOException, IllegalStateException, SecurityException {
        try {

            if (!this.shutdowned) {
                
                this.shutdowned = true;

                if (this.listeningThread != null) {
                    this.listeningThread = null;
                }

                //this.sessionCounter = 0;
                //this.sessions.clear(); SESSIONS MUST AUTOCLEAR BY INTERRUPT!!!
                this.games.clear();

                if (this.serverSocket != null) {
                    this.serverSocket.close();
                    this.serverSocket = null;
                }
               
                if (this.log != null) {
                    try {
                        this.log.close();
                    } catch (IOException ex) {
                        System.out.println("Server: stop warning. Can`t close log." +
                                ex.getMessage());
                    }
                }
            } else {
                throw new IllegalStateException("Server: stop error. Is not raised. Can`t shutdown.");
            }

        } catch (IOException ex) {
            writeToLog("Server: stop error. " + ex.getMessage());
            throw ex;
        } catch (SecurityException ex) {
            writeToLog("Server: stop error. " + ex.getMessage());
            throw ex;
        }

        if(this.changeListener != null){
            this.changeListener.switchedState(false);
        }
        writeToLog("Server: shutdowned.");
    }

    public int addGame(Game game) {
        int n = -1;
        if (!this.shutdowned) { // is it redundant?
            this.games.add(game);
            n = this.games.indexOf(game);
            writeToLog("Server: game added.");
        } else {
            writeToLog("Server: addGame warning. Tryed to add game to shutdowned server.");
        }
        return n;
    }

    public void removeGame(Game gameToRemove) {
        if (!this.shutdowned) { // is it redundant?
            if (this.games.remove(gameToRemove)) {
                writeToLog("Server: game removed.");
            } else {
                writeToLog("Server: removeGame warning. No specified game found.");
            }
        } else {
            writeToLog("Server: removeGame warning. Tryed to remove game from shutdowned server.");
        }
    }

    /**
     * Return game from list at the specified index
     * @param n
     * @return
     */
    public Game getGame(int n) {
        Game game = null;
        if (!this.shutdowned) { // is it redundant?
            try {
                game = this.games.get(n);
            } catch (IndexOutOfBoundsException ex) {
                writeToLog("Server: getGame warning. Tryed to get game with illegal ID.");
            }
        } else {
            writeToLog("Server: getGame warning. Tryed to get game from shutdowned server.");
        }
        return game;
    }

    public List<Game> getGamesList() {
        if (this.shutdowned) { // is it redundant?
            writeToLog("Server: getGamesList warning. Tryed to get games list from shutdowned server.");
        }

        return this.games;
    }

    public int getSessionCount() {
        return sessionCounter;
    }

    public synchronized boolean isShutdowned() {
        return this.shutdowned;
    }

    public int getPort() {
        return this.port;
    }

    public long getWorkTime() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    public int getClientsNum() {
        return sessionCounter;
    }

    public void sessionTerminated(ISession endedSession) {
        this.sessions.remove(endedSession);
        this.sessionCounter--;
        writeToLog("Server: session removed.");
    }

    public void setChangeListener(ServerChangeListener logListener) {
        this.changeListener = logListener;
    }

    public void notifyAllClients(String message) {
        throw new UnsupportedOperationException("Not supported in this implementation.");
    }

    public void notifyAllClients(List<String> messages) {
        throw new UnsupportedOperationException("Not supported in this implementation.");
    }

    public void notifySomeClients(List<ISession> sessions, List<String> messages) {
        throw new UnsupportedOperationException("Not supported in this implementation.");
    }

    public void notifyAllClientsExceptOne(List<String> messages, ISession sessionToIgnore) {
        throw new UnsupportedOperationException("Not supported in this implementation.");
    }

    public void notifySomeClients(List<ISession> sessions, String message) {
        throw new UnsupportedOperationException("Not supported in this implementation.");
    }

    private class SocketListen implements Runnable {

        private Server net;

        public SocketListen(Server net) {
            this.net = net;
        }

        public void run() {

            writeToLog("Server: waiting for a new client...");

            try {

                while (!shutdowned) {
                    //throws IO, Security, SocketTimeout, IllegalBlockingMode
                    //exceptions
                    Socket clientSocket = serverSocket.accept();
                    writeToLog("Server: client connected. Starting new session thread...");
                    sessionCounter++;
                    //CHECK V THIS// Is it throwing any exceptions?
                    ISession newSession = new Session(this.net, clientSocket,
                            sessionCounter, this.net.log);
                    sessions.add(newSession);
                    newSession.start();
                }

            } catch (SocketTimeoutException ex) { //never happen in current realization
                writeToLog("Server: run warning. " + ex.getMessage());
            } catch (IOException ex) { //if an I/O error occurs when waiting for a connection.
                if(ex.getMessage().equalsIgnoreCase("Socket closed")){
                    writeToLog("Server: " + ex.getMessage()); //server socket closed
                }else{
                    writeToLog("Server: error. " + ex.getMessage()); //else exception
                }
            } catch (SecurityException ex) { //accept wasn`t allowed
                writeToLog("Server: run error. " + ex.getMessage());
            } catch (IllegalBlockingModeException ex) { //CHECK < THIS// what comments should i write?
                writeToLog("Server: run error. " + ex.getMessage());
            }

            /*must free resources and stop our thread.*/
            int i = 1;
            synchronized (sessions) {
                for (ISession session : sessions) {
                    writeToLog("Server: interrupting session " + i + "...");
                    session.interruptSession();
                    ++i;
                }
            }

            writeToLog("Server: listening(run) thread come to end.");
        }
    }

    public List<String> getLog() {
        return log.getLog();
    }

    public void writeToLog(String message) {
        if (log == null || log.isClosed()) {
            System.out.println(message);
        } else {
            log.println(message);
        }

        if (changeListener!=null){
            changeListener.addedToLog(message);
        }
    }
}
