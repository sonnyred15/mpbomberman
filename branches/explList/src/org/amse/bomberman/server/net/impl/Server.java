/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.impl;

import org.amse.bomberman.server.net.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.IllegalBlockingModeException;
import java.util.ArrayList;
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

    private ILog log; // if log wouldn`t be initialized all messages would print to console.
    private int port;
    private List<Game> games;
    private ServerSocket serverSocket;
    private boolean shutdowned = true; //until we start accepting clients.
    private Thread listeningThread;
    private int sessionCounter = 0; //need to generate name of log files.

    /**
     * Constructor with default port.
     */
    public Server() {
        this.port = Constants.DEFAULT_PORT;
        this.games = new ArrayList<Game>();
    }

    /**
     * Constructor that creating Server object with port param.
     * @param port Free port number. Port must be between 0 and 65535, inclusive.
     * '0' for random free port. 0 is not reccomended cause clients must know
     * actual port number to connect.
     */
    public Server(int port) {
        this.port = port;
        this.games = new ArrayList<Game>();
    }

    /**
     * Starts listening thread where ServerSocket.accept() method is using.
     * @throws java.io.IOException if we have error while opening socket.
     */
    public void start() throws IOException, IllegalStateException {
        if (shutdowned) {
            this.shutdowned = false;
            this.sessionCounter = 0;
            this.serverSocket = new ServerSocket(port, 0); // throws IOExeption
            this.listeningThread = new Thread(new SocketListen(this));
            this.listeningThread.start();
            this.games = new ArrayList<Game>();
            this.log = new ConsoleLog();
        } else {
            throw new IllegalStateException("Already accepting. Can`t raise server.");
        }
    }

    /**
     * Closing server socket and ending listening thread and clients threads
     * by calling interrupt()
     * @throws java.io.IOException if an error occurs when closing socket.
     * @throws IllegalStateException if we are trying to shutdown not raised server.
     */
    public void shutdown() throws IOException, IllegalStateException {
        if (!this.shutdowned) {
            try {
                this.shutdowned = true;
                this.listeningThread.interrupt();
                this.listeningThread = null;
                this.serverSocket.close();
                this.serverSocket = null;
                this.games = null;
                this.log.close();
            } catch (IOException ex) {
                System.out.println("IOException while closing log in " +
                        "stopAcceptingClients() " + ex.getMessage());
            } catch (SecurityException ex) {
                System.out.println("Thread can`t interrupt sessions by" +
                        "security reasons in stopAcceptingClients(). " +
                        ex.getMessage());
            }
            writeToLog("Server: shutdowned.");
        } else {
            throw new IllegalStateException("Server is not raised. Can`t shutdown it.");
        }
    }

    public void addGame(Game game) {
        if(!this.shutdowned){
            this.games.add(game);
        }else{
            System.out.println("Tryed to add game to shutdowned server.");
            return;
        }
    }

    public Game getGame(int n) {
        Game game = null;
        if (!this.shutdowned) {
            try {
                game = this.games.get(n);
            } catch (IndexOutOfBoundsException ex) {
                log.println("Client tryed to get game with illegal ID. Canceled.");
            }
        } else {
            System.out.println("Tryed to get game from shutdowned server.");
        }
        return game;
    }

    public List<Game> getGamesList() {
        if (!this.shutdowned){
            return this.games;
        }else{ //if server was stopped.
            System.out.println("Tryed to get games list from shutdowned server.");
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

            } catch (IOException ex) { //if an I/O error occurs when waiting for a connection.
                writeToLog(ex.getMessage());
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
