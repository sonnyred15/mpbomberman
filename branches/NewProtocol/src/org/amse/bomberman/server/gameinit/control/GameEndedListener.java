
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit.control;

import org.amse.bomberman.server.gameinit.Game;

/**
 * Interface for game end listener.
 * @author Kirilchuk V.E
 */
public interface GameEndedListener {

    /**
     * Tells that game was ended.
     */
    void gameEnded(Game game);
}
