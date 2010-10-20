package org.amse.bomberman.client.models.listeners;

/**
 *
 * @author Kirilchuk V.E.
 */
public interface GameInfoModelListener {

    void updateGameInfo();

    /**
     * Method of listener to process error
     * that happened while receiving game info.
     *
     * @param error description.
     */
    void gameInfoError(String error);//TODO CLIENT not clear: Add\kick error in game info..
}
