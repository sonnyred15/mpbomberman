
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit.imodel;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.Bomb;
import org.amse.bomberman.server.gameinit.GameMap;
import org.amse.bomberman.server.gameinit.MoveableObject;
import org.amse.bomberman.server.gameinit.Pair;
import org.amse.bomberman.server.gameinit.Player;
import org.amse.bomberman.server.gameinit.control.GameMapUpdateListener;
import org.amse.bomberman.util.Constants.Direction;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

/**
 *
 * @author chibis
 */
public interface IModel {
    Player addBot(String botName);

    void addExplosions(List<Pair> explosions);

    void addGameMapUpdateListener(GameMapUpdateListener gameMapUpdateListener);

    Player addPlayer(String name);

    void bombDetonated(Bomb bomb);

    void detonateBombAt(int x, int y);

    int getCurrentPlayersNum();

    List<Pair> getExplosionSquares();

    GameMap getGameMap();

    int[][] getGameMapArray();

    String getGameMapName();

    Player getPlayer(int PlayerID);

    List<Player> getPlayersList();

    boolean isExplosion(Pair pair);

    void playerBombed(Player atacker, int victimID);

    void playerBombed(Player atacker, Player victim);

    void playerDied(Player player);

    @Deprecated    // think is not needed
    void printToConsole();

    void removeExplosion(Pair pair);

    void removeGameMapUpdateListener(
            GameMapUpdateListener gameMapUpdateListener);

    void removePlayer(int playerID);

    void startup();

    boolean tryDoMove(MoveableObject objectToMove, Direction direction);

    boolean tryPlaceBomb(Player player);
}
