/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl.servers;

import java.io.IOException;
import java.net.ServerSocket;
import org.amse.bomberman.server.gameinit.GameStorage;

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

    public void start(TcpServer server) throws IOException {
        try {
            server.setServerSocket(new ServerSocket(server.getPort(), 0));    // throws IOExeption,SecurityException
            server.setListeningThread(new Thread(new ServerThread(server)));
            server.setGameStorage(new GameStorage(server));
        } catch (IOException ex) {
            System.err.println("Server: start error. " + ex.getMessage());

            throw ex;
        }

        server.setServerState(StartedState.getInstance());
        server.getListeningThread().start();
        System.out.println("Server: started.");
    }

    public void stop(TcpServer server) {
        System.err.println("Server: shutdown error. Already shutdowned.");
        throw new IllegalStateException("Server: shutdown error. "
                + "Already shutdowned.");
    }

}
