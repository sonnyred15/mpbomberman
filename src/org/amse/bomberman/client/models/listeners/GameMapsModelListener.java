package org.amse.bomberman.client.models.listeners;

/**
 *
 * @author Kirilchuk V.E.
 */
public interface GameMapsModelListener {

    void updateGameMaps();

    void gameMapsError(String error);
}
