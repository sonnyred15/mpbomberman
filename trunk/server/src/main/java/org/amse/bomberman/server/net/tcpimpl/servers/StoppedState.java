package org.amse.bomberman.server.net.tcpimpl.servers;

import java.io.IOException;
import java.net.ServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kirilchuk V.E.
 */
class StoppedState implements ServerState {

    private static final Logger LOG = LoggerFactory.getLogger(StoppedState.class);
    
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
            LOG.error("Server: start error.", ex);
            throw ex;
        }

        server.setServerState(StartedState.getInstance());
        server.getListeningThread().start();
        LOG.info("Server started. You can connect to port: " + server.getPort());
    }

    @Override
    public void stop(TcpServer server) {
        LOG.info("Server: shutdown error. Already shutdowned.");
        throw new IllegalStateException("Server: shutdown error. "
                + "Already shutdowned.");
    }

}
