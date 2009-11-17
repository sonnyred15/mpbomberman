/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;
import java.util.ArrayList;
import java.util.List;
import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.util.ILog;
import org.amse.bomberman.util.impl.ConsoleLog;
import org.amse.bomberman.util.impl.FileLog;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Net {

    public static int DEFAULT_PORT = 10500;
    public static int DEFAULT_ACCEPT_TIMEOUT = 60000; // 1 minute
    //CHECK ^ THIS // maybe create Defaults class in Util package???
    private ILog log; // if log wouldn`t be initialized all messages would print to console.
    private int port;
    private List<Game> games;
    private ServerSocket serverSocket;
    private boolean shutdowned;
    private Thread listeningThread;
    private int sessionCounter = 0;

    /**
     * Constructor with default port.
     */
    public Net() {
        this.port = Net.DEFAULT_PORT;
        this.games = new ArrayList<Game>();
    }

    /**
     * Constructor that creating Net object with port param.
     * @param port Free port number. Port must be between 0 and 65535, inclusive.
     * '0' for random free port.
     */
    public Net(int port) {
        this.port = port;
        this.games = new ArrayList<Game>();
    }

    /**
     * Starts listening thread where ServerSocket.accept() method is using.
     * @throws java.io.IOException if we have error while opening socket.
     */
    public void startAcceptingClients() throws IOException, IllegalStateException {
        if (this.listeningThread == null) {
            this.log = new ConsoleLog();
            this.shutdowned = false;
            //CHECK ^ THIS// what if daemon threads still didnt close!? syncronize
            this.serverSocket = new ServerSocket(port, 0); // throws IOExeption
            this.listeningThread = new Thread(new SocketListen(this));
            this.listeningThread.start();
            this.games = new ArrayList<Game>();
        } else {
            throw new IllegalStateException("Already accepting. Can`t raise server.");
        }
    }

    /**
     * Closing server socket and ending listening thread by changing
     * boolean shutdowned on true. 
     * @throws java.io.IOException if an error occurs when closing socket.
     * @throws IllegalStateException if we are trying to shutdown not raised server.
     */
    public void stopAcceptingClients() throws IOException, IllegalStateException {
        this.shutdowned = true;
        this.log.close();
        if (this.serverSocket != null) {
            this.serverSocket.close();// to shutdown without waiting timeout.
            //CHECK ^ THIS//is thi is normal to close socket.
            //or maybe it is better to make so called "fake-connect"
            this.serverSocket = null;
            this.listeningThread = null;
            this.games = null;
        } else {
            throw new IllegalStateException("Server is not raised. Can`t shutdown it.");
        }
    }

    public void addGame(Game game) {
        this.games.add(game);
    }

    public Game getGame(int n) {
        Game game = null;
        try {
            game = this.games.get(n);
        } catch (IndexOutOfBoundsException ex) {
            ; //do_nothing;
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAA");
        }
        return game;
    }

    public List<Game> getGamesList() {
        return this.games; //CHECK < THIS// never return null!?
    }

    public boolean isShutdowned() {
        return this.shutdowned;
    }

    public int getPort() {
        return this.port;
    }

    private class SocketListen implements Runnable {

        private Net net;

        private SocketListen() {
        }

        public SocketListen(Net net) {
            this.net = net;
        }

        public void run() {

            writeToLog("Server: started.");
            writeToLog("Server: Waiting for a new client...");

            try {
                serverSocket.setSoTimeout(DEFAULT_ACCEPT_TIMEOUT);//throws SocketException
                while (!shutdowned) {

                    Socket clientSocket = null;
                    try {
                        //throws IO, Security, SocketTimeout, IllegalBlockingMode
                        //exceptions
                        clientSocket = serverSocket.accept();
                        writeToLog("Server: Client connected. Starting new session thread...");
                        sessionCounter++;
                        new Session(clientSocket, this.net, new FileLog(sessionCounter + "ses.log")).start();
                    //CHECK ^ THIS//maybe we must store all sessions???(to interrupt)
                    } catch (SocketTimeoutException ex) {
                        writeToLog("socket timeout");
                    }

                }
            } catch (IOException ex) { //if an I/O error occurs when waiting for a connection.
                writeToLog(ex.getMessage());
            } catch (SecurityException ex) { //accept wasn`t allowed
                writeToLog(ex.getMessage());
            } catch (IllegalBlockingModeException ex) { //CHECK < THIS// what comments should i write?
                writeToLog(ex.getMessage());
            }

            writeToLog("Server: shutdowned.");
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
