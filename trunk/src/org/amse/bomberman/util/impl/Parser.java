package org.amse.bomberman.util.impl;

import java.util.ArrayList;
import java.util.List;
import org.amse.bomberman.client.model.BombMap;
import org.amse.bomberman.client.model.Cell;
import org.amse.bomberman.client.model.IModel;
import org.amse.bomberman.client.model.impl.Model;
import org.amse.bomberman.util.IParser;
import org.amse.bomberman.util.ProtocolConstants;

/**
 *
 * @author Mikhail Korovkin
 */
public class Parser implements IParser {

    public BombMap parse(List<String> list) {
        BombMap map = null;
        try {
            int n = 0;
            n = Integer.parseInt(list.get(0));
            map = new BombMap(n);
            for (int i = 0; i < n; i++) {
                String[] numbers = list.get(i + 1).split(" ");
                for (int j = 0; j < numbers.length; j++) {
                    map.setCell(new Cell(i, j), (int) Integer.parseInt(numbers[j]));
                }
            }
            // receive list of explosive
            int k = Integer.parseInt(list.get(n + 1));
            ArrayList<Cell> expl = new ArrayList<Cell>(k);
            for (int i = 0; i < k; i++) {
                String[] xy = list.get(i + n + 2).split(" ");
                Cell buf = new Cell((int) Integer.parseInt(xy[0])
                        , (int) Integer.parseInt(xy[1]));
                expl.add(buf);
            }
            // receive player info
            // m == 1 always
            int m = Integer.parseInt(list.get(n + k + 2));
            if (m == 1) {
                String[] info = new String[7];
                info = list.get(n + k + 3).split(" ");
                int x = Integer.parseInt(info[0]);
                int y = Integer.parseInt(info[1]);
                String nick = info[2];
                int lives = Integer.parseInt(info[3]);
                int bombs = Integer.parseInt(info[4]);
                int maxBombs = Integer.parseInt(info[5]);
                int radius = Integer.parseInt(info[6]);
                IModel model = Model.getInstance();
                model.setPlayerLives(lives);
                model.setPlayerCoord(new Cell(x, y));
                model.setPlayerBombs(maxBombs);
                model.setPlayerRadius(radius);
            }
            map.setExplosions(expl);
        } catch (NumberFormatException ex) {
            System.out.println("Wrong format of map: " + ex);
        }
        return map;
    }
}
