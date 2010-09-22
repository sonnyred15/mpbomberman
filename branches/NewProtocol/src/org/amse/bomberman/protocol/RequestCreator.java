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
        request.setMessageId(ProtocolConstants.GAMES_LIST_MESSAGE_ID);
        request.setData(emptyList);

        return request;
    }

    public ProtocolMessage<Integer, String> requestCreateGame(String gameName,
                                                              String mapName,
                                                              int maxPl) {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(ProtocolConstants.CREATE_GAME_MESSAGE_ID);

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
        request.setMessageId(ProtocolConstants.LEAVE_MESSAGE_ID);
        request.setData(emptyList);

        return request;
    }

    public ProtocolMessage<Integer, String> requestJoinGame(int gameId) {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(ProtocolConstants.JOIN_GAME_MESSAGE_ID);

        List<String> data = new ArrayList<String>(2);
        data.add("" + gameId);
        data.add(Model.getInstance().getPlayerName());

        request.setData(data);

        return request;
    }

    public ProtocolMessage<Integer, String> requestDoMove(Direction dir) {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(ProtocolConstants.DO_MOVE_MESSAGE_ID);

        List<String> data = new ArrayList<String>(1);
        data.add("" + dir.getValue());

        request.setData(data);

        return request;
    }

    public ProtocolMessage<Integer, String> requestStartGame() {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(ProtocolConstants.START_GAME_MESSAGE_ID);
        request.setData(emptyList);

        return request;
    }

    public ProtocolMessage<Integer, String> requestGameMap() {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(ProtocolConstants.GAME_MAP_INFO_MESSAGE_ID);
        request.setData(emptyList);

        return request;
    }

    public ProtocolMessage<Integer, String> requestPlaceBomb() {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(ProtocolConstants.PLACE_BOMB_MESSAGE_ID);
        request.setData(emptyList);

        return request;
    }

    public ProtocolMessage<Integer, String> requestJoinBotIntoGame() {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(ProtocolConstants.ADD_BOT_MESSAGE_ID);

        List<String> data = new ArrayList<String>(1);
        data.add("BOT_NAME");//TODO do bot name utility method!!!

        request.setData(data);

        return request;
    }

    public ProtocolMessage<Integer, String> requestRemoveBotFromGame() {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(ProtocolConstants.REMOVE_BOT_MESSAGE_ID);
        request.setData(emptyList);

        return request;
    }

    public ProtocolMessage<Integer, String> requestGameMapsList() {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(ProtocolConstants.GAME_MAPS_LIST_MESSAGE_ID);
        request.setData(emptyList);

        return request;
    }

    public ProtocolMessage<Integer, String> requestIsGameStarted() {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(ProtocolConstants.GAME_STATUS_MESSAGE_ID);
        request.setData(emptyList);

        return request;
    }

    public ProtocolMessage<Integer, String> requestGameInfo() {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(ProtocolConstants.GAME_INFO_MESSAGE_ID);
        request.setData(emptyList);

        return request;
    }


    public ProtocolMessage<Integer, String> requestAddChatMessage(String message) {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(ProtocolConstants.CHAT_ADD_MESSAGE_ID);

        List<String> data = new ArrayList<String>(1);
        data.add(message);

        request.setData(data);

        return request;
    }
    
    public ProtocolMessage<Integer, String> requestNewChatMessages() {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(ProtocolConstants.CHAT_GET_MESSAGE_ID);
        request.setData(emptyList);

        return request;
    }

    public ProtocolMessage<Integer, String> requestDownloadGameMap(String gameMapName) {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(ProtocolConstants.DOWNLOAD_GAME_MAP_MESSAGE_ID);

        List<String> data = new ArrayList<String>(1);
        data.add(gameMapName);

        request.setData(data);

        return request;
    }

    public ProtocolMessage<Integer, String> requestSetClientName(String playerName) {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(ProtocolConstants.CLIENT_NAME_MESSAGE_ID);

        List<String> data = new ArrayList<String>(1);
        data.add(playerName);

        request.setData(data);

        return request;
    }

    public ProtocolMessage<Integer, String> requestServerDisconnect() {
        ProtocolMessage<Integer, String> request = new ProtocolMessage<Integer, String>();
        request.setMessageId(ProtocolConstants.DISCONNECT_MESSAGE_ID);

        request.setData(emptyList);

        return request;
    }
}
