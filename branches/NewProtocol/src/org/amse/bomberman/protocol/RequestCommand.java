/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.protocol;

import java.util.List;

/**
 *
 * @author Kirilchuk V.E
 */
public enum RequestCommand { //TODO it is better to do interface-subclasses implementation. not enum!

    GET_GAMES(0){// "0"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException {
            executor.sendGames();
        }

    },
    CREATE_GAME(1) {// "1 gameName mapName maxPlayers playerName"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.tryCreateGame(args);
        }

    },
    JOIN_GAME(2) {//"2 gameID botName"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.tryJoinGame(args);
        }

    },
    DO_MOVE(3) {//"3 direction"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.tryDoMove(args);
        }

    },
    GET_GAME_MAP_INFO(4) {// "4"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.sendGameMapInfo();
        }

    },
    START_GAME(5) {// "5"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.tryStartGame();
        }

    },
    LEAVE(6) { // "6"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.tryLeave();
        }

    },
    PLACE_BOMB(7) { // "7"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.tryPlaceBomb();
        }

    },
    DOWNLOAD_GAME_MAP(8) {// "8 mapName"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.sendDownloadingGameMap(args);
        }

    },
    GET_GAME_STATUS(9) {// "9"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.sendGameStatus();
        }

    },
    GET_GAME_MAPS_LIST(10) {// "10"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.sendGameMapsList();
        }

    },
    ADD_BOT_TO_GAME(11) {// "11 gameID botName"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.tryAddBot(args);
        }

    },
    GET_GAME_INFO(12) {// "12"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.sendGameInfo();
        }

    },
    CHAT_ADD_MSG(13) { // "13 message"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.addMessageToChat(args);
        }

    },
    CHAT_GET_NEW_MSGS(14) {// "14"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.sendNewMessagesFromChat();
        }

    },
    REMOVE_BOT_FROM_GAME(15) {// "15"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.tryRemoveBot();
        }

    },
    GET_GAME_PLAYERS_STATS(16) {// "16"

        @Override
        public void execute(RequestExecutor executor, List<String> args) throws InvalidDataException  {
            executor.sendGamePlayersStats();
        }

    },
    SET_CLIENT_NAME(17) {// "17 name"

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
            case 0:
                return GET_GAMES;
            case 1:
                return CREATE_GAME;
            case 2:
                return JOIN_GAME;
            case 3:
                return DO_MOVE;
            case 4:
                return GET_GAME_MAP_INFO;
            case 5:
                return START_GAME;
            case 6:
                return LEAVE;
            case 7:
                return PLACE_BOMB;
            case 8:
                return DOWNLOAD_GAME_MAP;
            case 9:
                return GET_GAME_STATUS;
            case 10:
                return GET_GAME_MAPS_LIST;
            case 11:
                return ADD_BOT_TO_GAME;
            case 12:
                return GET_GAME_INFO;
            case 13:
                return CHAT_ADD_MSG;
            case 14:
                return CHAT_GET_NEW_MSGS;
            case 15:
                return REMOVE_BOT_FROM_GAME;
            case 16:
                return GET_GAME_PLAYERS_STATS;
            case 17:
                return SET_CLIENT_NAME;
            default:
                throw new IllegalArgumentException("Wrong argument "
                        + "must be between 0 and 17 inclusive");
        }
    }

    public abstract void execute(RequestExecutor executor, List<String> args) throws InvalidDataException;
}
