package org.amse.bomberman.client.models.gamemodel.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.client.models.gamemodel.Cell;
import org.amse.bomberman.client.models.gamemodel.GameMap;
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
    
    private final List<Cell> changes = new ArrayList<Cell>();

    private volatile GameMap gameMap;

    /**
     * Sets GameMap in the model. It modifies list of changes too!!!
     * After setting GameMap it notifies all listeners of this model.
     *
     * @param newGameMap new GameMap.
     */
    public synchronized void setGameMap(GameMap newGameMap) {
        Cell buf = new Cell(0, 0);
        changes.clear();
        // if it is not first call of setGameMap()
        if(this.gameMap != null && this.gameMap.getSize() == newGameMap.getSize()) {
            for(int i = 0; i < newGameMap.getSize(); i++) {
                for(int j = 0; j < newGameMap.getSize(); j++) {
                    buf = new Cell(i, j);
                    if(newGameMap.getValue(buf) != this.gameMap.getValue(buf)) {
                        changes.add(buf);
                    }
                }
            }

            List<Cell> oldExpl = this.gameMap.getExplosions();
            List<Cell> newExpl = newGameMap.getExplosions();

            for(Cell cell : oldExpl) {
                if(!newExpl.contains(cell)) {
                    changes.add(cell);
                }
            }

            for(Cell cell : newExpl) {
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
    public GameMap getGameMap() {//don`t need synchronize. volatile is enough here
        return gameMap;
    }

    /**
     * @return changes between previous and last gameMap.
     */
    public synchronized List<Cell> getChanges() {//need synchronize cause changes list is not thread safe.
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
