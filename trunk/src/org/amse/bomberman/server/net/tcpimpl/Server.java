
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.net.*;
import org.amse.bomberman.server.view.ServerChangeListener;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.ILog;
import org.amse.bomberman.util.impl.ConsoleLog;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import java.nio.channels.IllegalBlockingModeException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.server.gameinit.GameStorage;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Server implements IServer {
    private ILog log = new ConsoleLog();    // could be never initialized. Use writeToLog(...) instead of log.println(...)
    private final int            port;

    //    
    private final ServerState    serverState = ServerState.SHUTDOWNED;
    //
    private ServerSocket         serverSocket;
    private GameStorage          gameStorage;
    private volatile boolean     shutdowned = true;    // true until we start accepting clients.
    private Thread               listeningThread;
    private final List<ISession> sessions =
                                         new CopyOnWriteArrayList<ISession>();
    private int                  sessionCounter = 0;    // need to generate name of log files.
    private long                 startTime;
    private ServerChangeListener changeListener;

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
        try {
            if (shutdowned) {
                this.shutdowned = false;
                this.serverSocket = new ServerSocket(port, 0);    // throws IOExeption,SecurityException
                this.listeningThread = new Thread(new SocketListen(this));
                this.listeningThread.start();
                this.gameStorage = new GameStorage(this);
            } else {
                throw new IllegalStateException("Server: start error. " +
                                                "Already accepting. " +
                                                "Can`t raise.");
            }
        } catch (IOException ex) {
            writeToLog("Server: start error. " + ex.getMessage());

            throw ex;
        } catch (SecurityException ex) {
            writeToLog("Server: start error. " + ex.getMessage());

            throw ex;
        }

        this.startTime = System.currentTimeMillis();

        if (this.changeListener != null) {
            this.changeListener.changed(this);
        }

        writeToLog("Server: started.");
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
        try {
            if (!this.shutdowned) {
                this.shutdowned = true;

                if (this.listeningThread != null) {
                    this.listeningThread = null;
                }

                // this.sessionCounter = 0;
                // this.sessions.clear(); SESSIONS MUST AUTOCLEAR BY INTERRUPT!!!
                this.gameStorage.clearGames();

                if (this.serverSocket != null) {
                    this.serverSocket.close();
                    this.serverSocket = null;
                }

                if (this.log != null) {
                    try {
                        this.log.close();
                    } catch (IOException ex) {
                        System.out.println("Server: stop warning. " +
                                           "Can`t save log." + ex.getMessage());
                    }
                }
            } else {
                throw new IllegalStateException("Server: stop error. " +
                                                "Is not raised. " +
                                                "Can`t shutdown.");
            }
        } catch (IOException ex) {
            writeToLog("Server: stop error. " + ex.getMessage());

            throw ex;
        } catch (SecurityException ex) {
            writeToLog("Server: stop error. " + ex.getMessage());

            throw ex;
        }

        if (this.changeListener != null) {
            this.changeListener.changed(this);
        }

        writeToLog("Server: shutdowned.");
    }

    @Override
    public boolean isShutdowned() {
        return this.shutdowned;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public long getWorkTime() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    @Override
    public List<ISession> getSessions() {
        return this.sessions;
    }

    @Override
    public void sessionTerminated(ISession endedSession) {
        this.sessions.remove(endedSession);
        this.sessionCounter--;

        if (this.changeListener != null) {
            this.changeListener.changed(this);
        }

        writeToLog("Server: session removed.");
    }

    @Override
    public void setChangeListener(ServerChangeListener logListener) {
        this.changeListener = logListener;
    }

    @Override
    public ServerChangeListener getChangeListener() {
        return this.changeListener;
    }

    @Override
    public List<String> getLog() {
        return log.getLog();
    }

    @Override
    public void writeToLog(String message) {
        if ((log == null) || log.isClosed()) {
            System.out.println(message);
        } else {
            log.println(message);
        }

        if (changeListener != null) {
            changeListener.addedToLog(message);
        }
    }

    private class SocketListen implements Runnable {
        private Server server;

        public SocketListen(Server net) {
            this.server = net;
        }

        @Override
        public void run() {
            writeToLog("Server: waiting for a new client...");

            try {
                while (!shutdowned) {

                    // throws IO, Security, SocketTimeout, IllegalBlockingMode
                    // exceptions
                    Socket clientSocket = serverSocket.accept();

                    writeToLog("Server: client connected. " +
                               "Starting new session thread...");
                    sessionCounter++;

                    ISession newSession = null;

                    //
                    newSession = new AsynchroSession( this.server,
                                                     clientSocket,
                                                     gameStorage,
                                                     sessionCounter,
                                                     this.server.log);

                    //
                    sessions.add(newSession);
                    newSession.start();

                    if (changeListener != null) {
                        changeListener.changed(this.server);
                    }
                }
            } catch (SocketTimeoutException ex) {    // never happen in current realization
                writeToLog("Server: run warning. " + ex.getMessage());
            } catch (IOException ex) {    // if an I/O error occurs when waiting for a connection.
                if (ex.getMessage().equalsIgnoreCase("Socket closed")) {
                    writeToLog("Server: " + ex.getMessage());    // server socket closed
                } else {
                    writeToLog("Server: error. " + ex.getMessage());    // else exception
                }
            } catch (SecurityException ex) {    // accept wasn`t allowed
                writeToLog("Server: run error. " + ex.getMessage());
            } catch (IllegalBlockingModeException ex) {    // CHECK < THIS// what comments should i write?
                writeToLog("Server: run error. " + ex.getMessage());
            }

            /* must free resources and stop our thread. */
            int i = 1;

            for (ISession session : sessions) {
                writeToLog("Server: interrupting session " + i + "...");
                session.terminateSession();
                ++i;
            }

            writeToLog("Server: listening(run) thread come to end.");
        }
    }
}
