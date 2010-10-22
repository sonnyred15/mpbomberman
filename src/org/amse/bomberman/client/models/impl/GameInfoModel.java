package org.amse.bomberman.client.models.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.client.models.listeners.GameInfoModelListener;

/**
 *
 * @author Kirilchuk V.E.
 */
public class GameInfoModel {

    private final List<GameInfoModelListener> listeners 
            = new CopyOnWriteArrayList<GameInfoModelListener>();

    private volatile List<String> gameInfo = new ArrayList<String>(0);

    public List<String> getGameInfo() {//don`t need sychronized - volatile is enough here
        return gameInfo;
    }

    public void setGameInfo(List<String> data) {
        this.gameInfo = data;
        updateListeners();
    }

    public void gameInfoError(String error) {
        updateListeners(error);
    }

    public void addListener(GameInfoModelListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GameInfoModelListener listener) {
        listeners.remove(listener);
    }

    private void updateListeners() {
        for (GameInfoModelListener listener : listeners) {
            listener.updateGameInfo();
        }
    }

    private void updateListeners(String string) {
        for (GameInfoModelListener listener : listeners) {
            listener.gameInfoError(string);
        }
    }
}
