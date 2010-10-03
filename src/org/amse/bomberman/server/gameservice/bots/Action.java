
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameservice.bots;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameservice.Game;

/**
 * Interface for bot actions.
 * @author Kirilchuk V.E
 */
public interface Action {

    /**
     * Executing action in specified game.
     * <p>
     * For example if action is to move some bot in some direction
     * this method will call game method to move...
     * @param game
     */
    void executeAction(Game game);
}
