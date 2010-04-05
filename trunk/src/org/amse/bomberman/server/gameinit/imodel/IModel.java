
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

    int addPlayer(String name);

    void bombDetonated(Bomb bomb);

    void detonateBombAt(Pair position);

    int getCurrentPlayersNum();

    List<Pair> getExplosionSquares();

    GameMap getGameMap();

    @Deprecated // use getGameMap.getField() instead
    int[][] getGameMapArray();

    @Deprecated // use getGameMap.getName() instead
    String getGameMapName();

    Player getPlayer(int PlayerID);

    List<Player> getPlayersList();

    boolean isExplosion(Pair pair);

    void notifyGameMapUpdateListeners();

    void playerBombed(Player atacker, int victimID);

    void playerBombed(Player atacker, Player victim);

    @Deprecated // or model must implement DieListener or this mehod is not a model interface
    void playerDied(Player player);

    @Deprecated    // think is not needed
    void printToConsole();

    void removeExplosions(List<Pair> explosions);

    void removeGameMapUpdateListener(
            GameMapUpdateListener gameMapUpdateListener);

    void removePlayer(int playerID);

    void startup();

    boolean tryDoMove(MoveableObject objectToMove, Direction direction);

    boolean tryPlaceBomb(Player player);
}
