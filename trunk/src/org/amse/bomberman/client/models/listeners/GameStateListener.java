package org.amse.bomberman.client.models.listeners;

/**
 *
 * @author Kirilchuk V.E.
 */
public interface GameStateListener {

    void updateGameState();

    void gameTerminated(String cause);
}
