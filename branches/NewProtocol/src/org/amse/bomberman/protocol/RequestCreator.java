/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.protocol;

import java.util.ArrayList;
import java.util.List;

import org.amse.bomberman.client.model.impl.Model;
import org.amse.bomberman.util.Constants.Direction;

/**
 *
 * @author Kirilchuk V.E.
 */
public class RequestCreator {

    private final List<String> emptyList = new ArrayList<String>(0);

    public ProtocolMessage<Integer, String> requestGamesList() {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(RequestCommand.GET_GAMES.getValue());
        request.setData(emptyList);

        return request;
    }

    public ProtocolMessage<Integer, String> requestCreateGame(String gameName,
                                                              String mapName,
                                                              int maxPl) {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(RequestCommand.CREATE_GAME.getValue());

        List<String> data = new ArrayList<String>(4);
        data.add(gameName);
        data.add(mapName);
        data.add("" + maxPl);
        data.add(Model.getInstance().getPlayerName());

        request.setData(data);

        return request;
    }

    public ProtocolMessage<Integer, String> requestLeaveGame() {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(RequestCommand.LEAVE.getValue());
        request.setData(emptyList);

        return request;
    }

    public ProtocolMessage<Integer, String> requestJoinGame(int gameId) {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(RequestCommand.JOIN_GAME.getValue());

        List<String> data = new ArrayList<String>(2);
        data.add("" + gameId);
        data.add(Model.getInstance().getPlayerName());

        request.setData(data);

        return request;
    }

    public ProtocolMessage<Integer, String> requestDoMove(Direction dir) {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(RequestCommand.DO_MOVE.getValue());

        List<String> data = new ArrayList<String>(1);
        data.add("" + dir.getValue());

        request.setData(data);

        return request;
    }

    public ProtocolMessage<Integer, String> requestStartGame() {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(RequestCommand.START_GAME.getValue());
        request.setData(emptyList);

        return request;
    }

    public ProtocolMessage<Integer, String> requestGameMap() {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(RequestCommand.GET_GAME_MAP_INFO.getValue());
        request.setData(emptyList);

        return request;
    }

    public ProtocolMessage<Integer, String> requestPlantBomb() {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(RequestCommand.PLACE_BOMB.getValue());
        request.setData(emptyList);

        return request;
    }

    public ProtocolMessage<Integer, String> requestJoinBotIntoGame() {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(RequestCommand.ADD_BOT_TO_GAME.getValue());

        List<String> data = new ArrayList<String>(1);
        data.add("BOT_NAME");//TODO do bot name utility method!!!

        request.setData(data);

        return request;
    }

    public ProtocolMessage<Integer, String> requestRemoveBotFromGame() {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(RequestCommand.REMOVE_BOT_FROM_GAME.getValue());
        request.setData(emptyList);

        return request;
    }

    public ProtocolMessage<Integer, String> requestGameMapsList() {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(RequestCommand.GET_GAME_MAPS_LIST.getValue());
        request.setData(emptyList);

        return request;
    }

    public ProtocolMessage<Integer, String> requestIsGameStarted() {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(RequestCommand.GET_GAME_STATUS.getValue());
        request.setData(emptyList);

        return request;
    }

    public ProtocolMessage<Integer, String> requestGameInfo() {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(RequestCommand.GET_GAME_INFO.getValue());
        request.setData(emptyList);

        return request;
    }


    public ProtocolMessage<Integer, String> requestAddChatMessage(String message) {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(RequestCommand.CHAT_ADD_MSG.getValue());

        List<String> data = new ArrayList<String>(1);
        data.add(message);

        request.setData(data);

        return request;
    }
    
    public ProtocolMessage<Integer, String> requestNewChatMessages() {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(RequestCommand.CHAT_GET_NEW_MSGS.getValue());
        request.setData(emptyList);

        return request;
    }

    public ProtocolMessage<Integer, String> requestDownloadGameMap(String gameMapName) {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(RequestCommand.DOWNLOAD_GAME_MAP.getValue());

        List<String> data = new ArrayList<String>(1);
        data.add(gameMapName);

        request.setData(data);

        return request;
    }

    public ProtocolMessage<Integer, String> requestSetPlayerName(String playerName) {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(RequestCommand.SET_CLIENT_NAME.getValue());

        List<String> data = new ArrayList<String>(1);
        data.add(playerName);

        request.setData(data);

        return request;
    }
}
