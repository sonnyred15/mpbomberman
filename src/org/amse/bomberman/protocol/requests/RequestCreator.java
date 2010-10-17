package org.amse.bomberman.protocol.requests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.amse.bomberman.client.models.gamemodel.impl.GameMapModel;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.util.Direction;
import org.amse.bomberman.util.Creator;

/**
 *
 * @author Kirilchuk V.E.
 */
public class RequestCreator {

    private final List<String> emptyList
            = Collections.unmodifiableList(new ArrayList<String>(0));

    public ProtocolMessage<Integer, String> requestGamesList() {
        return requestWithEmptyData(ProtocolConstants.GAMES_LIST_MESSAGE_ID);
    }

    public ProtocolMessage<Integer, String>
            requestCreateGame(String gameName, String gameMapName, int maxPl) {

        List<String> data = new ArrayList<String>(4);
        data.add(gameName);
        data.add(gameMapName);
        data.add(String.valueOf(maxPl));

        return request(ProtocolConstants.CREATE_GAME_MESSAGE_ID, data);
    }

    public ProtocolMessage<Integer, String> requestLeaveGame() {
        return requestWithEmptyData(ProtocolConstants.LEAVE_MESSAGE_ID);
    }

    public ProtocolMessage<Integer, String> requestJoinGame(int gameId) {
        List<String> data = new ArrayList<String>(1);
        data.add(String.valueOf(gameId));

        return request(ProtocolConstants.JOIN_GAME_MESSAGE_ID, data);
    }

    public ProtocolMessage<Integer, String> requestDoMove(Direction dir) {
        List<String> data = new ArrayList<String>(1);
        data.add("" + dir.getValue());

        return request(ProtocolConstants.DO_MOVE_MESSAGE_ID, data);
    }

    public ProtocolMessage<Integer, String> requestStartGame() {
        return requestWithEmptyData(ProtocolConstants.START_GAME_MESSAGE_ID);
    }

    public ProtocolMessage<Integer, String> requestGameMap() {
        return requestWithEmptyData(ProtocolConstants.GAME_MAP_INFO_MESSAGE_ID);
    }

    public ProtocolMessage<Integer, String> requestPlaceBomb() {
        return requestWithEmptyData(ProtocolConstants.PLACE_BOMB_MESSAGE_ID);
    }

    public ProtocolMessage<Integer, String> requestJoinBotIntoGame() {
        List<String> data = new ArrayList<String>(1);
        data.add(Creator.createBotName());

        return request(ProtocolConstants.BOT_ADD_MESSAGE_ID, data);
    }

    public ProtocolMessage<Integer, String> requestRemoveBotFromGame() {
        return requestWithEmptyData(ProtocolConstants.KICK_PLAYER_MESSAGE_ID);
    }

    public ProtocolMessage<Integer, String> requestGameMapsList() {
        return requestWithEmptyData(ProtocolConstants.GAME_MAPS_LIST_MESSAGE_ID);
    }

    public ProtocolMessage<Integer, String> requestIsGameStarted() {
        return requestWithEmptyData(ProtocolConstants.GAME_STATUS_MESSAGE_ID);
    }

    public ProtocolMessage<Integer, String> requestGameInfo() {
        return requestWithEmptyData(ProtocolConstants.GAME_INFO_MESSAGE_ID);
    }

    public ProtocolMessage<Integer, String>
            requestAddChatMessage(String message) {

        List<String> data = new ArrayList<String>(1);
        data.add(message);

        return request(ProtocolConstants.CHAT_ADD_MESSAGE_ID, data);
    }

    public ProtocolMessage<Integer, String> requestGetNewChatMessages() {
        return requestWithEmptyData(ProtocolConstants.CHAT_GET_MESSAGE_ID);
    }

    public ProtocolMessage<Integer, String>
            requestDownloadGameMap(String gameMapName) {

        List<String> data = new ArrayList<String>(1);
        data.add(gameMapName);

        return request(ProtocolConstants.DOWNLOAD_GAME_MAP_MESSAGE_ID, data);
    }

    public ProtocolMessage<Integer, String>
            requestSetClientName(String playerName) {
        
        List<String> data = new ArrayList<String>(1);
        data.add(playerName);

        return request(ProtocolConstants.SET_NAME_MESSAGE_ID, data);
    }

    public ProtocolMessage<Integer, String> requestServerDisconnect() {
        return requestWithEmptyData(ProtocolConstants.DISCONNECT_MESSAGE_ID);
    }

    private ProtocolMessage<Integer, String>
            requestWithEmptyData(int messageId) {

        ProtocolMessage<Integer, String> request
                = new ProtocolMessage<Integer, String>();

        request.setMessageId(messageId);
        request.setData(emptyList);

        return request;
    }

    private ProtocolMessage<Integer, String>
            request(int messageId, List<String> data) {

        ProtocolMessage<Integer, String> request
                = new ProtocolMessage<Integer, String>();

        request.setMessageId(messageId);
        request.setData(data);

        return request;
    }
}
