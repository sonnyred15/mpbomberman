
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.net.*;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.ILog;
import org.amse.bomberman.util.impl.ConsoleLog;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.server.gameinit.GameStorage;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Server implements IServer {
    private ILog log = new ConsoleLog();//TODO make fabric for this.
    private final int            port;

    //    
    private volatile StateControl stateControl = StateControl.SHUTDOWNED;
    //
    private ServerSocket         serverSocket;
    private GameStorage          gameStorage;    
    private Thread               listeningThread;
    private final List<ISession> sessions =
                                         new CopyOnWriteArrayList<ISession>();
    private int                  sessionCounter = 0;    // need to generate name of log files.

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
    @Override
    public synchronized void start() throws IOException,
                                            IllegalStateException {
        this.stateControl.start(this);
    }

    /**
     * Closing server socket and ending listening thread and clients threads
     * by calling interrupt()
     * @throws java.io.IOException if an error occurs when closing socket.
     * @throws IllegalStateException if we are trying to shutdown not raised server.
     */
    @Override
    public synchronized void shutdown()
                                    throws IOException,
                                           IllegalStateException,
                                           SecurityException {
        this.stateControl.shutdown(this);
    }

    @Override
    public boolean isShutdowned() {
        return (this.stateControl==StateControl.SHUTDOWNED);
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public List<ISession> getSessions() {
        return this.sessions;
    }

    public GameStorage getGameStorage() {
        return gameStorage;
    }

    @Override
    public void sessionTerminated(ISession endedSession) {
        this.sessions.remove(endedSession);
        this.sessionCounter--;

        writeToLog("Server: session removed.");
    }

    @Override
    public void writeToLog(String message) {
        if ((log == null) || log.isClosed()) {
            System.out.println(message);
        } else {
            log.println(message);
        }
    }

    private class SocketListen implements Runnable {
        private Server server;

        public SocketListen(Server net) {
            this.server = net;
        }

        @Override
        public void run() {
            try {
                while (!isShutdowned()) {
                    writeToLog("Server: waiting for a new client...");
                    Socket clientSocket = serverSocket.accept();

                    writeToLog("Server: client connected. " +
                               "Starting new session thread...");
                    sessionCounter++;

                    //
                    ISession newSession = new AsynchroSession(this.server,
                                                     clientSocket,
                                                     gameStorage,
                                                     sessionCounter,
                                                     this.server.log);

                    //
                    sessions.add(newSession);
                    newSession.start();
                }
            } catch (SocketTimeoutException ex) {    // never happen in current realization
                writeToLog("Server: run warning. " + ex.getMessage());
            } catch (IOException ex) {    // if an I/O error occurs when waiting for a connection.
                if (ex.getMessage().equalsIgnoreCase("Socket closed")) {
                    writeToLog("Server: " + ex.getMessage());    // server socket closed
                } else {
                    writeToLog("Server: error. " + ex.getMessage());    // else exception
                }
            }

            //
            writeToLog("Server: listening(run) thread come to end. Freeing resources.");
            freeResources();
        }

        private void freeResources() {
            int i = 0;
            
            //Terminating all sessions
            for (ISession session : sessions) {
                writeToLog("Server: interrupting session " + i + "...");
                session.terminateSession();
                ++i;
            }

            //Clear all games.
            if (server.gameStorage != null) {
                server.gameStorage.clearGames();
                server.gameStorage = null;
            }

            //Closing log
            if (server.log != null) {
                try {
                    server.log.close();
                } catch (IOException ex) {
                    System.err.println("Server: freeing resources warning. "
                            + "Can`t save log." + ex.getMessage());
                }
            }

        }
    }

    private enum StateControl {

        STARTED() {

            @Override
            public void start(Server server) {
                System.err.println(
                        "Server: start error. Already in started state.");
                throw new IllegalStateException("Server: start error. "
                        + "Already in started state.");

            }

            @Override
            public void shutdown(Server server) throws IOException {
                try {
                    //Stop accepting clients by closing ServerSocket
                    //So, listening thread will end, but before he must clear
                    //all resources: clear games, terminate sessions and so on.
                    if (server.serverSocket != null) {
                        server.serverSocket.close();
                        server.serverSocket = null;
                    }

                    //nulling listeningThread.
                    if (server.listeningThread != null) {
                        server.listeningThread = null;
                    }                  
                } catch (IOException ex) {
                    server.writeToLog("Server: stop error. " + ex.getMessage());

                    throw ex;
                } catch (SecurityException ex) {
                    server.writeToLog("Server: stop error. " + ex.getMessage());

                    throw ex;
                }

                server.stateControl = StateControl.SHUTDOWNED;
                server.writeToLog("Server: shutdowned.");
            }
        },
        SHUTDOWNED() {

            @Override
            public void start(Server server) throws IOException {
                try {
                    server.log = new ConsoleLog();//TODO this is HARDCODE! Refactor!
                    server.serverSocket = new ServerSocket(server.port, 0);    // throws IOExeption,SecurityException
                    server.listeningThread =
                            new Thread(server.new SocketListen(server));                    
                    server.gameStorage = new GameStorage(server);
                } catch (IOException ex) {
                    server.writeToLog("Server: start error. " + ex.getMessage());

                    throw ex;
                } catch (SecurityException ex) {
                    server.writeToLog("Server: start error. " + ex.getMessage());

                    throw ex;
                }

                server.stateControl = StateControl.STARTED;
                server.listeningThread.start();
                server.writeToLog("Server: started.");
            }

            @Override
            public void shutdown(Server server) {
                System.err.println("Server: shutdown error. Already shutdowned.");
                throw new IllegalStateException("Server: shutdown error. "
                        + "Already shutdowned.");
            }
        };

        public abstract void start(Server server) throws IOException,
                                                         IllegalStateException;

        public abstract void shutdown(Server server)
                throws IOException,
                       IllegalStateException,
                       SecurityException;
    }
}
