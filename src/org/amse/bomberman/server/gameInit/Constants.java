/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameInit;

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

    public static final int DIRECTION_DOWN  = 0;
    public static final int DIRECTION_LEFT  = 1;
    public static final int DIRECTION_UP    = 2;
    public static final int DIRECTION_RIGHT = 3;
    
    public static final long stepTime = 200L;
}
