package org.amse.bomberman.server.net.tcpimpl.servers;

import java.io.IOException;
import org.amse.bomberman.server.net.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kirilchuk V.E.
 */
class StartedState implements ServerState {

    private static final Logger LOG = LoggerFactory.getLogger(StartedState.class);
    
    private static final ServerState INSTANCE = new StartedState();

    private StartedState() {
    }

    static ServerState getInstance() {
        return INSTANCE;
    }

    @Override
    public void start(TcpServer server, int port) throws IOException,
                                               IllegalStateException {
        LOG.error("Server: start error. Already in started state.");
        throw new IllegalStateException("Server: start error. "
                + "Already in started state.");

    }

    @Override
    public void stop(TcpServer server) {
        //Stop accepting clients by closing ServerSocket
        //So, listening thread will end.
        if (server.getServerSocket() != null) {
            try {
                server.getServerSocket().close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        //Terminating all sessions
        for (Session session : server.getSessions()) {
            LOG.info("Server: interrupting session " + session.getId() + "...");
            session.terminateSession();
        }

        server.setLastId(0);

        //Clear games.
        if (server.getServiceContext() != null) {
            server.getServiceContext().getGameStorage().clearGames();
        }

        server.setServerState(StoppedState.getInstance());
        LOG.info("Server: shutdowned.");
    }

}
