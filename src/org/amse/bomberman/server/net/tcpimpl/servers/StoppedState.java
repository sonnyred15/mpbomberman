package org.amse.bomberman.server.net.tcpimpl.servers;

import java.io.IOException;
import java.net.ServerSocket;

/**
 *
 * @author Kirilchuk V.E.
 */
class StoppedState implements ServerState {

    private static final ServerState INSTANCE = new StoppedState();

    private StoppedState() {
    }

    static ServerState getInstance() {
        return INSTANCE;
    }

    @Override
    public void start(TcpServer server, int port) throws IOException {
        try {
            server.setServerSocket(new ServerSocket(port, 0));    // throws IOExeption,SecurityException
            server.setListeningThread(new Thread(new ServerThread(server)));
        } catch (IOException ex) {
            System.err.println("Server: start error. " + ex.getMessage());

            throw ex;
        }

        server.setServerState(StartedState.getInstance());
        server.getListeningThread().start();
        System.out.println("Server: started.");
    }

    @Override
    public void stop(TcpServer server) {
        System.err.println("Server: shutdown error. Already shutdowned.");
        throw new IllegalStateException("Server: shutdown error. "
                + "Already shutdowned.");
    }

}
