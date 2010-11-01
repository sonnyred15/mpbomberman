package org.amse.bomberman.client.models.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.client.models.gamemodel.impl.ImmutableCell;
import org.amse.bomberman.client.models.gamemodel.impl.SimpleGameMap;
import org.amse.bomberman.client.models.listeners.GameMapModelListener;

/**
 * Class that represents gameMap model.
 *
 * @author Mikhail Korovkin
 * @author Kirilchuk V.E.
 */
public class GameMapModel {

    private final List<GameMapModelListener> listeners
            = new CopyOnWriteArrayList<GameMapModelListener>();
    
    private final List<ImmutableCell> changes = new ArrayList<ImmutableCell>();

    private volatile SimpleGameMap gameMap;

    /**
     * Sets GameMap in the model. It modifies list of changes too!!!
     * After setting GameMap it notifies all listeners of this model.
     *
     * @param newGameMap new GameMap.
     */
    public synchronized void setGameMap(SimpleGameMap newGameMap) {
        ImmutableCell buf = new ImmutableCell(0, 0);
        changes.clear();
        // if it is not first call of setGameMap()
        if(this.gameMap != null && this.gameMap.getSize() == newGameMap.getSize()) {
            for(int i = 0; i < newGameMap.getSize(); i++) {
                for(int j = 0; j < newGameMap.getSize(); j++) {
                    buf = new ImmutableCell(i, j);
                    if(newGameMap.getValue(buf) != this.gameMap.getValue(buf)) {
                        changes.add(buf);
                    }
                }
            }

            List<ImmutableCell> oldExpl = this.gameMap.getExplosions();
            List<ImmutableCell> newExpl = newGameMap.getExplosions();

            for(ImmutableCell cell : oldExpl) {
                if(!newExpl.contains(cell)) {
                    changes.add(cell);
                }
            }

            for(ImmutableCell cell : newExpl) {
                if(!oldExpl.contains(cell)) {
                    changes.add(cell);
                }
            }
        } else {
            for(int i = 0; i < newGameMap.getSize(); i++) {
                for(int j = 0; j < newGameMap.getSize(); j++) {
                    changes.add(buf);
                }
            }
        }
        this.gameMap = newGameMap;
        updateListeners();
    }

    /**
     * @return last game map.
     */
    public SimpleGameMap getGameMap() {//don`t need synchronize. volatile is enough here
        return gameMap;
    }

    /**
     * @return changes between previous and last gameMap.
     */
    public synchronized List<ImmutableCell> getChanges() {//need synchronize cause changes list is not thread safe.
        return changes;
    }

    /**
     * Adds listener to this model.
     *
     * @param listener listener to add.
     */
    public void addListener(GameMapModelListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes listener from this model.
     *
     * @param listener listener to remove.
     */
    public void removeListener(GameMapModelListener listener) {
        listeners.remove(listener);
    }

    private void updateListeners() {
        for(GameMapModelListener listener : listeners) {
            listener.gameMapChanged();
        }
    }

    /**
     * Resetes this model to initial state.
     * This method does not notifies listeners.
     */
    public void reset() {
        this.gameMap = null;
        this.changes.clear();
    }
}
