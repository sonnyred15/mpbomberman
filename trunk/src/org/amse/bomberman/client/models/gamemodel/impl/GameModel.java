package org.amse.bomberman.client.models.gamemodel.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.client.models.gamemodel.*;
import org.amse.bomberman.client.models.listeners.GameModelListener;
import org.amse.bomberman.client.net.ServerListener;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.util.impl.Parser;

/**
 *
 * @author Mikhail Korovkin
 */
public class GameModel implements ServerListener {

    private final List<GameModelListener> listeners
            = new CopyOnWriteArrayList<GameModelListener>();
    
    private Player    player;
    private List<Cell> changes = new ArrayList<Cell>();//changes must not be calculated here

    private volatile GameMap gameMap;
    private volatile boolean isStarted = false;
    private volatile boolean isEnded = false;

    public GameModel() {
        this.player = new PlayerImpl();
    }

    /**
     * Set BombMap in the model. It modifies list of changes too!!! After setting
     * BombMap it calls @update for all listeners of Model.
     * @param newGameMap new BombMap.
     */
    public synchronized void setGameMap(GameMap newGameMap) {
        Cell buf = new Cell(0, 0);
        changes.clear();
        // if it is not first call of @setMap
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
            List<Cell> changeExpl = new ArrayList<Cell>();

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
    public synchronized void received(ProtocolMessage<Integer, String> response) {
        int messageId = response.getMessageId();
        List<String> data = response.getData();
        if(messageId == ProtocolConstants.GAME_MAP_INFO_MESSAGE_ID) {
                Parser parser = new Parser();
                setGameMap(parser.parse(data));//will updateListeners
        } else if(messageId == ProtocolConstants.DO_MOVE_MESSAGE_ID) {//ignore answer
        } else if(messageId == ProtocolConstants.PLACE_BOMB_MESSAGE_ID) {//ignore answer
        } else if(messageId == ProtocolConstants.LEAVE_MESSAGE_ID) {
            setEnded(true);
        } else if(messageId == ProtocolConstants.GAME_TERMINATED_NOTIFY_ID) {
            if(data.get(0).equals(ProtocolConstants.MESSAGE_GAME_KICK)) {
                setEnded(true);
                updateListeners("Host is escaped from game!\n Game terminated.");
            }        
        } else if(messageId == ProtocolConstants.END_RESULTS_MESSAGE_ID) {
            setEnded(true);
        }
    }

    public GameMap getMap() {//don`t need synchronize. volatile is enough here
        return gameMap;
    }

    public List<Cell> getChanges() {
        return changes;
    }

    public void addListener(GameModelListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GameModelListener listener) {
        listeners.remove(listener);
    }

    private void updateListeners() {
        for(GameModelListener listener : listeners) {
            listener.gameModelChanged();
        }
    }

    private void updateListeners(String error) {
        for(GameModelListener listener : listeners) {
            listener.gameModelErrot(error);
        }
    }

    public void setPlayerLives(int lives) {//TODO CLIENT SERVER split game map info and player info
        player.setLives(lives);
        updateListeners();
    }

    public int getPlayerLives() {
        return player.getLife();
    }

    public void setPlayerCoord(Cell cell) {//TODO CLIENT SERVER split game map info and player info
        player.setCoord(cell);
        updateListeners();
    }

    public Cell getPlayerCoord() {
        return player.getCoord();
    }

    public void setPlayerName(String name) {//TODO CLIENT SERVER split game map info and player info
        player.setName(name);
        updateListeners();
    }

    public String getPlayerName() {
        return player.getName();
    }

    public void setPlayerBombs(int amount) {//TODO CLIENT SERVER split game map info and player info
        player.setBombAmount(amount);
        updateListeners();
    }

    public int getPlayerBombs() {
        return player.getBombAmount();
    }

    public void setPlayerRadius(int radius) {//TODO CLIENT SERVER split game map info and player info
        player.setBombRadius(radius);
        updateListeners();
    }

    public int getPlayerRadius() {
        return player.getBombRadius();
    }

    public boolean isStarted() {//don`t need synchronize. volatile is enough here
        return isStarted;
    }

    public void setStart(boolean bool) {//don`t need synchronize. volatile is enough here
        isStarted = bool;
        updateListeners();
    }

    public void setEnded(boolean bool) {//don`t need synchronize. volatile is enough here
        isEnded = bool;
        updateListeners();
    }

    public boolean isEnded() {//don`t need synchronize. volatile is enough here
        return isEnded;
    }

    public void reset() {
        this.player = new PlayerImpl();
        this.gameMap = null;
        this.isStarted = false;
        this.isEnded = false;
        this.changes.clear();
    }
}
