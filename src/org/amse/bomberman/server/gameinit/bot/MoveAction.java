
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit.bot;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.util.Constants.Direction;

/**
 * Class that represents move action for bot.
 * @author Kirilchuk V.E.
 */
public class MoveAction implements Action {
    private Bot       bot;
    private Direction direction;

    /**
     * Constructor of this action.
     * @param direction move direction.
     * @param bot bot to move.
     */
    public MoveAction(Direction direction, Bot bot) {
        this.direction = direction;
        this.bot = bot;
    }

    /**
     * Method from IAction interface. Execute action in defined game.
     * @see IAction
     * @param game game in which action must be executed.
     */
    public void executeAction(Game game) {
        game.tryDoMove(this.bot.getID(), this.direction);
    }
}
