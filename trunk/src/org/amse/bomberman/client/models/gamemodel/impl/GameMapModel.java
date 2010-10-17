package org.amse.bomberman.client.models.gamemodel.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.client.models.gamemodel.*;
import org.amse.bomberman.client.models.listeners.GameMapModelListener;
import org.amse.bomberman.client.net.ServerListener;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.util.impl.ParserImpl;

/**
 *
 * @author Mikhail Korovkin
 */
public class GameMapModel implements ServerListener {

    private final List<GameMapModelListener> listeners
            = new CopyOnWriteArrayList<GameMapModelListener>();
    
    private final List<Cell> changes = new ArrayList<Cell>();

    private volatile GameMap gameMap;

    /**
     * Set BombMap in the model. It modifies list of changes too!!! After setting
     * BombMap it calls @update for all listeners of Model.
     * @param newGameMap new BombMap.
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

    //TODO CLIENT must not be here BAD DESIGN
    public void received(ProtocolMessage<Integer, String> message) {
        int messageId = message.getMessageId();
        List<String> data = message.getData();
        if(messageId == ProtocolConstants.GAME_MAP_INFO_MESSAGE_ID) {
            ParserImpl parser = new ParserImpl();
            setGameMap(parser.parseGameMap(data));//will updateListeners
        } else if(messageId == ProtocolConstants.DO_MOVE_MESSAGE_ID) {
            //ignore answer
        } else if(messageId == ProtocolConstants.PLACE_BOMB_MESSAGE_ID) {
            //ignore answer
        }
    }

    public GameMap getMap() {//don`t need synchronize. volatile is enough here
        return gameMap;
    }

    public List<Cell> getChanges() {
        return changes;
    }

    public void addListener(GameMapModelListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GameMapModelListener listener) {
        listeners.remove(listener);
    }

    private void updateListeners() {
        for(GameMapModelListener listener : listeners) {
            listener.gameMapChanged();
        }
    }

    public void reset() {
        this.gameMap = null;
        this.changes.clear();
    }
}
