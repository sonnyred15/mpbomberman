
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.net.*;
import org.amse.bomberman.util.Constants;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Comparator;


import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import org.amse.bomberman.server.gameinit.GameStorage;
import org.amse.bomberman.server.net.tcpimpl.asynchro.AsynchroSession;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Server implements IServer {
    private final int            port;

    //    
    private volatile StateControl stateControl = StateControl.SHUTDOWNED;

    //
    private ServerSocket         serverSocket;
    private GameStorage          gameStorage;    
    private Thread               listeningThread;

    //
    private final Comparator<ISession> comparator = new Comparator<ISession>() {

        public int compare(ISession ses1, ISession ses2) {
            int id1 = ses1.getID();
            int id2 = ses2.getID();

            return id1 - id2;
        }

    };
    private final Set<ISession> sessions =
            new ConcurrentSkipListSet<ISession>(comparator);

    //
    private int sessionCounter = 0;    // need to generate unique ID`s for sessions.

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
     * {@inheritDoc}
     */
    @Override
    public synchronized void start() throws IOException,
                                            IllegalStateException {
        this.stateControl.start(this);
    }

    /**
     * Closing server socket and ending listening thread and clients threads.
     * Freeing resources: clearing games on game storage,
     * reseting counter and so on.
     * @throws java.io.IOException if an error occurs when closing socket.
     * @throws IllegalStateException if we are trying to shutdown not raised server.
     */
    @Override
    public synchronized void shutdown() throws IOException,
                                               IllegalStateException,
                                               SecurityException {
        this.stateControl.shutdown(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isShutdowned() {
        return (this.stateControl == StateControl.SHUTDOWNED);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPort() {
        return this.port;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ISession> getSessions() {
        return this.sessions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GameStorage getGameStorage() {
        return gameStorage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionTerminated(ISession endedSession) {
        this.sessions.remove(endedSession);
        this.sessionCounter--;

        System.out.println("Server: session removed.");
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
                    System.out.println("Server: waiting for a new client...");
                    Socket clientSocket = serverSocket.accept();

                    System.out.println("Server: client connected. " +
                               "Starting new session thread...");
                    sessionCounter++;

                    //
                    ISession newSession = new AsynchroSession(this.server,
                                                     clientSocket,
                                                     gameStorage,
                                                     sessionCounter);

                    //
                    sessions.add(newSession);
                    newSession.start();
                }
            } catch (SocketTimeoutException ex) {    // never happen in current realization
                System.out.println("Server: run warning. " + ex.getMessage());
            } catch (IOException ex) {    // if an I/O error occurs when waiting for a connection.
                if (ex.getMessage().equalsIgnoreCase("Socket closed")) {
                    System.out.println("Server: " + ex.getMessage());    // server socket closed
                } else {
                    System.err.println("Server: error. " + ex.getMessage());    // else exception
                }
            }

            //
            System.out.println("Server: listening(run) thread come to end. Freeing resources.");
            freeResources();
        }

        private void freeResources() {
            //Terminating all sessions
            for (ISession session : sessions) {
                System.out.println("Server: interrupting session " + session.getID() + "...");
                session.terminateSession();
            }
            sessionCounter = 0;

            //Clear all games.
            if (server.gameStorage != null) {
                server.gameStorage.clearGames();
                server.gameStorage = null;
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
                    System.err.println("Server: stop error. " + ex.getMessage());

                    throw ex;
                }

                server.stateControl = StateControl.SHUTDOWNED;
                System.out.println("Server: shutdowned.");
            }
        },
        SHUTDOWNED() {

            @Override
            public void start(Server server) throws IOException {
                try {
                    server.serverSocket = new ServerSocket(server.port, 0);    // throws IOExeption,SecurityException
                    server.listeningThread =
                            new Thread(server.new SocketListen(server));                    
                    server.gameStorage = new GameStorage(server);
                } catch (IOException ex) {
                    System.err.println("Server: start error. " + ex.getMessage());

                    throw ex;
                }

                server.stateControl = StateControl.STARTED;
                server.listeningThread.start();
                System.out.println("Server: started.");
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
