
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit.control;

import org.amse.bomberman.server.gameinit.Game;

/**
 * Interface for game start listeners.
 * @author Kirilchuk V.E
 */
public interface GameStartedListener {

    /**
     * Tells that game was started.
     */
    void started(Game game);
}
