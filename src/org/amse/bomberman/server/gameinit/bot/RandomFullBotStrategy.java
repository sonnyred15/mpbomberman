
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit.bot;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.GameMap;
import org.amse.bomberman.server.gameinit.imodel.IModel;
import org.amse.bomberman.util.Constants.Direction;
import org.amse.bomberman.util.Pair;

//~--- JDK imports ------------------------------------------------------------

import java.util.Random;

/**
 * Class that represents bot strategy where bot
 * randomly choose what to do. Availiable actions is any actions
 * that inherited from IAction.
 * @see IAction
 * @see EmptyAction
 * @see PlaceAndMoveAction
 * @see MoveAction
 * @author Kirilchuck V.E.
 */
public class RandomFullBotStrategy extends BotStrategy {
    private final static int PLACE__BOMB_PROBABILITY = 5;
    private Pair             target;

    /**
     * Method from BotStrategy. Returns new place bomb and move
     * or just move action.
     * @see BotStrategy
     * @param bot bot that thinking about action.
     * @param model model that owns this bot.
     * @return new place bomb and move or just move action.
     */
    @Override
    public IAction thinkAction(Bot bot, IModel model) {
        if (bot.getPosition().equals(this.target) || (target == null)) {
            target = findNewTarget(model);
        }

        Direction direction = null;

        do {
            try {
                Thread.sleep(75);    // here bot thread will wait for some time.
                direction = findWay(bot.getPosition(), target,
                                    model.getGameMap().getField(), model);

                // System.out.println("Direction" + direction.toString());
            } catch (IllegalArgumentException ex) {
                target = findNewTarget(model);

                // System.out.println("NEW TARGET");
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        } while (direction == null);

        Random rnd = new Random();
        int    n = rnd.nextInt(100);

        if (n < PLACE__BOMB_PROBABILITY) {
            return new PlaceAndMoveAction(direction, bot);
        }

        return new MoveAction(direction, bot);
    }

    private Pair findNewTarget(IModel model) {
        GameMap map = model.getGameMap();
        Random  random = new Random();
        int     x = 0;
        int     y = 0;

        do {
            x = random.nextInt(map.getDimension() - 1);
            y = random.nextInt(map.getDimension() - 1);
        } while (!(map.isEmpty(x, y) || map.isBonus(x, y)));

        Pair dir = new Pair(x, y);

        // System.out.println("Choosed " + dir.toString());
        return dir;
    }
}
