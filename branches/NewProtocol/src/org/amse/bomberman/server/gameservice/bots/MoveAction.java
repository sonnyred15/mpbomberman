
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameservice.bots;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameservice.Game;
import org.amse.bomberman.server.gameservice.models.impl.ModelPlayer;
import org.amse.bomberman.util.Constants.Direction;

/**
 * Class that represents move action for bot.
 * @author Kirilchuk V.E.
 */
public class MoveAction implements Action {
    private ModelPlayer player;
    private Direction   direction;

    /**
     * Constructor of this action.
     * @param direction move direction.
     * @param player bot to move.
     */
    public MoveAction(Game game, ModelPlayer player, Direction direction) {
        this.direction = direction;
        this.player = player;
    }

    /**
     * Method from IAction interface. Execute action in defined game.
     * @see IAction
     * @param game game in which action must be executed.
     */
    public void executeAction(Game game) {
        game.tryDoMove(this.player.getID(), this.direction);
    }
}
