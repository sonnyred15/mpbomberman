package org.amse.bomberman.util.impl;

import java.util.ArrayList;
import java.util.List;

import org.amse.bomberman.client.models.gamemodel.GameMap;
import org.amse.bomberman.client.models.gamemodel.Cell;
import org.amse.bomberman.client.models.gamemodel.Player;
import org.amse.bomberman.client.models.gamemodel.impl.PlayerImpl;
import org.amse.bomberman.util.Parser;

/**
 *
 * @author Mikhail Korovkin
 * @author Kirilchuk V.E.
 */
public class ParserImpl implements Parser {

    public GameMap parseGameMap(List<String> list) {//TODO CLIENT SERVER split gameMap and Player info
        GameMap map = null;
        try {

            // PARSING FIELD 0
            int n = 0; //dimension
            n = Integer.parseInt(list.get(0));
            map = new GameMap(n);
            for (int i = 0; i < n; i++) {
                String[] numbers = list.get(i + 1).split(" ");
                for (int j = 0; j < numbers.length; j++) {
                    map.setCell(new Cell(i, j), Integer.parseInt(numbers[j]));
                }
            }

            // PARSING EXPLOSIONS n + 1
            int k = Integer.parseInt(list.get(n + 1)); //explosions count
            ArrayList<Cell> expl = new ArrayList<Cell>(k);
            for (int i = 0; i < k; i++) {
                String[] xy = list.get(i + n + 2).split(" ");
                Cell buf = new Cell(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]));
                expl.add(buf);
            }            

            map.setExplosions(expl);
        } catch (NumberFormatException ex) {
            System.out.println("Wrong format of gameMap: " + ex);
        }
        return map;
    }

    public Player parsePlayer(List<String> list) {//TODO CLIENT SERVER split gameMap and Player info
        Player player = new PlayerImpl();
        try {
            int n = 0; //dimension
            n = Integer.parseInt(list.get(0));
            int k = Integer.parseInt(list.get(n + 1)); //explosions count

            // PARSING PLAYER n + k + 2
            int x = Integer.parseInt(list.get(n + k + 2));
            int y = Integer.parseInt(list.get(n + k + 3));
            String nick = list.get(n + k + 4);
            int lives = Integer.parseInt(list.get(n + k + 5));
            int bombs = Integer.parseInt(list.get(n + k + 6));
            int maxBombs = Integer.parseInt(list.get(n + k + 7));
            int radius = Integer.parseInt(list.get(n + k + 8));

            player.setLives(lives);
            player.setCoord(new Cell(x, y));
            player.setBombAmount(maxBombs);
            player.setBombRadius(radius);
        } catch (NumberFormatException ex) {
            System.out.println("Wrong format of player: " + ex);
        }
        return player;
    }
}
