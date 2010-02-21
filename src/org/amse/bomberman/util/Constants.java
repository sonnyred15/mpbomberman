package org.amse.bomberman.util;

/**
 *
 * @author Kirilchuk V.E
 * @author Michail Korovkin
 */
public final class Constants {
    
    private Constants() {
    }

    public static final int MAP_EMPTY  = 0;
    public static final int MAP_PROOF_WALL = -8;
    public static final int MAP_BOMB  = -16;
    public static final int MAP_DETONATED_BOMB = -17;
    public static final int MAP_EXPLOSION_LINE = -18;
    
    public static final int MAX_PLAYERS  = 15;//1..15
    
    public static final long GAME_STEP_TIME = 45L;
    public static final long BOMB_TIMER_VALUE = 200L*10;
    public static final long BOMB_DETONATION_TIME = 200L*3;

    public static final int PLAYER_MAX_BOMBS = 3;

    public static final int DEFAULT_PORT = 10500;
    public static final int DEFAULT_ACCEPT_TIMEOUT = 60000;
    public static final int DEFAULT_CLIENT_TIMEOUT = 60000;
    
    public static final String DEFAULT_FILE_LOG_NAME = "server.log";

    public static enum Direction {

        DOWN(0),
        LEFT(1),
        UP(2),
        RIGHT(3);

        private final int value;

        private Direction(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static Direction fromInt(int direction) throws IllegalArgumentException{
            switch (direction) {
                case 0:
                    return DOWN;
                case 1:
                    return LEFT;
                case 2:
                    return UP;
                case 3:
                    return RIGHT;
                default:
                    throw new IllegalArgumentException("Wrong argument " +
                            "must be between 0 and 3 inclusive");
            }
        }
    }

    public static enum Command {

        GET_GAMES(0),
        CREATE_GAME(1),
        JOIN_GAME(2),
        DO_MOVE(3),
        GET_MAP_ARRAY(4),
        START_GAME(5),
        LEAVE_GAME(6),
        PLACE_BOMB(7),
        DOWNLOAD_MAP(8),
        GET_GAME_STATUS(9),
        GET_MAPS_LIST(10),
        ADD_BOT_TO_GAME(11),
        GET_MY_GAME_INFO(12),
        CHAT_ADD_MSG(13),
        CHAT_GET_NEW_MSGS(14);

        private final int value;

        private Command(int value){
            this.value = value;
        }

        public int getValue(){
            return this.value;
        }

        public static Command fromInt(int command) {
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
                    return GET_MAP_ARRAY;
                case 5:
                    return START_GAME;
                case 6:
                    return LEAVE_GAME;
                case 7:
                    return PLACE_BOMB;
                case 8:
                    return DOWNLOAD_MAP;
                case 9:
                    return GET_GAME_STATUS;
                case 10:
                    return GET_MAPS_LIST;
                case 11:
                    return ADD_BOT_TO_GAME;
                case 12:
                    return GET_MY_GAME_INFO;
                case 13:
                    return CHAT_ADD_MSG;
                case 14:
                    return CHAT_GET_NEW_MSGS;
                default:
                    throw new IllegalArgumentException("Wrong argument " +
                            "must be between 0 and 14 inclusive");
            }
        }
    }
}
