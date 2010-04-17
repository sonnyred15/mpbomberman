
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.util;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.Player;
import org.amse.bomberman.server.gameinit.control.Controller;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Kirilchuk V.E
 */
public final class Stringalize {
    private Stringalize() {}

    public static List<String> explosions(List<Pair> expl) {    // CHECK < THIS!!!// WHATS ABOUT SYNCHRONIZATION?
        List<String> lst = new ArrayList<String>();

        lst.add("" + expl.size());

        for (Pair pair : expl) {
            lst.add(pair.getX() + " " + pair.getY());
        }

        return lst;
    }

    public static String game(Game game, int gameIndex) {
        StringBuilder result = new StringBuilder();

        result.append(gameIndex);
        result.append(' ');
        result.append(game.getName());
        result.append(' ');
        result.append(game.getGameMapName());
        result.append(' ');
        result.append(game.getCurrentPlayersNum());
        result.append(' ');
        result.append(game.getMaxPlayers());

        return result.toString();
    }

    public static List<String> gameInfo(Game game, Controller controller) {
        List<String> lst = new ArrayList<String>();

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

    public static List<String> gameMapsList() {
        return Creator.createGameMapsList();
    }

    public static String gameStatus(Game game) {
        if (game.isStarted()) {
            return "started.";
        } else {
            return "not started.";
        }
    }

    public static List<String> map(int[][] map) {    // CHECK < THIS!!!// WHATS ABOUT map SYNCHRONIZATION?
        List<String> lst = new ArrayList<String>();

        lst.add("" + map.length);

        for (int i = 0; i < map.length; ++i) {
            StringBuilder buff = new StringBuilder();

            for (int j = 0; j < map.length; j++) {
                buff.append(map[i][j]);
                buff.append(" ");
            }

            lst.add(buff.toString());
        }

        return lst;
    }

    public static List<String> mapAndExplosionsInfo(Game game) {
        List<String> result = Stringalize.map(game.getGameField());

        result.addAll(Stringalize.explosions(game.getExplosionSquares()));

        return result;
    }

    public static List<String> mapExplPlayerInfo(Game game, Player player) {
        List<String> result = Stringalize.mapAndExplosionsInfo(game);

        result.add("" + 1);
        result.add(Stringalize.playerInfo(player));

        return result;
    }

    // TODO temporary
    public static List<String> mapExplPlayerInfo2(Game game, Player player) {
        int[][]      field = game.getGameField();
        List<Player> players = game.getCurrentPlayers();
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

    public static String playerInfo(Player player) {    // CHECK < THIS!!!//
        return player.getInfo();
    }

    public static List<String> unstartedGames(List<Game> allGames) {
        List<String> unstartedGames = new ArrayList<String>();

        if (allGames != null) {
            Iterator<Game> it = allGames.iterator();

            for (int i = 0; it.hasNext(); ++i) {
                Game game = it.next();

                // send only games that are not started!!!
                if (!game.isStarted()) {
                    unstartedGames.add(Stringalize.game(game, i));
                }
            }
        }

        return unstartedGames;
    }
}
