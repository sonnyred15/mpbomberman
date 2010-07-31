
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.util;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.net.tcpimpl.Controller;
import org.amse.bomberman.server.gameinit.imodel.Player;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class.
 * Main appointment to make String r List of Strings from something.
 * @author Kirilchuk V.E
 */
public final class Stringalize {
    private Stringalize() {}

    public static List<String> playersStats(final List<Player> playersList) {
        List<Player> players = new ArrayList<Player>(playersList);

        Collections.sort(players, new Comparator<Player>() {

            @Override
            public int compare(Player player1, Player player2) {
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
        StringBuilder str = null;
        for (Player player : players) {
            str = new StringBuilder();
            str.append(player.getNickName());
            str.append(' ');
            str.append(player.getPoints());
            str.append(' ');
            str.append(player.getDeaths());
            result.add(str.toString());
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
    public static List<String> explosions(List<Pair> expl) {    // CHECK < THIS!!!// WHATS ABOUT SYNCHRONIZATION?
        List<String> lst = new ArrayList<String>();

        lst.add("" + expl.size());

        for (Pair pair : expl) {
            lst.add(pair.getX() + " " + pair.getY());
        }

        return lst;
    }

    /**
     * Creates string of game parameters in next format:
     * <p>
     * "gameID gameName gameMapName curPlayersNum maxPlayers"
     * @param game game to get parameters from.
     * @param gameID ID of game.
     * @return string of game parameters.
     */
    public static String gameParams(Game game, int gameID) {
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
     * @param controller represent client to get info for.
     * @return string of some game parameters for client.
     */
    public static List<String> gameInfoForClient(Controller controller) {
        List<String> lst  = new ArrayList<String>();
        Game         game = controller.getMyGame();

        if (controller == game.getOwner()) {
            lst.add("true");
        } else {
            lst.add("false");
        }

        lst.add("" + game.getMaxPlayers());

        List<Player> players = game.getCurrentPlayers();

        lst.add("" + players.size());

        for (Player player1 : players) {
            lst.add(player1.getNickName());
        }

        return lst;
    }

    /**
     * Returns list of string - names of availible gameMaps.
     * @return list of string - names of availible gameMaps.
     */
    public static List<String> gameMapsList() {
        return Creator.createGameMapsList();
    }

    /**
     * Returns game start status - started game or not.
     * @param game game to get status from.
     * @return "started" if game started, "not started" otherwise.
     */
    public static String gameStartStatus(Game game) {    // TODO started not started bad decision make true false.
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
    public static List<String> field(int[][] gameMapField) {    // CHECK < THIS!!!// WHATS ABOUT field SYNCHRONIZATION?
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
    public static List<String> fieldAndExplosionsInfo(Game game) {
        List<String> result = Stringalize.field(game.getGameField());

        result.addAll(Stringalize.explosions(game.getExplosionSquares()));

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
    public static List<String> fieldExplPlayerInfo(Game game, Player player) {
        int[][]      field             = game.getGameField();
        List<Player> players           = game.getCurrentPlayers();
        List<String> stringalizedField = new ArrayList<String>();

        stringalizedField.add("" + field.length);

        for (int i = 0; i < field.length; ++i) {
            StringBuilder buff = new StringBuilder();

            for (int j = 0; j < field.length; j++) {
                int n = field[i][j];

                if (n == Constants.MAP_BOMB) {
                    for (Player pl : players) {
                        int x = pl.getPosition().getX();
                        int y = pl.getPosition().getY();

                        if ((x == i) && (y == j) && pl.isAlive()) {
                            n += 100 + pl.getID();
                        }
                    }
                }

                buff.append(n);
                buff.append(" ");
            }

            stringalizedField.add(buff.toString());
        }

        stringalizedField.addAll(Stringalize.explosions(game.getExplosionSquares()));
        stringalizedField.add("" + 1);
        stringalizedField.add(Stringalize.playerInfo(player));

        return stringalizedField;
    }

    /**
     * Returns string in next format:
     * <p>
     * positionX positionY nickName lives bombs maxBombs
     * @param player player to get info from.
     * @return players info.
     */
    public static String playerInfo(Player player) {    // CHECK < THIS!!!//
        return player.getInfo();
    }

    /**
     * List of strings - unstarted games strings in next format:
     *  <p>
     *  "gameID gameName gameMapName curPlayersNum maxPlayers"
     *  @param allGames started and unstarted games.
     *  @return list of strings - unstarted games strings.
     */
    public static List<String> unstartedGames(List<Game> allGames) {
        List<String> unstartedGames = new ArrayList<String>();

        if (allGames != null) {
            Iterator<Game> it = allGames.iterator();

            for (int i = 0; it.hasNext(); ++i) {
                Game game = it.next();

                // send only games that are not started!!!
                if (!game.isStarted()) {
                    unstartedGames.add(Stringalize.gameParams(game, i));
                }
            }
        }

        return unstartedGames;
    }
}
