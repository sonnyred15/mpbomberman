
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameservice.bots;

//~--- non-JDK imports --------------------------------------------------------

import java.util.List;
import org.amse.bomberman.server.gameservice.GameMap;
import org.amse.bomberman.server.gameservice.models.Model;
import org.amse.bomberman.util.Direction;
import org.amse.bomberman.util.Pair;

//~--- JDK imports ------------------------------------------------------------

import java.util.Random;
import org.amse.bomberman.server.gameservice.Game;
import org.amse.bomberman.server.gameservice.models.impl.Bonus;
import org.amse.bomberman.server.gameservice.models.impl.ModelPlayer;
import org.amse.bomberman.util.Constants;

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
    public Action thinkAction(Game game, BotGamePlayer bot) {
        ModelPlayer player = game.getPlayer(bot.getPlayerId());
        if (player.getPosition().equals(target) || (target == null)) {
            target = findNewTarget(game, player);
        }

        Direction direction = null;

        do {
            try {
                direction = findWay(game, player.getPosition(), target);
            } catch (IllegalArgumentException ex) {
                target = findNewTarget(game, player);
            }
        } while (direction == null);

        Random rnd = new Random();
        int    n = rnd.nextInt(100);

        if (n < PLACE__BOMB_PROBABILITY) {
            return new PlaceAndMoveAction(game, player,direction);
        }

        return new MoveAction(game, player, direction);
    }

    private Pair findNewTarget(Game game, ModelPlayer player) {
        int[][] field = game.getGameField();

        Random  random = new Random();
        int     x = 0;
        int     y = 0;

        do {
            x = random.nextInt(field.length - 1);
            y = random.nextInt(field.length - 1);
        } while (!((field[x][y] == Constants.MAP_EMPTY) || Bonus.isBonus(field[x][y])));

        Pair dir = new Pair(x, y);

        return dir;
    }
}
