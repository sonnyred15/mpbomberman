/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.protocol.requests;

import java.util.List;
import org.amse.bomberman.protocol.ProtocolConstants;

/**
 *
 * @author Kirilchuk V.E
 */
public enum RequestCommand { //TODO it is better to do interface-subclasses implementation. not enum!

    GET_GAMES(ProtocolConstants.GAMES_LIST_MESSAGE_ID){// "0"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException {
            executor.sendGames();
        }

    },
    CREATE_GAME(ProtocolConstants.CREATE_GAME_MESSAGE_ID) {// "1 gameName mapName maxPlayers"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.tryCreateGame(args);
        }

    },
    JOIN_GAME(ProtocolConstants.JOIN_GAME_MESSAGE_ID) {//"2 gameID botName"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.tryJoinGame(args);
        }

    },
    DO_MOVE(ProtocolConstants.DO_MOVE_MESSAGE_ID) {//"3 direction"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.tryDoMove(args);
        }

    },
    GET_GAME_MAP_INFO(ProtocolConstants.GAME_MAP_INFO_MESSAGE_ID) {// "4"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.sendGameMapInfo();
        }

    },
    START_GAME(ProtocolConstants.START_GAME_MESSAGE_ID) {// "5"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.tryStartGame();
        }

    },
    LEAVE(ProtocolConstants.LEAVE_MESSAGE_ID) { // "6"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.tryLeave();
        }

    },
    PLACE_BOMB(ProtocolConstants.PLACE_BOMB_MESSAGE_ID) { // "7"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.tryPlaceBomb();
        }

    },
    DOWNLOAD_GAME_MAP(ProtocolConstants.DOWNLOAD_GAME_MAP_MESSAGE_ID) {// "8 mapName"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.sendDownloadingGameMap(args);
        }

    },
    GET_GAME_STATUS(ProtocolConstants.GAME_STATUS_MESSAGE_ID) {// "9"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.sendGameStatus();
        }

    },
    GET_GAME_MAPS_LIST(ProtocolConstants.GAME_MAPS_LIST_MESSAGE_ID) {// "10"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.sendGameMapsList();
        }

    },
    ADD_BOT_TO_GAME(ProtocolConstants.BOT_ADD_MESSAGE_ID) {// "11 gameID botName"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.tryAddBot(args);
        }

    },
    GET_GAME_INFO(ProtocolConstants.GAME_INFO_MESSAGE_ID) {// "12"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.sendGameInfo();
        }

    },
    CHAT_ADD_MSG(ProtocolConstants.CHAT_ADD_MESSAGE_ID) { // "13 message"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.addMessageToChat(args);
        }

    },
    CHAT_GET_NEW_MSGS(ProtocolConstants.CHAT_GET_MESSAGE_ID) {// "14"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.sendNewMessagesFromChat();
        }

    },
    KICK_PLAYER_FROM_GAME(ProtocolConstants.KICK_PLAYER_MESSAGE_ID) {// "15"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.tryKickPlayer(args);
        }

    },
    GET_GAME_PLAYERS_STATS(ProtocolConstants.PLAYERS_STATS_MESSAGE_ID) {// "16"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.sendGamePlayersStats();
        }

    },
    SET_CLIENT_NAME(ProtocolConstants.SET_NAME_MESSAGE_ID) {// "17 name"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException {
            executor.setClientNickName(args);
        }

    };
    //
    private final int value;

    private RequestCommand(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static RequestCommand valueOf(int command) {
        switch (command) {
            case ProtocolConstants.GAMES_LIST_MESSAGE_ID:
                return GET_GAMES;
            case ProtocolConstants.CREATE_GAME_MESSAGE_ID:
                return CREATE_GAME;
            case ProtocolConstants.JOIN_GAME_MESSAGE_ID:
                return JOIN_GAME;
            case ProtocolConstants.DO_MOVE_MESSAGE_ID:
                return DO_MOVE;
            case ProtocolConstants.GAME_MAP_INFO_MESSAGE_ID:
                return GET_GAME_MAP_INFO;
            case ProtocolConstants.START_GAME_MESSAGE_ID:
                return START_GAME;
            case ProtocolConstants.LEAVE_MESSAGE_ID:
                return LEAVE;
            case ProtocolConstants.PLACE_BOMB_MESSAGE_ID:
                return PLACE_BOMB;
            case ProtocolConstants.DOWNLOAD_GAME_MAP_MESSAGE_ID:
                return DOWNLOAD_GAME_MAP;
            case ProtocolConstants.GAME_STATUS_MESSAGE_ID:
                return GET_GAME_STATUS;
            case ProtocolConstants.GAME_MAPS_LIST_MESSAGE_ID:
                return GET_GAME_MAPS_LIST;
            case ProtocolConstants.BOT_ADD_MESSAGE_ID:
                return ADD_BOT_TO_GAME;
            case ProtocolConstants.GAME_INFO_MESSAGE_ID:
                return GET_GAME_INFO;
            case ProtocolConstants.CHAT_ADD_MESSAGE_ID:
                return CHAT_ADD_MSG;
            case ProtocolConstants.CHAT_GET_MESSAGE_ID:
                return CHAT_GET_NEW_MSGS;
            case ProtocolConstants.KICK_PLAYER_MESSAGE_ID:
                return KICK_PLAYER_FROM_GAME;
            case ProtocolConstants.PLAYERS_STATS_MESSAGE_ID:
                return GET_GAME_PLAYERS_STATS;
            case ProtocolConstants.SET_NAME_MESSAGE_ID:
                return SET_CLIENT_NAME;
            default:
                throw new IllegalArgumentException("Wrong argument "
                        + "must be between 0 and 17 inclusive");
        }
    }

    public abstract void execute(RequestExecutor executor, List<String> args) throws InvalidDataException;
}
