package org.amse.bomberman.client.model.impl;

import java.util.ArrayList;
import java.util.List;
import org.amse.bomberman.client.model.*;
import org.amse.bomberman.client.net.IConnector;
import org.amse.bomberman.client.view.IView;

/**
 *
 * @author Michail Korovkin
 */
public class Model implements IModel{
    private static IModel model= null;
    private IConnector connector;
    private BombMap map;
    private IPlayer player = Player.getInstance();
    private List<IView> listener = new ArrayList<IView>();
    private List<Cell> changes = new ArrayList<Cell>();

    private Model() {
    }
    public static IModel getInstance() {
        if (model == null) {
            model = new Model();
        }
        return model;
    }
    /**
     * Set BombMap in the model. It modifies list of changes too!!! After setting
     * BombMap it calls @update for all listeners of Model.
     * @param map new BombMap.
     */
    public void setMap(BombMap map) {
        Cell buf = new Cell(0,0);
        changes.clear();
        // if it is not first call of @setMap
        if (this.map != null) {
            for (int i = 0; i < map.getSize(); i++) {
                for (int j = 0; j < map.getSize(); j++) {
                    buf = new Cell(i, j);
                    if (map.getValue(buf) != this.map.getValue(buf)) {
                        changes.add(buf);
                    }
                }
            }
            List<Cell> oldExpl = this.map.getExplosions();
            List<Cell> newExpl = map.getExplosions();
            List<Cell> changeExpl = new ArrayList<Cell>();
            for (Cell cell : oldExpl) {
                if (!newExpl.contains(cell)) {
                    changes.add(cell);
                }
            }
            for (Cell cell : newExpl) {
                if (!oldExpl.contains(cell)) {
                    changes.add(cell);
                }
            }
        }
        this.map = map;
        updateListeners();
    }
    // only for player from this connector
    /*public boolean movePlayer(int number, Direction dir) {
        if (Connector.getInstance().doMove(dir)) {
            map = Connector.getInstance().getMap();
            this.updateListeners();
            return true;
        } else return false;
    }*/
    public BombMap getMap() {
        return map;
    }
    public List<Cell> getChanges() {
        return changes;
    }
    public void addListener(IView view) {
        listener.add(view);
    }
    public void removeListener(IView view) {
        listener.remove(view);
    }
    private void updateListeners() {
        for (IView elem : listener) {
            elem.update();
        }
    }
    public void setPlayerLives(int lives) {
        player.setLives(lives);
    }
    public int getPlayerLives() {
        return player.getLife();
    }
    public void setPlayerCoord(Cell cell) {
        player.setCoord(cell);
    }
    public Cell getPlayerCoord() {
        return player.getCoord();
    }
    public void setPlayerName(String name) {
        player.setName(name);
    }
    public String getPlayerName() {
        return player.getName();
    }
    public void setPlayerBombs(int amount) {
        player.setBombAmount(amount);
    }
    public int getPlayerBombs() {
        return player.getBombAmount();
    }

    ///////////////////////////////////////
    //////////Needed by controller/////////
    ///////////////////////////////////////

    private List<String> gamesList = new ArrayList<String>();

    public void setGamesList(List<String> gamesList) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setCreated(boolean created) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setJoined(boolean joined) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setStarted(boolean started) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setLeavedGame(boolean leaved) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void downloadedGameMap(List<String> gameMap) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setGameMapsList(List<String> gameMapsList) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setBotAdded(boolean wasAdded) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setGameInfo(List<String> gameInfo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setNewChatMessages(List<String> newChatMessages) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}