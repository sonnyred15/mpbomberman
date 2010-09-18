/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl.servers;

import java.io.IOException;

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

    public void stop(TcpServer server) throws IOException {
        try {
            //Stop accepting clients by closing ServerSocket
            //So, listening thread will end, but before he must clear
            //all resources: clear games, terminate sessions and so on.
            if(server.getServerSocket() != null) {
                server.getServerSocket().close();
                server.setServerSocket(null);
            }

            //nulling listeningThread.
            if(server.getListeningThread() != null) {
                server.setListeningThread(null);
            }

            //TODO terminate SESSIONS!!!!!11
        } catch (IOException ex) {
            System.err.println("Server: stop error. " + ex.getMessage());

            throw ex;
        }

        server.setServerState(StoppedState.getInstance());
        System.out.println("Server: shutdowned.");
    }

}
