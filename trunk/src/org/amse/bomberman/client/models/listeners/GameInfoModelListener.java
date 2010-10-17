package org.amse.bomberman.client.models.listeners;

/**
 *
 * @author Kirilchuk V.E.
 */
public interface GameInfoModelListener {

    void updateGameInfo();

    void gameInfoError(String error);
}
