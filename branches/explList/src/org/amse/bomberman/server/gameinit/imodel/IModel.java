/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit.imodel;

import org.amse.bomberman.server.gameinit.Player;

/**
 *
 * @author chibis
 */
public interface IModel {

    void placeBomb(Player player);

    boolean doMove(Player player, int direction);

    void removePlayer(int playerID);

    int[][] getMapArray();

    void printToConsole();

    void changeMapForCurMaxPlayers(int curMaxPlayers);

    int xCoordOf(int playerID);

    int yCoordOf(int playerID);
}
