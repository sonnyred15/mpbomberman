/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.Pair;
import org.amse.bomberman.server.gameinit.Player;

/**
 *
 * @author Kirilchuk V.E
 */
public class Stringalize {

    public static List<String> unstartedGames(List<Game> allGames) {
        List<String> unstartedGames = new ArrayList<String>();
        if (allGames != null) {
            synchronized (allGames) {
                Iterator<Game> it = allGames.iterator();
                for (int i = 0; it.hasNext(); ++i) {
                    Game g = it.next();
                    //send only allGames that are not started!!!
                    if (!g.isStarted()) {
                        unstartedGames.add(i + " " +
                                g.getName() + " " +
                                g.getMapName() + " " +
                                g.getCurrentPlayers() + " " +
                                g.getGameMaxPlayers());
                    }
                }
            }
        }
        return unstartedGames;
    }

    public static List<String> map(int[][] map){ //CHECK < THIS!!!// WHATS ABOUT map SYNCHRONIZATION?
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

    public static List<String> explosions(List<Pair> expl) { //CHECK < THIS!!!// WHATS ABOUT SYNCHRONIZATION?
        List<String> lst = new ArrayList<String>();
        lst.add("" + expl.size());
        for (Pair pair : expl) {
            lst.add(pair.getX() + " " + pair.getY());
        }
        return lst;
    }

    public static String playerInfo(Player player) { //CHECK < THIS!!!//
        return player.getInfo();
    }
}
