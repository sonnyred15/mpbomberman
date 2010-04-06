package org.amse.bomberman.client.model.impl;

import java.util.ArrayList;
import java.util.List;
import org.amse.bomberman.client.model.*;
import org.amse.bomberman.client.net.IConnector;
import org.amse.bomberman.client.view.IView;
import org.amse.bomberman.client.view.mywizard.RequestResultListener;
import org.amse.bomberman.util.ProtocolConstants;
import org.amse.bomberman.util.impl.Parser;

/**
 *
 * @author Michail Korovkin
 */
public class Model implements IModel, RequestResultListener{
    private static IModel model= null;
    private IConnector connector;
    private BombMap map;
    private IPlayer player = Player.getInstance();
    private List<IView> listener = new ArrayList<IView>();
    private List<Cell> changes = new ArrayList<Cell>();
    private volatile boolean isStarted = false;

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
    public synchronized void setMap(BombMap map) {
        Cell buf = new Cell(0,0);
        changes.clear();
        // if it is not first call of @setMap
        if (this.map != null && this.map.getSize() == map.getSize()) {
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
        } else {
            for (int i = 0; i < map.getSize(); i++) {
                for (int j = 0; j < map.getSize(); j++) {
                    changes.add(buf);
                }
            }
        }
        this.map = map;
        updateListeners();
    }

    public synchronized void received(List<String> list) {
        String command = list.get(0);
        list.remove(0);
        if (command.equals(ProtocolConstants.CAPTION_GAME_MAP_INFO)) {
            if (!list.get(0).equals("Not joined to any game.")) {
                Parser parser = new Parser();
                this.setMap(parser.parse(list));
            } else {
                escapeGame();
            }
        }
        if (command.equals(ProtocolConstants.CAPTION_DO_MOVE)) {
            if (list.get(0).equals("false")) {
                //System.out.println("You try to do move uncorrectly.");
            } else {
                if (list.get(0).equals("Not joined to any game.")) {
                    escapeGame();
                }
            }
        }
        if (command.equals(ProtocolConstants.CAPTION_GAME_STATUS_INFO)) {
            if (list.get(0).equals("started.")) {
                isStarted = true;
            } else {
                escapeGame();
            }
        }
        if (command.equals(ProtocolConstants.CAPTION_LEAVE_GAME_INFO)) {
            if (list.get(0).equals("Disconnected.")) {
                escapeGame();
            } else {
                // TO DO
            }
        }
    }
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
        for (int i = 0; i < listener.size(); i++) {
            listener.get(i).update();
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
    public synchronized boolean isStarted() {
        return isStarted;
    }
    public synchronized void setStart(boolean bool) {
        isStarted = bool;
    }
    private synchronized void escapeGame() {
        isStarted = false;
        map = null;
        changes.clear();
        updateListeners();
        listener.clear();
    }
}
