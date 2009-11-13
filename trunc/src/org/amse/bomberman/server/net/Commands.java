/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net;

/**
 *
 * @author chibis
 */
public class Commands {

    public static final int GET_GAMES = 0;
    public static final int CREATE_GAME = 1;
    public static final int JOIN_GAME = 2;
    public static final int DO_MOVE = 3;
    public static final int GET_MAP_ARRAY = 4;
    public static final int START_GAME = 5;
    public static final int LEAVE_GAME = 6;

    private Commands() {
    }
}
