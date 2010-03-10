/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.client.net.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.amse.bomberman.client.net.IConnector2;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.view.control.Controller;
import org.amse.bomberman.util.Constants.Command;
import org.amse.bomberman.util.Constants.Direction;

/**
 *
 * @author Kirilchuk V.E.
 */
public class AsynchroConnector implements IConnector2 {

    private Socket socket;
    private final Controller controller;

    public AsynchroConnector(Controller controller) {
        this.controller = controller;
    }

    public void сonnect(InetAddress address, int port) throws
            UnknownHostException, IOException, IllegalArgumentException {

        this.socket = new Socket(address, port);
        Thread t = new Thread(new ServerListen());
        t.start();
    }

    public void requestGamesList() throws NetException {
        sendRequest("" + Command.GET_GAMES.getValue());
    }

    private synchronized void sendRequest(String request) throws NetException {
        PrintWriter out = null;
        try {
            OutputStream os = this.socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            out = new PrintWriter(osw);

            out.println(request);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestLeaveGame() throws NetException {
        sendRequest("" + Command.LEAVE_GAME.getValue());
    }

    public void requestCreateGame(String gameName, String mapName, int maxPl)
            throws NetException {

        sendRequest("" + Command.CREATE_GAME.getValue() +
                " " + gameName +
                " " + mapName +
                " " + maxPl);
    }

    public void requestJoinGame(int gameID) throws NetException {
        sendRequest("2 " + gameID + " " + "playerName");
    }

    public void requestDoMove(Direction dir) throws NetException {
        sendRequest("3 " + dir.getValue());
    }

    public void requestStartGame() throws NetException {
        sendRequest("" + Command.START_GAME.getValue());
    }

    public void requestMap() throws NetException {
        sendRequest("" + Command.GET_MAP_ARRAY.getValue());
    }

    public void requestPlantBomb() throws NetException {
        sendRequest("" + Command.PLACE_BOMB.getValue());
    }

    public void requestJoinBotIntoGame(int gameID) throws NetException {
        sendRequest("" + Command.ADD_BOT_TO_GAME.getValue() + " " + gameID + " BOT");
    }

    public void requestMapsList() throws NetException {
    }

    public void requestIsGameStarted() throws NetException {
        sendRequest("" + Command.GET_GAME_STATUS.getValue());
    }

    public void requestGameInfo() throws NetException {
        sendRequest("" + Command.GET_MY_GAME_INFO.getValue());
    }

    public void sendChatMessage(String message) throws NetException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void getNewChatMessages() throws NetException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public InetAddress getInetAddress() { //PENDING is it useless??
        return this.socket.getInetAddress();
    }

    public int getPort() {
        return this.socket.getPort(); //PENDING i think this port different from one in constructor
    }

    public void beginUpdating() throws NetException {
        throw new UnsupportedOperationException("Not supported in current implementation.");
    }

    private class ServerListen implements Runnable {

        public void run() {
            BufferedReader in = null;
            try {
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                in = new BufferedReader(isr);

                String oneLine;
                List<String> message = new ArrayList<String>();
                while (!Thread.interrupted()) {
                    while ((oneLine = in.readLine()) != null) {
                        if (oneLine.length() == 0) {
                            break;
                        }
                        message.add(oneLine);
                    }
                    processServerMessage(message);
                    message.clear();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private void processServerMessage(List<String> message) {//TODO fix NPE
            String firstLine = message.get(0);
            System.out.println("GETTED SERVER MESSAGE FIRSTLINE=" + firstLine);

            /* Games list getted from server.*/
            if (firstLine.startsWith("Games list")) {
                message.remove(0);
                controller.updateGamesList(message);

            /* Create game info*/
            } else if (firstLine.startsWith("Create game")) {
                controller.updateCreateGameResult(message.get(1));

            /* My game info */
            } else if (firstLine.startsWith("Game info")) {
                message.remove(0);
                controller.updateGameInfo(message);

            /* Join game info*/
            } else if (firstLine.startsWith("Join game")) {                
                controller.updateJoinGameResult(message.get(1));

            /* Advise to update game info*/
            } else if (firstLine.startsWith("Update game info")) {
                controller.requestGameInfo();
                
            /* Advise to update games list info*/
            } else if (firstLine.startsWith("Update games info")) {
                controller.requestGamesList();
                
            } else { //all other messages
                JOptionPane.showMessageDialog(null, "Uncatched message in processServerMessage \n" +
                        " " + firstLine);

            }
        }
    }
}
