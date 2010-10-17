package org.amse.bomberman.client.models.listeners;

/**
 *
 * @author Kirilchuk V.E.
 */
public interface GamesModelListener {

    void updateGamesList();

    void gamesListError(String error);
}
