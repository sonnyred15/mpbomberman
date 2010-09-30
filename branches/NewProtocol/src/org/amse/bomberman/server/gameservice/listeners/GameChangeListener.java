/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameservice.listeners;

import org.amse.bomberman.server.gameservice.Game;

/**
 *
 * @author Kirilchuk V.E
 */
public interface GameChangeListener {

    void parametersChanged(Game game);

    /**
     * Tells that game was started.
     */
    void gameStarted(Game game);

    /**
     * Tells that game was terminated.
     */
    void gameTerminated(Game game);

    void newChatMessage(String message);

    void fieldChanged();

    void gameEnded(Game game);

    void statsChanged(Game game);
}
