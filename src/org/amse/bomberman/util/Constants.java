package org.amse.bomberman.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kirilchuk V.E
 * @author Mikhail Korovkin
 */
public final class Constants {
    
    private Constants() {
    }

    public static final int MAP_EMPTY  = 0;
    public static final int MAP_PROOF_WALL = -8;
    public static final int MAP_BOMB  = -16;
    public static final int MAP_DETONATED_BOMB = -17;
    public static final int MAP_EXPLOSION_LINE = -18;
    public static final int MAP_BONUS_LIFE = -9;
    public static final int MAP_BONUS_BOMB_COUNT = -10;
    public static final int MAP_BONUS_BOMB_RADIUS = -11;
    
    public static final int MAX_PLAYERS  = 15;//1..15
    
    public static final long GAME_STEP_TIME = 45L;
    public static final long BOMB_TIMER_VALUE = 200L*10;
    public static final long BOMB_DETONATION_TIME = 200L*3;

    public static final int PLAYER_DEFAULT_MAX_BOMBS = 3;
    public static final int PLAYER_DEFAULT_BOMB_RADIUS = 2;
    public static final int PLAYER_IMMORTALITY_TIME = 1000;

    public static final int DEFAULT_PORT = 10500;
    public static final int DEFAULT_ACCEPT_TIMEOUT = 60000;
    public static final int DEFAULT_CLIENT_TIMEOUT = 300000;
    
    public static final String DEFAULT_FILE_LOG_NAME = "server.log";
    public static File RESOURSES_GAMEMAPS_DIRECTORY;
    static{
        String path = Constants.class.getProtectionDomain()
                        .getCodeSource().getLocation().getPath();
        File f = new File(path);
        f = f.getParentFile();
        f = new File(f.getPath() + "/resources/maps");
        RESOURSES_GAMEMAPS_DIRECTORY = f;
    }


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
        GET_GAME_MAP_INFO(4),
        START_GAME(5),
        LEAVE_GAME(6),
        PLACE_BOMB(7),
        DOWNLOAD_GAME_MAP(8),
        GET_GAME_STATUS(9),
        GET_GAME_MAPS_LIST(10),
        ADD_BOT_TO_GAME(11),
        GET_MY_GAME_INFO(12),
        CHAT_ADD_MSG(13),
        CHAT_GET_NEW_MSGS(14),
        REMOVE_BOT_FROM_GAME(15),
        GET_MY_GAME_PLAYERS_STATS(16),
        SET_PLAYER_NAME(17);


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
                    return GET_GAME_MAP_INFO;
                case 5:
                    return START_GAME;
                case 6:
                    return LEAVE_GAME;
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
                    return GET_MY_GAME_INFO;
                case 13:
                    return CHAT_ADD_MSG;
                case 14:
                    return CHAT_GET_NEW_MSGS;
                case 15:
                    return REMOVE_BOT_FROM_GAME;
                case 16:
                    return GET_MY_GAME_PLAYERS_STATS;
                case 17:
                    return SET_PLAYER_NAME;
                default:
                    throw new IllegalArgumentException("Wrong argument " +
                            "must be between 0 and 15 inclusive");
            }
        }
    }
}
