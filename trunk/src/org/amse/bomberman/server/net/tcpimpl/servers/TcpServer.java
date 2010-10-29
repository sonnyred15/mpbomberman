package org.amse.bomberman.server.net.tcpimpl.servers;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import org.amse.bomberman.server.ServiceContext;

import org.amse.bomberman.server.net.*;

/**
 *
 * @author Kirilchuk V.E.
 */
public class TcpServer implements Server {

    private volatile ServerState serverState = StoppedState.getInstance();
    //
    private ServerSocket   serverSocket;
    
    private Thread         listeningThread;
    //
    private volatile long lastId = 0;    // need to generate unique ID`s for sessions.
    //
    private final ServiceContext context;
    private final Comparator<Session> comparator = new Comparator<Session>() {

        @Override
        public int compare(Session ses1, Session ses2) {
            long id1 = ses1.getId();
            long id2 = ses2.getId();
            return (int)(id1 - id2);// loss of presicion
        }

    };
    private final Set<Session> sessions =
            new ConcurrentSkipListSet<Session>(comparator);

    public TcpServer(ServiceContext context) {
        this.context = context;
    }
    //

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void start(int port) throws IOException,
                                            IllegalStateException {
        serverState.start(this, port);
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
                                           IllegalStateException {
        serverState.stop(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStopped() {// don`t need synchronized cause state is volatile.
        return (serverState == StoppedState.getInstance());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPort() {
        return serverSocket.getLocalPort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Session> getSessions() {
        return sessions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionTerminated(Session endedSession) {
        sessions.remove(endedSession);
        System.out.println("Server: session removed.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceContext getServiceContext() {
        return context;
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