/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.protocol;

import java.util.List;
import org.amse.bomberman.server.gameservice.Game;
import org.amse.bomberman.server.gameservice.GamePlayer;
import org.amse.bomberman.server.gameservice.models.impl.ModelPlayer;
import org.amse.bomberman.util.Pair;

/**
 *
 * @author Kirilchuk V.E.
 */
public interface Converter<T> {

    /**
     * Creates list of explosions.
     * Each string of list is coordinate of explosion in next format:
     * <p>
     * "x=... y=..."
     *
     * @param expl explosions to stringazize.
     * @return list of explosions as list of strings.
     */
    List<T> convertExplosions(List<Pair> expl);

    /**
     * Returnes list of strings in next format:
     * <p>
     * dimension
     * <p>
     * int int int .. int
     * <p>
     * ..................
     * <p>
     * int int int .. int
     * @param gameMapField field to make list of strings from.
     * @return gameMapField as list of strings.
     */
    List<T> convertField(int[][] gameMapField);

    /**
     * Returns list of strings in next format:
     * <p>
     * dimension
     * <p>
     * int int int .. int
     * <p>
     * ..................
     * <p>
     * int int int .. int
     * <p>
     * x=... y=...
     * <p>
     * x=... y=... this is explosions.
     * <br>
     * @param game game to get field and exloions from.
     * @return list of strings of gameMapField and explosions
     */
    List<T> convertFieldAndExplosions(Game game);

    /**
     * Returns list of strings in next format:
     * <p>
     * dimension
     * <p>
     * int int int .. int
     * <p>
     * ..................
     * <p>
     * int int int .. int
     * <p>
     * x=... y=...
     * <p>
     * x=... y=... (this is explosions.)
     * <p>
     * 1 (separator - always just one.)
     * <p>
     * playerInfo (watch playerInfo method)
     * @param game game to get field and exloions from.
     * @param player player to get info from.
     * @return list of strings of gameMapField and explosions
     */
    List<T> convertFieldExplPlayer(Game game, int playerID);

    /**
     * Creates string of some game parameters for client(controller)
     * in next format:
     * <p>
     * "true/false maxPlayers curGamePlayersNum player1info player2info ..."
     * <p>
     * true if this client is owner of this game, false otherwise.
     * <p>
     * To playerInfo watch playerInfo method.
     * @param controller represent client to get info for.
     * @return string of some game parameters for client.
     */
    List<T> convertGameInfo(Game game, GamePlayer player);

    /**
     * Returns list of string - names of availible gameMaps.
     * @return list of string - names of availible gameMaps.
     */
    List<T> convertGameMapsList();

    /**
     * Creates string of game parameters in next format:
     * <p>
     * "gameID gameName gameMapName curPlayersNum maxPlayers"
     * @param game game to get parameters from.
     * @param gameID ID of game.
     * @return string of game parameters.
     */
    String convertGameParams(Game game, int gameID);

    /**
     * Returns game start status - started game or not.
     * @param game game to get status from.
     * @return "started" if game started, "not started" otherwise.
     */
    String convertGameStartStatus(Game game);

    /**
     * Returns string in next format:
     * <p>
     * positionX positionY nickName lives bombs maxBombs
     * @param player player to get info from.
     * @return players info.
     */
    List<T> convertPlayerInfo(ModelPlayer player);

    List<T> convertPlayersStats(final List<ModelPlayer> playersList);

    /**
     * List of strings - unstarted games strings in next format:
     * <p>
     * "gameID gameName gameMapName curPlayersNum maxPlayers"
     * @param allGames started and unstarted games.
     * @return list of strings - unstarted games strings.
     */
    List<T> convertUnstartedGames(List<Game> allGames);

}
