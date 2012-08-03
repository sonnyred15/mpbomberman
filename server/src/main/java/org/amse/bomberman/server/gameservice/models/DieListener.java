
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameservice.models;

import org.amse.bomberman.server.gameservice.models.impl.ModelPlayer;

/**
 * Interface for listener of players die.
 * @author Kirilchuk V.E
 */
public interface DieListener {

    /**
     * Notifyes listener about die of player.
     * @param player player that died.
     */
    void playerDied(ModelPlayer player);
}
