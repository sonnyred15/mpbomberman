package org.amse.bomberman.server.net.tcpimpl.servers;

//~--- non-JDK imports --------------------------------------------------------
import org.amse.bomberman.server.net.*;
import org.amse.bomberman.util.Constants;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.net.ServerSocket;
import java.util.Collections;
import java.util.Comparator;


import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import org.amse.bomberman.server.gameservice.GameStorage;

/**
 *
 * @author Kirilchuk V.E.
 */
public class TcpServer implements Server {

    private final int port;
    //    
    private volatile ServerState serverState = StoppedState.getInstance();
    //
    private ServerSocket serverSocket;
    private GameStorage  gameStorage;
    private Thread       listeningThread;
    //
    private volatile long lastId = 0;    // need to generate unique ID`s for sessions.
    //
    private final Comparator<Session> comparator = new Comparator<Session>() {

        public int compare(Session ses1, Session ses2) {
            long id1 = ses1.getId();
            long id2 = ses2.getId();
            return (int)(id1 - id2);// loss of presicion
        }

    };
    private final Set<Session> sessions =
            new ConcurrentSkipListSet<Session>(comparator);
    //    

    /**
     * Constructor with default port.
     */
    public TcpServer() {
        this(Constants.DEFAULT_PORT);
    }

    /**
     * Main constructor that creating Server object with port param.
     * @param port Free port number. Port must be between 1 and 65535, inclusive.
     * 
     */
    public TcpServer(int port) {
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException();
        }
        this.port = port;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void start() throws IOException,
                                            IllegalStateException {
        this.serverState.start(this);
    }

    /**
     * Closing server socket and ending listening thread and clients threads.
     * Freeing resources: clearing games on game storage,
     * reseting counter and so on.
     * @throws java.io.IOException if an error occurs when closing socket.
     * @throws IllegalStateException if we are trying to shutdown not raised server.
     */
    public synchronized void stop() throws IOException,
                                           IllegalStateException {
        this.serverState.stop(this);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isStopped() {// don`t need synchronized cause state is volatile.
        return (this.getServerState() == StoppedState.getInstance());
    }

    /**
     * {@inheritDoc}
     */
    public int getPort() {
        return port;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Session> getSessions() {
        return this.sessions;
    }

    /**
     * {@inheritDoc}
     */
    public void sessionTerminated(Session endedSession) {
        this.sessions.remove(endedSession);
        System.out.println("Server: session removed.");
    }

    /**
     * @return the serverState
     */
    ServerState getServerState() {
        return serverState;
    }

    /**
     * @param serverState the serverState to set
     */
    void setServerState(ServerState serverState) {
        this.serverState = serverState;
    }

    /**
     * @return the serverSocket
     */
    ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * @param serverSocket the serverSocket to set
     */
    void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**
     * {@inheritDoc}
     */
    public GameStorage getGameStorage() {
        return gameStorage;
    }

    /**
     * @param gameStorage the gameStorage to set
     */
    void setGameStorage(GameStorage gameStorage) {
        this.gameStorage = gameStorage;
    }

    /**
     * @return the listeningThread
     */
    Thread getListeningThread() {
        return listeningThread;
    }

    /**
     * @param listeningThread the listeningThread to set
     */
    void setListeningThread(Thread listeningThread) {
        this.listeningThread = listeningThread;
    }

    /**
     * @return the sessionCounter
     */
    long getLastId() {//don`t need synchronize cause lastId is volatile
        return lastId;
    }

    /**
     * @param sessionCounter the sessionCounter to set
     */
    void setLastId(long id) {
        this.lastId = id;
    }
}
