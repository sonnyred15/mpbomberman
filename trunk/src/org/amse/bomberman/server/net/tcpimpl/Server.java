/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl;

import org.amse.bomberman.server.net.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.ILog;
import org.amse.bomberman.util.impl.ConsoleLog;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Server implements IServer{

    private ILog log; // could be never initialized. Use writeToLog(...) instead of log.println(...)
    private int port;
    private List<Game> games;
    private ServerSocket serverSocket;
    private boolean shutdowned = true; //true until we start accepting clients.
    private Thread listeningThread;
    private int sessionCounter = 0; //need to generate name of log files.

    /**
     * Constructor with default port.
     */
    public Server() {
        this.port = Constants.DEFAULT_PORT;
        this.games = Collections.synchronizedList(new LinkedList<Game>());
    }

    /**
     * Constructor that creating Server object with port param.
     * @param port Free port number. Port must be between 0 and 65535, inclusive.
     * '0' for random free port. 0 is not reccomended cause clients must know
     * actual port number to connect.
     */
    public Server(int port) {
        this.port = port;
        this.games = Collections.synchronizedList(new LinkedList<Game>());
    }

    /**
     * Starts listening thread where ServerSocket.accept() method is using.
     * 
     */
    public void start() throws IOException, IllegalStateException {
        try {

            if (shutdowned) {
                this.shutdowned = false;
                this.sessionCounter = 0;
                this.serverSocket = new ServerSocket(port, 0); // throws IOExeption,SecurityException
                this.listeningThread = new Thread(new SocketListen(this));
                this.listeningThread.start();
                this.games = Collections.synchronizedList(new LinkedList<Game>());
                this.log = new ConsoleLog();
            } else {
                throw new IllegalStateException("Already accepting. Can`t raise server.");
            }

        } catch (IOException ex) {
            writeToLog(ex.getMessage());
            this.shutdown();
            throw ex;
        } catch (SecurityException ex) {
            writeToLog(ex.getMessage());
            this.shutdown();
            throw ex;
        }

    }

    /**
     * Closing server socket and ending listening thread and clients threads
     * by calling interrupt()
     * @throws java.io.IOException if an error occurs when closing socket.
     * @throws IllegalStateException if we are trying to shutdown not raised server.
     */
    public void shutdown() throws IOException, IllegalStateException {
        try {

            if (!this.shutdowned) {
                this.shutdowned = true;
                if (this.listeningThread != null) {
                    this.listeningThread.interrupt();
                    this.listeningThread = null;
                }
                if (this.serverSocket != null) {
                    this.serverSocket.close();
                    this.serverSocket = null;
                }
                this.games = null;
                if (this.log != null) {
                    this.log.close();
                }
            } else {
                throw new IllegalStateException("Server is not raised. Can`t shutdown it.");
            }

        } catch (IOException ex) {
            writeToLog("IOException in stopAcceptingClients() "
                    + ex.getMessage());
            throw ex;
        } catch (SecurityException ex) {
            writeToLog("Thread can`t interrupt sessions by" +
                    "security reasons in stopAcceptingClients(). " +
                    ex.getMessage());
            throw ex;
        }

        writeToLog("Server: shutdowned.");
    }

    public void addGame(Game game) {
        if(!this.shutdowned){
            this.games.add(game);
            writeToLog("Game added.");
        }else{
            writeToLog("Tryed to add game to shutdowned server.");
            return;
        }
    }

    public void removeGame(Game gameToRemove) {
        if(!this.shutdowned){
           this.games.remove(gameToRemove);
           writeToLog("Game removed");
        }else{
            writeToLog("Tryed to remove game from shutdowned server.");
            return;
        }
    }

    public Game getGame(int n) {
        Game game = null;
        if (!this.shutdowned) {
            try {
                game = this.games.get(n);
            } catch (IndexOutOfBoundsException ex) {
                writeToLog("Client tryed to get game with illegal ID. Canceled.");
            }
        } else {
            writeToLog("Tryed to get game from shutdowned server.");
        }
        return game;
    }

    public List<Game> getGamesList() {
        if (!this.shutdowned){
            return this.games;
        }else{ //if server was stopped.
            writeToLog("Tryed to get games list from shutdowned server.");
            return null;
        }
    }

    public boolean isShutdowned() {
        return this.shutdowned;
    }

    public int getPort() {
        return this.port;
    }

    private class SocketListen implements Runnable {

        private Server net;
        private List<ISession> sessions;

        public SocketListen(Server net) {
            this.net = net;
            this.sessions = new ArrayList<ISession>();
        }

        public void run() {

            writeToLog("Server: started.");
            writeToLog("Server: Waiting for a new client...");

            try {

                while (!Thread.interrupted()) {
                    //throws IO, Security, SocketTimeout, IllegalBlockingMode
                    //exceptions
                    Socket clientSocket = serverSocket.accept();
                    writeToLog("Server: Client connected. Starting new session thread...");
                    sessionCounter++;
                    //CHECK V THIS// Is it throwing any exceptions?
                    ISession newSession = new Session(this.net, clientSocket,
                            sessionCounter, this.net.log);
                    this.sessions.add(newSession);
                    newSession.start();
                }

            } catch (SocketTimeoutException ex) { //never happen in current realization
                writeToLog(ex.getMessage());
            } catch (IOException ex) { //if an I/O error occurs when waiting for a connection.
                writeToLog(ex.getMessage()); //or socket closed
            } catch (SecurityException ex) { //accept wasn`t allowed
                writeToLog(ex.getMessage());
            } catch (IllegalBlockingModeException ex) { //CHECK < THIS// what comments should i write?
                writeToLog(ex.getMessage());
            }

            /*must free resources and stop our thread.*/
            for (ISession session : sessions) {
                session.interrupt();
            }

            writeToLog("Server: listening thread come to end.");
        }
    }

    private void writeToLog(String message) {
        if (log == null) {
            System.out.println(message);
        } else {
            log.println(message);
        }
    }
}
