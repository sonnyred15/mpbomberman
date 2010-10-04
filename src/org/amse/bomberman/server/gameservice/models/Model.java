
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameservice.models;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameservice.models.impl.Bomb;
import org.amse.bomberman.server.gameservice.models.impl.ModelPlayer;
import org.amse.bomberman.server.gameservice.Game;
import org.amse.bomberman.server.gameservice.GameMap;
import org.amse.bomberman.server.gameservice.models.impl.StatsTable;
import org.amse.bomberman.util.Direction;
import org.amse.bomberman.util.Pair;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

/**
 * Interface that provides methods of Model of BomberMan Game.
 * Model is responsable for game rules and responsable for connection
 * between GameMap and Game.
 * @see Game
 * @see GameMap
 * @author Kirilchuk V.E.
 */
public interface Model {

    /**
     * Method to add explosions from bombes.
     * @param explosions explosions to add.
     */
    void addExplosions(List<Pair> explosions);

    /**
     * Method to add player into Model.
     * @param name nickName of player.
     * @return ingame ID of player.
     */
    int addPlayer(String name);

    /**
     * Method to notify model that bomb was detonated.
     * @param bomb detonated bomb.
     */
    void bombDetonated(Bomb bomb);

    /**
     * Method to force detonate of bomb at defined position.
     * @param position position of bomb to detonate forcibly.
     */
    void detonateBombAt(Pair position);

    void tryEnd();

    /**
     * Returns current players num including bots.
     * @return current players num including bots.
     */
    int getCurrentPlayersNum();

    /**
     * Returns list of explosions.
     * @return list of explosions.
     */
    List<Pair> getExplosionSquares();

    /**
     * Returns GameMap of this model.
     * @see GameMap
     * @return
     */
    GameMap getGameMap();

    /**
     * Returns Player corresponding to defined ID.
     * @see Player
     * @param PlayerID ingame ID of player.
     * @return Player corresponding to defined ID.
     */
    ModelPlayer getPlayer(int PlayerID);

    /**
     * Returns list of players including bots.
     * By contract must return unmodifiable list cause
     * only model must operate with players!
     * @return list of players including bots.
     */
    List<ModelPlayer> getPlayersList();

    /**
     * Checks if square at defined position is under explosion.
     * @param position position to check.
     * @return true if position is under explosion, false otherwise.
     */
    boolean isExplosion(Pair position);

    /**
     * Tells to model that the player was bombed by bomb of another player.
     * @param atacker ID of owner of bomb that damaged the player.
     * @param victimID ID of player that was damaged.
     */
    void playerBombed(ModelPlayer atacker, int victimID);

    /**
     * Tells to model that the player was bombed by bomb of another player.
     * @param atacker owner of bomb that damaged the player.
     * @param victimID ID of player that was damaged.
     */
    void playerBombed(ModelPlayer atacker, ModelPlayer victim);

    /**
     * Printing gameField to console.
     * @deprecated
     */
    void printToConsole();

    /**
     * Removing some explosions from model.
     * @param explosions explosions to remove.
     */
    void removeExplosions(List<Pair> explosions);

    /**
     * Removes player with defined ID.
     * @param playerID ID of player to remove from model.
     */
    boolean removePlayer(int playerID);

    /**
     * Do some preparations and begin the game.
     */
    void startup();

    /**
     * Tryes to move moveable object with defined direction.
     * @param objectToMove object to try to move.
     * @param direction move direction.
     * @return true if object was moved, false otherwise.
     */
    boolean tryDoMove(MoveableObject objectToMove, Direction direction);

    /**
     * Tryes to place bomb by defined player.
     * @param player player that tryes to place bomb.
     * @return true if bomb was placed, false otherwise.
     */
    boolean tryPlaceBomb(ModelPlayer player);

    StatsTable getStatsTable();
}
