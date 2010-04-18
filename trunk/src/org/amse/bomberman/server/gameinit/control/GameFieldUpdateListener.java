
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit.control;

/**
 * Interface for listeners of changes on game field.
 * Note that actually gameMap field does not correspond for explosions.
 * But this listeners must be notifyed in all situations when game field must
 * be updated by clients(and about new explosions too).
 * @author Kirilchuk V.E
 */
public interface GameFieldUpdateListener {

    /**
     * Tells that game field changed. Note that actually gameMap field
     * does not correspond for explosions.
     * But listeners must be notifyed in all situations when game field must
     * be updated by clients(and about new explosions too).
     */
    void gameFieldChanged();
}
