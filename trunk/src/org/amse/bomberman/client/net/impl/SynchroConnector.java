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
import org.amse.bomberman.client.net.IConnector2;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.view.control.Controller;
import org.amse.bomberman.util.Constants.Command;
import org.amse.bomberman.util.Constants.Direction;
import org.amse.bomberman.util.ProtocolConstants;

/**
 *
 * @author Kirilchuk V.E.
 */
public class SynchroConnector implements IConnector2 {

    private Socket socket;
    private final Controller controller;

    public SynchroConnector(Controller controller) {
        this.controller = controller;
    }

    public void сonnect(InetAddress address, int port) throws
            UnknownHostException,
            IOException {

        this.socket = new Socket(address, port);
    }

    public void requestLeaveGame() throws NetException {
        List<String> answer = queryAnswer("" + Command.LEAVE_GAME.getValue());
        System.out.println(answer.get(0));
        answer.add(0, ProtocolConstants.CAPTION_LEAVE_GAME_INFO);
        this.controller.receivedRequestResult(answer);
    }

    public void requestGamesList() throws NetException {
        List<String> games = queryAnswer("" + Command.GET_GAMES.getValue());
        for (String string : games) {
            System.out.println(string);
        }
        games.add(0, ProtocolConstants.CAPTION_GAMES_LIST);
        this.controller.receivedRequestResult(games);
    }

    public void requestCreateGame(String gameName, String mapName, int maxPl)
            throws NetException {

        List<String> answer = queryAnswer("" + Command.CREATE_GAME.getValue() +
                " " + gameName + " " + mapName + " " + maxPl);

        answer.add(0, ProtocolConstants.CAPTION_CREATE_GAME);
        this.controller.receivedRequestResult(answer);
    }

    public void requestJoinGame(int gameID) throws NetException {
        List<String> list = queryAnswer("2 " + gameID);
        System.out.println(list.get(0));
        list.add(0, ProtocolConstants.CAPTION_JOIN_GAME);
        this.controller.receivedRequestResult(list);
    }

    public void requestDoMove(Direction dir) throws NetException {
        List<String> list = queryAnswer("3 " + dir.getValue());
        list.add(0, ProtocolConstants.CAPTION_DO_MOVE);
        this.controller.receivedRequestResult(list);
    }

    public void requestStartGame() throws NetException {
        List<String> list = queryAnswer("" + Command.START_GAME.getValue());
        System.out.println(list);
        list.add(0, ProtocolConstants.CAPTION_START_GAME_INFO);
        this.controller.receivedRequestResult(list);
    }

    public void requestGameMap() throws NetException {
        List<String> gameMap = queryAnswer("" + Command.GET_GAME_MAP_INFO.getValue());
        gameMap.add(0, ProtocolConstants.CAPTION_GAME_MAP_INFO);
        this.controller.receivedRequestResult(gameMap);
    }

    public void requestPlantBomb() throws NetException {
        List<String> list = queryAnswer("" + Command.PLACE_BOMB.getValue());
        System.out.println(list.get(0));
        list.add(0, ProtocolConstants.CAPTION_PLACE_BOMB_INFO);
        this.controller.receivedRequestResult(list);
    }

    public void requestJoinBotIntoGame(int gameID) throws NetException {
        // bot name???!!!
        List<String> list = queryAnswer("" +
                Command.ADD_BOT_TO_GAME.getValue() + " " + gameID + " BOT");
        System.out.println(list.get(0));
        list.add(0, ProtocolConstants.CAPTION_JOIN_BOT_INFO);
        this.controller.receivedRequestResult(list);
    }

    public void requestGameMapsList() throws NetException {
        List<String> gameMaps = queryAnswer("" + Command.GET_GAME_MAPS_LIST.getValue());
        gameMaps.add(0, ProtocolConstants.CAPTION_GAME_MAPS_LIST);
        this.controller.receivedRequestResult(gameMaps);
    }

    public void requestIsGameStarted() throws NetException {
        List<String> list = queryAnswer("" + Command.GET_GAME_STATUS.getValue());
        list.add(0, ProtocolConstants.CAPTION_GAME_STATUS_INFO);
        this.controller.receivedRequestResult(list);
    }

    public void requestGameInfo() throws NetException {
        List<String> list = queryAnswer("" + Command.GET_MY_GAME_INFO.getValue());
        list.add(0, ProtocolConstants.CAPTION_GAME_INFO);
        this.controller.receivedRequestResult(list);
    }

    public void sendChatMessage(String message) throws NetException {
        List<String> answer = queryAnswer("" + Command.CHAT_ADD_MSG.getValue() +
                " " + message);
        answer.add(0, ProtocolConstants.CAPTION_SEND_CHAT_MSG_INFO);
        this.controller.receivedRequestResult(answer);
    }

    public void requestNewChatMessages() throws NetException {
        List<String> answer = queryAnswer("" + Command.CHAT_GET_NEW_MSGS.getValue());
        answer.add(0, ProtocolConstants.CAPTION_GET_CHAT_MSGS);
        this.controller.receivedRequestResult(answer);
    }

    public void requestDownloadGameMap(String gameMapName) throws NetException {
        List<String> answer = queryAnswer("" + Command.DOWNLOAD_GAME_MAP + " " + gameMapName);
        answer.add(0, ProtocolConstants.CAPTION_DOWNLOAD_GAME_MAP);
        this.controller.receivedRequestResult(answer);
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
}
