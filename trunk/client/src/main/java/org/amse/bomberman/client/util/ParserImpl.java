package org.amse.bomberman.client.util;

import java.util.ArrayList;
import java.util.List;

import org.amse.bomberman.client.models.gamemodel.Player;
import org.amse.bomberman.client.models.gamemodel.impl.ImmutableCell;
import org.amse.bomberman.client.models.gamemodel.impl.SimpleGameMap;
import org.amse.bomberman.client.models.gamemodel.impl.SimplePlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of Parser.
 *
 * @author Mikhail Korovkin
 * @author Kirilchuk V.E.
 */
public class ParserImpl implements Parser {

    private static final Logger LOG = LoggerFactory.getLogger(ParserImpl.class);

    @Override
    public SimpleGameMap parseGameMap(List<String> list) {//TODO CLIENT SERVER split gameMap and Player info
        try {
            if (list == null || list.isEmpty()) {
                throw new IllegalArgumentException("Empty or null data.");
            }

            LOG.trace("Start parsing gameMap.");
            if (LOG.isDebugEnabled()) {
                for (String string : list) {
                    LOG.debug(string);
                }
            }

            SimpleGameMap map = null;


            // PARSING FIELD 0
            int n = 0; //dimension
            n = Integer.parseInt(list.get(0));
            map = new SimpleGameMap(n);
            for (int i = 0; i < n; i++) {
                String[] numbers = list.get(i + 1).split(" ");
                for (int j = 0; j < numbers.length; j++) {
                    map.setCell(new ImmutableCell(i, j), Integer.parseInt(numbers[j]));
                }
            }

            // PARSING EXPLOSIONS n + 1
            int k = Integer.parseInt(list.get(n + 1)); //explosions count
            ArrayList<ImmutableCell> expl = new ArrayList<ImmutableCell>(k);
            for (int i = 0; i < k; i++) {
                String[] xy = list.get(i + n + 2).split(" ");
                ImmutableCell buf = new ImmutableCell(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]));
                expl.add(buf);
            }

            map.setExplosions(expl);
            return map;
        } catch (RuntimeException ex) {
            LOG.error("Wrong format of gameMap.", ex);
            throw ex;
        }
    }

    @Override
    public Player parsePlayer(List<String> list) {//TODO CLIENT SERVER split gameMap and Player info
        try {
            if (list == null || list.isEmpty()) {
                throw new IllegalArgumentException("Empty or null data.");
            }

            LOG.trace("Start parsing player.");
            if (LOG.isDebugEnabled()) {
                for (String string : list) {
                    LOG.debug(string);
                }
            }

            Player player = new SimplePlayer();

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
            player.setCoord(new ImmutableCell(x, y));
            player.setBombAmount(maxBombs);
            player.setBombRadius(radius);

            return player;
        } catch (RuntimeException ex) {
            LOG.error("Wrong format of player.", ex);
            throw ex;
        }
    }
}
