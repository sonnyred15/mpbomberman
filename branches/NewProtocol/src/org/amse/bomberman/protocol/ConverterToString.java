
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.protocol;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameservice.Game;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.Controller;
import org.amse.bomberman.server.gameservice.models.impl.ModelPlayer;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.amse.bomberman.server.gameservice.GamePlayer;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Creator;
import org.amse.bomberman.util.Pair;

/**
 * Utility class.
 * Main appointment to make String r List of Strings from something.
 * @author Kirilchuk V.E
 */
public class ConverterToString implements Converter<String> {

    public List<String> convertPlayersStats(final List<ModelPlayer> playersList) {
        List<ModelPlayer> players = new ArrayList<ModelPlayer>(playersList);

        Collections.sort(players, new Comparator<ModelPlayer>() {

            @Override
            public int compare(ModelPlayer player1, ModelPlayer player2) {
                int points1 = player1.getPoints();
                int points2 = player2.getPoints();

                if (points1 == points2) {
                    return 0;
                } else if (points1 > points2) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        List<String> result = new ArrayList<String>();
        for (ModelPlayer player : players) {
            result.add(player.getNickName());
            result.add(String.valueOf(player.getKills()));
            result.add(String.valueOf(player.getDeaths()));
            result.add(String.valueOf(player.getPoints()));
        }
        
        return result;
    }

    /**
     * Creates list of explosions.
     * Each string of list is coordinate of explosion in next format:
     * <p>
     * "x=... y=..."
     *
     * @param expl explosions to stringazize.
     * @return list of explosions as list of strings.
     */
    public List<String> convertExplosions(List<Pair> expl) {    // CHECK < THIS!!!// WHATS ABOUT SYNCHRONIZATION?
        List<String> result = new ArrayList<String>();

        result.add(String.valueOf(expl.size()));

        for (Pair pair : expl) {
            result.add(pair.getX() + " " + pair.getY());
        }

        return result;
    }

    /**
     * Creates string of game parameters in next format:
     * <p>
     * "gameID gameName gameMapName curPlayersNum maxPlayers"
     * @param game game to get parameters from.
     * @param gameID ID of game.
     * @return string of game parameters.
     */
    public String convertGameParams(Game game, int gameID) {
        StringBuilder result = new StringBuilder();

        result.append(gameID);
        result.append(ProtocolConstants.SPLIT_SYMBOL);
        result.append(game.getName());
        result.append(ProtocolConstants.SPLIT_SYMBOL);
        result.append(game.getGameMapName());
        result.append(ProtocolConstants.SPLIT_SYMBOL);
        result.append(game.getCurrentPlayersNum());
        result.append(ProtocolConstants.SPLIT_SYMBOL);
        result.append(game.getMaxPlayers());

        return result.toString();
    }

    /**
     * Creates string of some game parameters for client(controller)
     * in next format:
     * <p>
     * "true/false maxPlayers curGamePlayersNum player1info player2info ..."
     * <p>
     * true if this client is owner of this game, false otherwise.
     * <p>
     * To playerInfo watch playerInfo method.
     * @param player represent client to get info for.
     * @return string of some game parameters for client.
     */
    public List<String> convertGameInfo(Game game, GamePlayer player) {
        List<String> result  = new ArrayList<String>();

        if (game.isGameOwner(player)) {
            result.add("true");
        } else {
            result.add("false");
        }

        result.add(String.valueOf(game.getMaxPlayers()));

        List<ModelPlayer> players = game.getCurrentPlayers();

        result.add(String.valueOf(players.size()));

        for (ModelPlayer player1 : players) {
            result.add(player1.getNickName());
        }

        return result;
    }

    /**
     * Returns list of string - names of availible gameMaps.
     * @return list of string - names of availible gameMaps.
     */
    public List<String> convertGameMapsList() {
        return Creator.createGameMapsList();
    }

    /**
     * Returns game start status - started game or not.
     * @param game game to get status from.
     * @return "started" if game started, "not started" otherwise.
     */
    public String convertGameStartStatus(Game game) {    // TODO started not started bad decision make true false.
        if (game.isStarted()) {
            return "started.";
        } else {
            return "not started.";
        }
    }

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
    public List<String> convertField(int[][] gameMapField) {    // CHECK < THIS!!!// WHATS ABOUT field SYNCHRONIZATION?
        List<String> lst = new ArrayList<String>();

        lst.add("" + gameMapField.length);

        for (int i = 0; i < gameMapField.length; ++i) {
            StringBuilder buff = new StringBuilder();

            for (int j = 0; j < gameMapField.length; j++) {
                buff.append(gameMapField[i][j]);
                buff.append(" ");
            }

            lst.add(buff.toString());
        }

        return lst;
    }

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
    public List<String> convertFieldAndExplosions(Game game) {
        List<String> result = convertField(game.getGameField());

        result.addAll(convertExplosions(game.getExplosionSquares()));

        return result;
    }

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
    public List<String> convertFieldExplPlayer(Game game, int playerID) {
        int[][]      field             = game.getGameField();
        List<ModelPlayer> players           = game.getCurrentPlayers();
        List<String> stringalizedField = new ArrayList<String>();

        stringalizedField.add(String.valueOf((field.length)));

        for (int i = 0; i < field.length; ++i) {
            StringBuilder buff = new StringBuilder();

            for (int j = 0; j < field.length; ++j) {
                int n = field[i][j];

                if (n == Constants.MAP_BOMB) {
                    for (ModelPlayer pl : players) {
                        int x = pl.getPosition().getX();
                        int y = pl.getPosition().getY();

                        if ((x == i) && (y == j) && pl.isAlive()) {
                            n = (n + 100 + pl.getID()); //player and bomb in one cell
                        }
                    }
                }

                buff.append(n);
                buff.append(" ");//TODO not need for last n in row
            }

            stringalizedField.add(buff.toString());
        }

        stringalizedField.addAll(convertExplosions(game.getExplosionSquares()));
        
        ModelPlayer player = game.getPlayer(playerID); //TODO if playerID was incorrect.
        stringalizedField.addAll(convertPlayerInfo(player));

        return stringalizedField;
    }

    /**
     * Returns string in next format:
     * <p>
     * positionX positionY nickName lives bombs maxBombs
     * @param player player to get info from.
     * @return players info.
     */
    public List<String> convertPlayerInfo(ModelPlayer player) {
        List<String> result = new ArrayList<String>();

        Pair position = player.getPosition();
        result.add(String.valueOf(position.getX()));
        result.add(String.valueOf(position.getY()));
        result.add(player.getNickName());
        result.add(String.valueOf(player.getLives()));
        result.add(String.valueOf(player.getSettedBombsNum()));
        result.add(String.valueOf(player.getMaxBombs()));
        result.add(String.valueOf(player.getRadius()));

        return result;
    }

    /**
     * List of strings - unstarted games strings in next format:
     *  <p>
     *  "gameID gameName gameMapName curPlayersNum maxPlayers"
     *  @param allGames started and unstarted games.
     *  @return list of strings - unstarted games strings.
     */
    public List<String> convertUnstartedGames(List<Game> allGames) {
        List<String> unstartedGames = new ArrayList<String>();

        if (allGames != null) {
            Iterator<Game> it = allGames.iterator();

            for (int i = 0; it.hasNext(); ++i) {
                Game game = it.next();

                // send only games that are not started!!!
                if (!game.isStarted()) {
                    unstartedGames.add(convertGameParams(game, i));
                }
            }
        }

        return unstartedGames;
    }
}
