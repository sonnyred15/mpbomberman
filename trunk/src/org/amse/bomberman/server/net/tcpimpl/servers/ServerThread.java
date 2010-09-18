package org.amse.bomberman.server.net.tcpimpl.servers;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import org.amse.bomberman.server.net.Session;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.AsynchroThreadSession;

/**
 *
 * @author Kirilchuk V.E.
 */
class ServerThread implements Runnable {

    private TcpServer server;

    ServerThread(TcpServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            while(!server.isStopped()) {
                System.out.println("Server: waiting for a new client...");
                Socket clientSocket = server.getServerSocket().accept();
                System.out.println("Server: client connected. "
                        + "Starting new session thread...");
                server.setSessionCounter(server.getSessionCounter() + 1); //TODO synchronize problem;
                //
                Session newSession =
                        new AsynchroThreadSession(server, clientSocket,
                                                  server.getGameStorage(),
                                                  server.getSessionCounter());
                //
                server.sessions.add(newSession);
                newSession.start();
            }
        } catch (SocketTimeoutException ex) {
            // never happen in current realization
            System.out.println("Server: run warning. " + ex.getMessage());
        } catch (IOException ex) {
            // if an I/O error occurs when waiting for a connection.
            if(ex.getMessage().equalsIgnoreCase("Socket closed")) {
                System.out.println("Server: " + ex.getMessage()); // server socket closed
            } else {
                System.err.println("Server: error. " + ex.getMessage()); // else exception
            }
        }
        System.out.println(
                "Server: listening(run) thread come to end. Freeing resources.");
        freeResources();
    }

    private void freeResources() {
        //Terminating all sessions
        for(Session session : server.sessions) {
            System.out.println(
                    "Server: interrupting session " + session.getID() + "...");
            session.terminateSession();
        }
        server.setSessionCounter(0);
        //Clear all games.
        if(server.getGameStorage() != null) {
            server.getGameStorage().clearGames();
            server.setGameStorage(null);
        }
    }

}
