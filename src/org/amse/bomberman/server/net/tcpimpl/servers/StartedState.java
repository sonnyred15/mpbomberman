/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl.servers;

import java.io.IOException;
import org.amse.bomberman.server.net.Session;

/**
 *
 * @author Kirilchuk V.E.
 */
class StartedState implements ServerState {

    private static final ServerState INSTANCE = new StartedState();

    private StartedState() {
    }

    static ServerState getInstance() {
        return INSTANCE;
    }

    public void start(TcpServer server) throws IOException,
                                               IllegalStateException {
        System.err.println(
                "Server: start error. Already in started state.");
        throw new IllegalStateException("Server: start error. "
                + "Already in started state.");

    }

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
            System.out.println(
                    "Server: interrupting session " + session.getId() + "...");
            session.terminateSession();
        }

        server.setLastId(0);

        //Clear games.
        if (server.getGameStorage() != null) {
            server.getGameStorage().clearGames();
        }

        server.setServerState(StoppedState.getInstance());
        System.out.println("Server: shutdowned.");
    }

}
