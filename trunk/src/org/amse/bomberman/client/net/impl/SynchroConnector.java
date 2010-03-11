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
import java.util.Timer;
import java.util.TimerTask;
import org.amse.bomberman.client.net.IConnector2;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.view.control.Controller;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Constants.Command;
import org.amse.bomberman.util.Constants.Direction;

/**
 *
 * @author Kirilchuk V.E.
 */
public class SynchroConnector implements IConnector2 {

    private Socket socket;
    private final Controller controller;
    private Timer timer;

    public SynchroConnector(Controller controller) {
        this.controller = controller;
    }

    public void сonnect(InetAddress address, int port) throws
            UnknownHostException,
            IOException {
        this.socket = new Socket(address, port);
    }

    public void requestLeaveGame() throws NetException {
        // true stop timer?
        if (timer != null) {
            timer.cancel();
        }
        List<String> answer = queryAnswer("" + Command.LEAVE_GAME.getValue());
        System.out.println(answer.get(0));
        this.controller.updateLeaveGameResult(answer.get(0));
    }

    public void requestGamesList() throws NetException {
        List<String> games = queryAnswer("" + Command.GET_GAMES.getValue());
        for (String string : games) {
            System.out.println(string);
        }
        this.controller.updateGamesList(games);
    }

    public void requestCreateGame(String gameName, String mapName, int maxPl)
            throws NetException {
        List<String> answer = queryAnswer("" + Command.CREATE_GAME.getValue() +
                " " + gameName + " " + mapName + " " + maxPl);
        this.controller.updateCreateGameResult(answer.get(0));
    }

    public void requestJoinGame(int gameID) throws NetException {
        List<String> list = queryAnswer("2 " + gameID);
        String answer = list.get(0);
        System.out.println(answer);
        this.controller.updateJoinGameResult(answer);
    }

    public void requestDoMove(Direction dir) throws NetException {
        List<String> list = queryAnswer("3 " + dir.getValue());
        String answer = list.get(0);
        this.controller.updateDoMoveResult(answer);
    }

    public void requestStartGame() throws NetException {
        List<String> list = queryAnswer("" + Command.START_GAME.getValue());
        System.out.println(list);
        this.controller.updateStartGameResult(list.get(0));
    }

    public void requestGameMap() throws NetException {
        List<String> gameMap = queryAnswer("" + Command.GET_MAP_ARRAY.getValue());
        this.controller.updateGameMap(gameMap);
    }

    public void requestPlantBomb() throws NetException {
        List<String> list = queryAnswer("" + Command.PLACE_BOMB.getValue());
        System.out.println(list.get(0));
        this.controller.updatePlantBombResult(list.get(0));
    }

    public void requestJoinBotIntoGame(int gameID) throws NetException {
        // bot name???!!!
        List<String> list = queryAnswer("" +
                Command.ADD_BOT_TO_GAME.getValue() + " " + gameID + " BOT");
        String answer = list.get(0);
        System.out.println(answer);
        this.controller.updateJoinBotResult(answer);
    }

    public void requestGameMapsList() throws NetException {
        List<String> gameMaps = queryAnswer("" + Command.GET_MAPS_LIST.getValue());
        this.controller.updateMapsList(gameMaps);
    }

    public void requestIsGameStarted() throws NetException {
        List<String> list = queryAnswer("" + Command.GET_GAME_STATUS.getValue());
        this.controller.updateIsGameStarted(list.get(0));
    }

    public void requestGameInfo() throws NetException {
        List<String> list = queryAnswer("" + Command.GET_MY_GAME_INFO.getValue());
        this.controller.updateGameInfo(list);
    }

    public void sendChatMessage(String message) throws NetException {
        List<String> answer = queryAnswer("" + Command.CHAT_ADD_MSG.getValue() +
                " " + message);
        this.controller.updateNewChatMessages(answer);
    }

    public void requestNewChatMessages() throws NetException {
        List<String> answer = queryAnswer("" + Command.CHAT_GET_NEW_MSGS.getValue());
        this.controller.updateNewChatMessages(answer);
    }

    public void requestDownloadGameMap(String gameMapName) throws NetException {
        List<String> answer = queryAnswer("" + Command.DOWNLOAD_MAP + " " + gameMapName);
        this.controller.updateDownloadGameMap(answer);
    }

    private synchronized ArrayList<String> queryAnswer(String query) throws
            NetException {
        PrintWriter out = null;
        BufferedReader in = null;
        ArrayList<String> answer = null;
        try {
            OutputStream os = this.socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            out = new PrintWriter(osw);
            //System.out.println("Client: Sending query: '"+query+"'.");
            out.println(query);
            out.flush();

            InputStream is = this.socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            in = new BufferedReader(isr);
            String oneLine;
            answer = new ArrayList<String>();
            while ((oneLine = in.readLine()) != null) {
                if (oneLine.length() == 0) {
                    break;
                }
                answer.add(oneLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new NetException();
        }
        if (answer.size() == 0) {
            throw new NetException();
        }
        return answer;
    }

    public InetAddress getInetAddress() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getPort() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void beginUpdating() {
        // must be here or somewhere else???
        timer = new Timer();
        timer.schedule(new UpdateTimerTask(), (long) 0, (long) Constants.GAME_STEP_TIME);
    }

    private class UpdateTimerTask extends TimerTask {

        @Override
        public void run() {
            try {
                requestGameMap();//will call this.controller.updateGameMap and update it in model
            } catch (NetException ex) {
                // is it good???
                ex.printStackTrace();
                this.cancel();
            }
        }
    }
}
