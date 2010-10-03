
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
 * Class that represents place bomb and then move action for bot.
 * It is a little hack action cause clients can`t do two actions faster than
 * <code>Constants.GAME_STEP_TIME</code>.
 * @author Kirilchuk V.E.
 */
public class PlaceAndMoveAction implements Action {
    private ModelPlayer player;
    private Direction   direction;

    /**
     * Constructor of this action.
     * @param direction move direction.
     * @param bot bot to place bomb and move.
     */
    public PlaceAndMoveAction(Game game, ModelPlayer player, Direction direction) {
        this.direction = direction;
        this.player = player;
    }

    /**
     * Method from IAction interface.
     * Tryes to place bomb at defined position
     * and then move in defined direction.
     * @see IAction
     * @param game game in which action must be executed.
     */
    public void executeAction(Game game) {
        game.tryPlaceBomb(this.player.getID());
        game.tryDoMove(this.player.getID(), this.direction);
    }
}
