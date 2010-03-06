/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;
import java.util.List;
import org.amse.bomberman.server.net.ISession;

/**
 *
 * @author Kirilchuk V.E.
 */
public class AsynchroServer extends Server {

    public AsynchroServer(int port) {
        super(port);
    }

    /**
     * Starts listening thread where ServerSocket.accept() method is using.
     *
     */
    @Override
    public synchronized void start() throws IOException, IllegalStateException {
        try {

            if (this.shutdowned) {
                this.shutdowned = false;
                this.serverSocket = new ServerSocket(port, 0); // throws IOExeption,SecurityException
                this.listeningThread = new Thread(new SocketListen(this));
                this.listeningThread.start();
            } else {
                throw new IllegalStateException("Server: start error. Already accepting. Can`t raise.");
            }

        } catch (IOException ex) {
            writeToLog("Server: start error. " + ex.getMessage());
            throw ex;
        } catch (SecurityException ex) {
            writeToLog("Server: start error. " + ex.getMessage());
            throw ex;
        }

        this.startTime = System.currentTimeMillis();
        if (this.changeListener != null) {
            this.changeListener.switchedState(true);
        }
        writeToLog("Server: started.");
    }

    private class SocketListen implements Runnable {

        private Server net;

        public SocketListen(Server net) {
            this.net = net;
        }

        public void run() {

            writeToLog("Server: waiting for a new client...");

            try {

                while (!shutdowned) {
                    //throws IO, Security, SocketTimeout, IllegalBlockingMode
                    //exceptions
                    Socket clientSocket = serverSocket.accept();
                    writeToLog("Server: client connected. Starting new session thread...");
                    sessionCounter++;
                    //CHECK V THIS// Is it throwing any exceptions?
                    ISession newSession = new AsynchroSession(this.net, clientSocket,
                            sessionCounter, this.net.log);
                    sessions.add(newSession);
                    newSession.start();
                }

            } catch (SocketTimeoutException ex) { //never happen in current realization
                writeToLog("Server: run warning. " + ex.getMessage());
            } catch (IOException ex) { //if an I/O error occurs when waiting for a connection.
                if (ex.getMessage().equalsIgnoreCase("Socket closed")) {
                    writeToLog("Server: " + ex.getMessage()); //server socket closed
                } else {
                    writeToLog("Server: error. " + ex.getMessage()); //else exception
                }
            } catch (SecurityException ex) { //accept wasn`t allowed
                writeToLog("Server: run error. " + ex.getMessage());
            } catch (IllegalBlockingModeException ex) { //CHECK < THIS// what comments should i write?
                writeToLog("Server: run error. " + ex.getMessage());
            }

            /*must free resources and stop our thread.*/
            int i = 1;
            synchronized (sessions) {
                for (ISession session : sessions) {
                    writeToLog("Server: interrupting session " + i + "...");
                    session.interruptSession();
                    ++i;
                }
            }

            writeToLog("Server: listening(run) thread come to end.");
        }
    }

    @Override
    public void notifyAllClients(String message) {
        for (ISession session : sessions) {
            session.sendAnswer(message);
        }
    }

    @Override
    public void notifyAllClients(List<String> messages) {
        for (ISession session : sessions) {
            session.sendAnswer(messages);
        }
    }

    @Override
    public void notifySomeClients(List<ISession> sessions, List<String> messages) {
        for (ISession session : sessions) {
            session.sendAnswer(messages);
        }
    }

    @Override
    public void notifyAllClientsExceptOne(List<String> messages, ISession sessionToIgnore) {
        for (ISession session : sessions) {
            if (session != sessionToIgnore) {
                session.sendAnswer(messages);
            }
        }
    }
}
