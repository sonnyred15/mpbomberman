/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit.imodel;

import java.util.ArrayList;
import java.util.List;
import org.amse.bomberman.server.gameinit.Bomb;
import org.amse.bomberman.server.gameinit.bot.Bot;
import org.amse.bomberman.server.gameinit.GameMap;
import org.amse.bomberman.server.gameinit.MoveableMapObject;
import org.amse.bomberman.server.gameinit.Pair;
import org.amse.bomberman.server.gameinit.Player;
import org.amse.bomberman.util.Constants.Direction;

/**
 *
 * @author chibis
 */
public interface IModel {

    void addBomb(Bomb bomb);

    void bombStartDetonating(Bomb bomb);

    Bot addBot(String botName);

    void startBots();

    void removeBot(Bot bot);

    GameMap getMap();

    String getMapName();

    void placeBomb(Player player);

    void detonateBomb(int x, int y);

    void playerBombed(Player atacker, int victimID);

    void playerBombed(Player atacker, Player victim);

    boolean doMove(MoveableMapObject objectToMove, Direction direction);

    void removePlayer(int playerID);

    int[][] getMapArray();

    void addExplosions(List<Pair> explosions);

    void removeExplosion(Pair pair);

    boolean isExplosion(Pair pair);

    List<Pair> getExplosionSquares();

    void printToConsole();

    void changeMapForCurMaxPlayers(int curMaxPlayers);

    int xCoordOf(int playerID);

    int yCoordOf(int playerID);
}
