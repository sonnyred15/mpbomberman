package org.amse.bomberman.protocol.responses;

//~--- non-JDK imports --------------------------------------------------------
import org.amse.bomberman.server.gameservice.Game;
import org.amse.bomberman.util.Creator;

//~--- JDK imports ------------------------------------------------------------

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.server.gameservice.GamePlayer;

/**
 * Invoker of commands
 * @author Kirilchuk V.E.
 */
public class ResponseCreator {

    private final Converter<String> converter;

    /**
     * Constructs protocol answerer.
     */
    public ResponseCreator() {
        this.converter = new ConverterToString();
    }

    public ResponseCreator(Converter<String> converter) {
        this.converter = converter;
    }

    /**
     * Method that returns "ok" message to send for client
     * with specified protocol caption and
     * with specified additional message.
     *
     * @param caption protocol caption of message.
     * @param msg message.
     *
     * @return
     */
    public ProtocolMessage<Integer, String> ok(int messageId, String msg) {
        return singleMessage(messageId, msg);
    }

    /**
     * Method that returns "not ok" message to send for client
     * with specified protocol caption and
     * with specified additional message..
     *
     * @param caption protocol caption of message.
     * @param msg message
     *
     * @return
     */
    public ProtocolMessage<Integer, String> notOk(int messageId, String msg) {
        return singleMessage(messageId, msg);
    }

    public ProtocolMessage<Integer, String> illegalState(String action,
                                                         String stateName) {
        String message = "Can`t " + action + " in '" + stateName + "' state.";

        return singleMessage(ProtocolConstants.INVALID_REQUEST_MESSAGE_ID, message);
    }

    public ProtocolMessage<Integer, String> unstartedGamesList(List<Game> games) {
        int messageId = ProtocolConstants.GAMES_LIST_MESSAGE_ID;

        List<String> data = converter.convertUnstartedGames(games);
        if (data.isEmpty()) {
            return singleMessage(messageId, "No unstarted games finded.");
        } else {
            return message(messageId, data);
        }
    }

    public ProtocolMessage<Integer, String> chatMessage(String chatMessage) {
        return singleMessage(ProtocolConstants.CHAT_GET_MESSAGE_ID, chatMessage);
    }

    /**
     * Method that returns message to send for client that contains
     * gameMap info with exlosions and client player status.
     *
     * @param controller //TODO fill this javaDoc
     *
     * @return message to send for client that contains
     * gameMap info with exlosions and client player status.
     */
    public ProtocolMessage<Integer, String> gameMapInfo(List<String> data) {
        return message(ProtocolConstants.GAME_MAP_INFO_MESSAGE_ID, data);
    }

    /**
     * Method that returns message for client, that contains gameMap.
     * If errors occurs during creation of gameMap then message will
     * contain remark about error.
     *
     * @param gameMapName to send to client in message.
     *
     * @return message for client, that contains game map.
     * If errors occurs during creation of gameMap then message will
     * contain remark about error.
     */
    public ProtocolMessage<Integer, String> downloadGameMap(String gameMapName) {
        List<String> data = new ArrayList<String>();

        int[][] ret = null;
        try {
            ret = Creator.createMapAndGetField(gameMapName);

            System.out.println("Session: client downloading gameMap." + " GameMap=" + gameMapName);
            data.addAll(converter.convertField(ret));

        } catch (FileNotFoundException ex) {
            System.out.println("Session: sendMap warning. "
                    + "Client tryed to download map, canceled. "
                    + "Map wasn`t founded on server." + " Map=" + gameMapName + " "
                    + ex.getMessage());
            data.add("No such map on server.");
        } catch (IOException ex) {
            System.out.println("Session: sendMap error. "
                    + "Client tryed to download map, canceled. "
                    + "Error on server side while loading map." + " Map=" + gameMapName
                    + " " + ex.getMessage());
            data.add("Error on server side, while loading map.");
        }

        return message(ProtocolConstants.DOWNLOAD_GAME_MAP_MESSAGE_ID, data);
    }

    /**
     * Method that returns message to send to client
     * that contains game status.
     *
     * @param game status will be getted from this game.
     *
     * @return message to send to client
     * that contains game status.
     */
    public ProtocolMessage<Integer, String> gameStatus(Game game) {
        List<String> data = new ArrayList<String>();
        data.add(converter.convertGameStartStatus(game));

        return message(ProtocolConstants.GAME_STATUS_MESSAGE_ID , data);
    }

    /**
     * Method that returns message to send to client
     * that contains availiable gameMaps.
     *
     * @return message that contains availiable
     * gameMaps.
     */
    public ProtocolMessage<Integer, String> gameMapsList() {
        List<String> data = converter.convertGameMapsList();

        if (data.isEmpty()) {
            System.out.println("Session: sendMapsList error. No maps founded on server.");
            data.add("No maps on server was founded.");
        } else {
            System.out.println("Session: sended maps list to client. Maps count="
                    + (data.size() - 1));
        }

        return message(ProtocolConstants.GAME_MAPS_LIST_MESSAGE_ID, data);
    }

    /**
     * Method that returns message to send to client
     * that contains game info.
     *
     * @param controller //TODO fill this javaDoc
     *
     * @return message to send to client
     * that contains game info.
     */
    public ProtocolMessage<Integer, String> gameInfo(Game game, GamePlayer player) {
        List<String> data = new ArrayList<String>();
        data.addAll(converter.convertGameInfo(game, player));

        return message(ProtocolConstants.GAME_INFO_MESSAGE_ID, data);
    }

    /**
     * Method that returns message to send to client that contains
     * client game`s players stats.
     *
     * @param game the game of client to take players stats from.
     *
     * @return message to send to client that contains
     * client game`s players stats.
     */
    public ProtocolMessage<Integer, String> playersStats(Game game) {
        List<String> data = new ArrayList<String>();

        data.addAll(converter.convertPlayersStats(game.getPlayersStats()));

        return message(ProtocolConstants.PLAYERS_STATS_MESSAGE_ID, data);
    }

    public ProtocolMessage<Integer, String> gameEnd(Game game) {
        List<String> data = new ArrayList<String>();

        data.addAll(converter.convertPlayersStats(game.getPlayersStats()));

        return message(ProtocolConstants.END_RESULTS_MESSAGE_ID, data);
    }

    public Converter<String> getConverter() {
        return this.converter;
    }

    private ProtocolMessage<Integer, String> message(int messageId, List<String> data) {
        ProtocolMessage<Integer, String> message = new ProtocolMessage<Integer, String>();
        message.setMessageId(messageId);

        message.setData(data);

        return message;
    }

    private ProtocolMessage<Integer, String> singleMessage(int messageId, String msg) {
        List<String> data = new ArrayList<String>(1);
        data.add(msg);

        return message(messageId, data);
    }
}
