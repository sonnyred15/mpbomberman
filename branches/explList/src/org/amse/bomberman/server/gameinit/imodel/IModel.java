/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit.imodel;

import java.util.List;
import org.amse.bomberman.server.gameinit.Pair;
import org.amse.bomberman.server.gameinit.Player;
import org.amse.bomberman.util.Constants.Direction;

/**
 *
 * @author chibis
 */
public interface IModel {

    public String getMapName();

    void placeBomb(Player player);

    boolean doMove(Player player, Direction direction);

    void removePlayer(int playerID);

    int[][] getMapArray();

    List<Pair> getExplosionSquares();

    void printToConsole();

    void changeMapForCurMaxPlayers(int curMaxPlayers);

    int xCoordOf(int playerID);

    int yCoordOf(int playerID);
}
