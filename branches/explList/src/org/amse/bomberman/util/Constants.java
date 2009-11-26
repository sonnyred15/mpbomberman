/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.util;

/**
 *
 * @author chibis
 */
public final class Constants {
    
    private Constants() {
    }

    public static final int MAP_EMPTY  = 0;
    public static final int MAP_BOMB  = -16;
    public static final int MAP_DETONATED_BOMB = -17;
    public static final int MAP_EXPLOSION_LINE = -18;
    
    public static final int MAX_PLAYERS  = 15;//1..15
    
    public static final long GAME_STEP_TIME = 200L;
    public static final long BOMB_TIMER_VALUE = GAME_STEP_TIME*10;
    public static final long BOMB_DETONATION_TIME = GAME_STEP_TIME*3;

    public static final int DEFAULT_PORT = 10500;
    public static final int DEFAULT_ACCEPT_TIMEOUT = 60000;
    
    public static final String DEFAULT_FILE_LOG_NAME = "fileLog.log";

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
