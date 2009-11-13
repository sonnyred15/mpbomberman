/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import org.amse.bomberman.server.gameInit.Game;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Net {

    private int port = 10500;
    private List<Game> games;
    private ServerSocket serverSocket;
    private boolean exit;
    private Thread listeningThread;

    /**
     * 
     * @throws java.io.IOException if we have error while opening the socket.
     */
    public Net() throws IOException {
        this.exit = false;
        this.games = new ArrayList<Game>();
    }

    public Net(int port) throws IOException {
        this();
        this.port = port;
    }

    /**
     *  Starts listening thread where ServerSocket.accept() method is using.
     * @throws java.io.IOException if we have error while opening socket.
     */
    public void startAcceptingClients() throws IOException {
        if (this.listeningThread == null) {
            this.exit = false;
            this.serverSocket = new ServerSocket(port, 0, InetAddress.getByName("localhost"));
            this.listeningThread = new Thread(new SocketListen(this));
            this.listeningThread.start();
        } else {
            System.out.println("Already accepting!");
        }
    }

    /**
     * Closing server socket and ending listening thread by changing
     * boolean exit on true. 
     * @throws java.io.IOException if an error occurs when closing socket.
     */
    public void stopAcceptingClients() throws IOException {
        this.exit = true;
        if (this.serverSocket != null) {
            this.serverSocket.close();// to shutdown without waiting timeout.
        //CHECK ^ THIS//
        }
        this.listeningThread = null;
    }

    public void addGame(Game game) {
        this.games.add(game);
    }

    public Game getGame(int n) {
        return this.games.get(n);
    }

    public List<Game> getGamesList() {
        return this.games;
    }
    
    public boolean isShutdowned() {
        return this.exit;       
    }

    private class SocketListen implements Runnable {

        private Net net;

        private SocketListen() {
        }

        public SocketListen(Net net) {
            this.net = net;
        }      

        public void run() {
            System.out.println("Server: started.");
            System.out.println("Server: Waiting for a new client...");
            try {
                serverSocket.setSoTimeout(60000);//1 minute
                while (!exit) {
                    Socket clientSocket = null;
                    try {
                        clientSocket = serverSocket.accept();
                    } catch (SocketTimeoutException ex) {
                        System.out.println("socket timeout");
                    }
                    if (clientSocket != null) {
                        System.out.println("Server: Client connected. Starting new session thread...");
                        new Session(clientSocket, this.net).start();
                    }
                }
            } catch (IOException ex) {
                //
            }
            System.out.println("Server: shutdowned.");
        }
    }
}
