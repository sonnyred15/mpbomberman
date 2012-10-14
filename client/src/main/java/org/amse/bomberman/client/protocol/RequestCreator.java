package org.amse.bomberman.client.protocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.util.Direction;

/**
 *
 * @author Kirilchuk V.E.
 */
public class RequestCreator {

    private final List<String> emptyList
            = Collections.unmodifiableList(new ArrayList<String>(0));

    public ProtocolMessage requestGamesList() {
        return requestWithEmptyData(ProtocolConstants.GAMES_LIST_MESSAGE_ID);
    }

    public ProtocolMessage
            requestCreateGame(String gameName, String gameMapName, int maxPl) {

        List<String> data = new ArrayList<String>(4);
        data.add(gameName);
        data.add(gameMapName);
        data.add(String.valueOf(maxPl));

        return request(ProtocolConstants.CREATE_GAME_MESSAGE_ID, data);
    }

    public ProtocolMessage requestLeaveGame() {
        return requestWithEmptyData(ProtocolConstants.LEAVE_MESSAGE_ID);
    }

    public ProtocolMessage requestJoinGame(int gameId) {
        List<String> data = new ArrayList<String>(1);
        data.add(String.valueOf(gameId));

        return request(ProtocolConstants.JOIN_GAME_MESSAGE_ID, data);
    }

    public ProtocolMessage requestDoMove(Direction dir) {
        List<String> data = new ArrayList<String>(1);
        data.add("" + dir.getValue());

        return request(ProtocolConstants.DO_MOVE_MESSAGE_ID, data);
    }

    public ProtocolMessage requestStartGame() {
        return requestWithEmptyData(ProtocolConstants.START_GAME_MESSAGE_ID);
    }

    public ProtocolMessage requestGameMap() {
        return requestWithEmptyData(ProtocolConstants.GAME_MAP_INFO_MESSAGE_ID);
    }

    public ProtocolMessage requestPlaceBomb() {
        return requestWithEmptyData(ProtocolConstants.PLACE_BOMB_MESSAGE_ID);
    }

    public ProtocolMessage requestJoinBotIntoGame() {
        return requestWithEmptyData(ProtocolConstants.BOT_ADD_MESSAGE_ID);
    }

    public ProtocolMessage requestKickFromGame(int id) {
        List<String> data = new ArrayList<String>(1);
        data.add(String.valueOf(id));
        
        return request(ProtocolConstants.KICK_PLAYER_MESSAGE_ID, data);
    }

    public ProtocolMessage requestGameMapsList() {
        return requestWithEmptyData(ProtocolConstants.GAME_MAPS_LIST_MESSAGE_ID);
    }

    public ProtocolMessage requestIsGameStarted() {
        return requestWithEmptyData(ProtocolConstants.GAME_STATUS_MESSAGE_ID);
    }

    public ProtocolMessage requestGameInfo() {
        return requestWithEmptyData(ProtocolConstants.GAME_INFO_MESSAGE_ID);
    }

    public ProtocolMessage
            requestAddChatMessage(String message) {

        List<String> data = new ArrayList<String>(1);
        data.add(message);

        return request(ProtocolConstants.CHAT_ADD_MESSAGE_ID, data);
    }

    public ProtocolMessage requestGetNewChatMessages() {
        return requestWithEmptyData(ProtocolConstants.CHAT_GET_MESSAGE_ID);
    }

    @Deprecated
    public ProtocolMessage
            requestDownloadGameMap(String gameMapName) {

        List<String> data = new ArrayList<String>(1);
        data.add(gameMapName);

        return request(ProtocolConstants.DOWNLOAD_GAME_MAP_MESSAGE_ID, data);
    }

    public ProtocolMessage
            requestSetClientName(String playerName) {
        
        List<String> data = new ArrayList<String>(1);
        data.add(playerName);

        return request(ProtocolConstants.SET_NAME_MESSAGE_ID, data);
    }

    public ProtocolMessage requestServerDisconnect() {
        return requestWithEmptyData(ProtocolConstants.DISCONNECT_MESSAGE_ID);
    }

    private ProtocolMessage
            requestWithEmptyData(int messageId) {

        ProtocolMessage request
                = new ProtocolMessage();

        request.setMessageId(messageId);
        request.setData(emptyList);

        return request;
    }

    private ProtocolMessage
            request(int messageId, List<String> data) {

        ProtocolMessage request
                = new ProtocolMessage();

        request.setMessageId(messageId);
        request.setData(data);

        return request;
    }
}
