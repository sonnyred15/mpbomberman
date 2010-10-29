package org.amse.bomberman.server.gameservice.bots;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameservice.impl.Game;

/**
 * Class that represents empty action for bot.
 * @author Kirilchuk V.E.
 */
public class EmptyAction implements Action {

    /**
     * Method from IAction interface.
     * This method do nothing as expected from EmptyAction...
     * @see IAction
     * @param game game in which action must be executed.
     */
    @Override
    public void executeAction(Game game) {
        ;    // do_nothing;
    }
}
