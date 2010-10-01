package org.amse.bomberman.client.model.impl;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.amse.bomberman.client.model.*;
import org.amse.bomberman.client.view.IView;
import org.amse.bomberman.client.net.RequestResultListener;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.util.impl.Parser;

/**
 *
 * @author Mikhail Korovkin
 */
public class Model implements IModel, RequestResultListener{
    private static IModel model= null;
    private BombMap map;
    private IPlayer player = Player.getInstance();
    private List<IView> listener = new ArrayList<IView>();
    private List<Cell> changes = new ArrayList<Cell>();
    private List<String> history = new ArrayList<String>();
    private List<String> results = new ArrayList<String>();
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

    public synchronized void received(ProtocolMessage<Integer, String> response) {
        int messageId = response.getMessageId();
        List<String> data = response.getData();
        if (messageId == ProtocolConstants.GAME_MAP_INFO_MESSAGE_ID) {
            if (!data.get(0).equals("Not joined to any game.")) {
                Parser parser = new Parser();
                this.setMap(parser.parse(data));
            } else {
                escapeGame();
            }
        } else if (messageId == ProtocolConstants.DO_MOVE_MESSAGE_ID) {
            if (data.get(0).equals("false")) {                
            } else {
                if (data.get(0).equals("Not joined to any game.")) {
                    escapeGame();
                }
            }
        } else if (messageId == ProtocolConstants.PLACE_BOMB_MESSAGE_ID) {
            if (!data.get(0).equals("Placed.")) {
                escapeGame();
            }
        } else if (messageId == ProtocolConstants.LEAVE_MESSAGE_ID) {
            if (data.get(0).equals("Disconnected.")) {
                escapeGame();
            }
        } else if (messageId == ProtocolConstants.NOTIFICATION_MESSAGE_ID) {
            if (data.get(0).equals(ProtocolConstants.MESSAGE_GAME_KICK)) {
                JOptionPane.showMessageDialog(null, "Host is escaped from game!\n"
                       , "Game ended.", JOptionPane.INFORMATION_MESSAGE);
                escapeGame();
            }
        } else if (messageId == ProtocolConstants.CHAT_GET_MESSAGE_ID) {//TODO hardcoded string
            if (!data.get(0).equals("No new messages.")) {
                history.addAll(data);
                this.updateListeners();
            }
        } else if (messageId == ProtocolConstants.END_RESULTS_MESSAGE_ID) {
            results = data;
            updateListeners();
        }
    }
    public BombMap getMap() {
        return map;
    }
    public List<Cell> getChanges() {
        return changes;
    }
    public List<String> getHistory() {
        return history;
    }
    public List<String> getResults() {
        return results;
    }
    public void addListener(IView view) {
        listener.add(view);
    }
    public void removeListeners() {
        listener.clear();
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
    public void setPlayerRadius(int radius) {
        player.setBombRadius(radius);
    }
    public int getPlayerRadius() {
        return player.getBombRadius();
    }
    public synchronized boolean isStarted() {
        return isStarted;
    }
    public synchronized void setStart(boolean bool) {
        isStarted = bool;
    }
    private synchronized void escapeGame() {
        isStarted = false;
        updateListeners();
        map = null;
        listener.clear();
        changes.clear();
        results.clear();
        history.clear();
    }
}
