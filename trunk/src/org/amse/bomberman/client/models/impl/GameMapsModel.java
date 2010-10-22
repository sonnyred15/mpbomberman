package org.amse.bomberman.client.models.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.client.models.listeners.GameMapsModelListener;

/**
 *
 * @author Kirilchuk V.E.
 */
public class GameMapsModel {

    private final List<GameMapsModelListener> listeners
            = new CopyOnWriteArrayList<GameMapsModelListener>();

    private volatile List<String> gameMaps = new ArrayList<String>(0);

    public List<String> getGameMaps() {//don`t need syncronized - volatile is enough here
        return gameMaps;
    }

    public void setGameMapsList(List<String> data) {
        gameMaps = data;
        updateListeners();
    }

    public void noGameMaps() {
        updateListeners("No maps on server was founded.");
    }

    public void addListener(GameMapsModelListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GameMapsModelListener listener) {
        listeners.remove(listener);
    }

    private void updateListeners() {
        for (GameMapsModelListener listener : listeners) {
            listener.updateGameMaps();
        }
    }

    private void updateListeners(String string) {
        for (GameMapsModelListener listener : listeners) {
            listener.gameMapsError(string);
        }
    }
}
