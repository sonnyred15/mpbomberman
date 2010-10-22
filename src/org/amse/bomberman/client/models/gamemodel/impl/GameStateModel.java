package org.amse.bomberman.client.models.gamemodel.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.client.models.listeners.GameStateListener;

/**
 * Class that represents game state model.
 *
 * @author Kirilchuk V.E.
 */
public class GameStateModel {

    private final List<GameStateListener> listeners
            = new CopyOnWriteArrayList<GameStateListener>();

    private volatile boolean ended = false;

    /**
     * Sets state. This method notifies listeners.
     *
     * @param ended state to set.
     */
    public void setEnded(boolean ended) {
        if(this.ended != ended) { //to not spam listeners every time
            this.ended = ended;
            updateListeners();
        }
    }

    /**
     * @return state of game.
     */
    public boolean isEnded() {//don`t need synchronize - volatile is enough here
        return ended;
    }

    private void updateListeners() {
        for(GameStateListener listener : listeners) {
            listener.updateGameState();
        }
    }

    /**
     * Adds listener to this model.
     *
     * @param listener listener to add.
     */
    public void addListener(GameStateListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes listener from this model.
     *
     * @param listener listener to remove.
     */
    public void removeListener(GameStateListener listener) {
        listeners.remove(listener);
    }

    /**
     * Reset this model to initinal state.
     * This method does not notifies listeners.
     */
    public void reset() {
        ended = false;
    }
}
