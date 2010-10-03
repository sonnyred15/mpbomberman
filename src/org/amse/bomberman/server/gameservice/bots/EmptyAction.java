
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameservice.bots;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameservice.Game;

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
    public void executeAction(Game game) {
        ;    // do_nothing;
    }
}
