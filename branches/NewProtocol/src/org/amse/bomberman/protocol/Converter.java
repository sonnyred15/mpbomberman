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

    List<T> convertExplosions(List<Pair> expl);

    List<T> convertField(int[][] gameMapField);

    List<T> convertFieldAndExplosions(Game game);

    List<T> convertFieldExplPlayer(Game game, int playerID);

    List<T> convertGameInfo(Game game, GamePlayer player);

    List<T> convertGameMapsList();

    String convertGameParams(Game game, int gameID);

    String convertGameStartStatus(Game game);

    List<T> convertPlayerInfo(ModelPlayer player);

    List<T> convertPlayersStats(final List<ModelPlayer> playersList);

    List<T> convertUnstartedGames(List<Game> allGames);
}
