package org.amse.bomberman.client.models.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.client.models.listeners.GamesModelListener;

/**
 *
 * @author Kirilchuk V.E.
 */
public class GamesModel {

    private final List<GamesModelListener> listeners 
            = new CopyOnWriteArrayList<GamesModelListener>();

    private volatile List<String> games = new ArrayList<String>();

    public void setGames(List<String> data) {
        games = data;
        updateListeners();
    }

    public List<String> getGames() {
        return games;
    }

    public void addListener(GamesModelListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GamesModelListener listener) {
        listeners.remove(listener);
    }

    private void updateListeners() {
        for (GamesModelListener listener : listeners) {
            listener.updateGamesList();
        }
    }
}
