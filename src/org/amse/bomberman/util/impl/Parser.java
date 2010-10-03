package org.amse.bomberman.util.impl;

import java.util.ArrayList;
import java.util.List;
import org.amse.bomberman.client.model.BombMap;
import org.amse.bomberman.client.model.Cell;
import org.amse.bomberman.client.model.IModel;
import org.amse.bomberman.client.model.impl.Model;
import org.amse.bomberman.util.IParser;
import org.amse.bomberman.protocol.ProtocolConstants;

/**
 *
 * @author Mikhail Korovkin
 */
public class Parser implements IParser {

    public BombMap parse(List<String> list) {
        BombMap map = null;
        try {

            // PARSING FIELD 0
            int n = 0; //dimension
            n = Integer.parseInt(list.get(0));
            map = new BombMap(n);
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
            
            // PARSING PLAYER n + k + 2
            int x = Integer.parseInt(list.get(n + k + 2));
            int y = Integer.parseInt(list.get(n + k + 3));
            String nick = list.get(n + k + 4);
            int lives = Integer.parseInt(list.get(n + k + 5));
            int bombs = Integer.parseInt(list.get(n + k + 6));
            int maxBombs = Integer.parseInt(list.get(n + k + 7));
            int radius = Integer.parseInt(list.get(n + k + 8));

            IModel model = Model.getInstance();
            model.setPlayerLives(lives);
            model.setPlayerCoord(new Cell(x, y));
            model.setPlayerBombs(maxBombs);
            model.setPlayerRadius(radius);

            map.setExplosions(expl);
        } catch (NumberFormatException ex) {
            System.out.println("Wrong format of map: " + ex);
        }
        return map;
    }
}
