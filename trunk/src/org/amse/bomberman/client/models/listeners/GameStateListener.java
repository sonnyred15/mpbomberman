package org.amse.bomberman.client.models.listeners;

/**
 *
 * @author Kirilchuk V.E.
 */
public interface GameStateListener {

    void updateGameState();

    /**
     * Method of listener to process game termination signal.
     *
     * @param cause description.
     */
    void gameTerminated(String cause);
}
