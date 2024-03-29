package org.amse.bomberman.server.net.tcpimpl.servers;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import org.amse.bomberman.server.net.Session;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.AsynchroThreadSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kirilchuk V.E.
 */
class ServerThread implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ServerThread.class);
    
    private final TcpServer server;

    ServerThread(TcpServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            while(!server.isShutdowned()) {
                Socket clientSocket = server.getServerSocket().accept();
                //
                Session newSession =
                        new AsynchroThreadSession(clientSocket, server.getLastId(),
                                                  server.getServiceContext());
                //
                synchronized(server) {//for atomicity of get-set
                    long lastId = server.getLastId();
                    server.setLastId(lastId + 1);
                                     
                }
                server.getSessions().add(newSession);
                newSession.addEndListener(server);
                newSession.start();
            }
        } catch (SocketTimeoutException ex) {
            // never happen in current realization
           LOG.warn("ServerThread: run warning.", ex);
        } catch (IOException ex) {
            // if an I/O error occurs when waiting for a connection.
            if(!server.getServerSocket().isClosed()) {
                ex.printStackTrace();
            }
        }
        LOG.info("ServerThread stopped.");
    }
}
