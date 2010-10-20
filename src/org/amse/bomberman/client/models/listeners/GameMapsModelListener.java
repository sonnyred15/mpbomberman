package org.amse.bomberman.client.models.listeners;

/**
 *
 * @author Kirilchuk V.E.
 */
public interface GameMapsModelListener {

    void updateGameMaps();

    /**
     * Method of listener to process error while
     * receiving info about gameMaps.
     *
     * @param error description.
     */
    void gameMapsError(String error);
}
