package org.amse.bomberman.server.gameservice.bots;

import org.amse.bomberman.server.gameservice.impl.Game;

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
