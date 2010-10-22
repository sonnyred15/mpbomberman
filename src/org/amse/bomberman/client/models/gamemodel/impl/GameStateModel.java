package org.amse.bomberman.client.models.gamemodel.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.client.models.listeners.GameStateListener;

/**
 *
 * @author Kirilchuk V.E.
 */
public class GameStateModel {

    private final List<GameStateListener> listeners
            = new CopyOnWriteArrayList<GameStateListener>();

    private volatile boolean ended = false;//TODO CLIENT when it must be reset?

    public void setEnded(boolean ended) {
        if(this.ended != ended) { //to not spam listeners every time
            this.ended = ended;
            updateListeners();
        }
    }

    public boolean isEnded() {//don`t need synchronize - volatile is enough here
        return ended;
    }

    private void updateListeners() {
        for(GameStateListener listener : listeners) {
            listener.updateGameState();
        }
    }

    public void addListener(GameStateListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GameStateListener listener) {
        listeners.remove(listener);
    }
}
