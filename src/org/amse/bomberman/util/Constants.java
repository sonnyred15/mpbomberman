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

    public static final int PLAYER_DEFAULT_LIVES = 3;
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
}
