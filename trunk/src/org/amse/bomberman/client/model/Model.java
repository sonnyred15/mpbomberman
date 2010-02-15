package org.amse.bomberman.client.model;

import java.util.ArrayList;
import java.util.List;
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
    // here?
    private Player player = new Player("Mavr");
    private List<IView> listener = new ArrayList<IView>();
    // ??? how do it? how start bot Threads?
    private List<Bot> bots = new ArrayList<Bot>();
    private List<Cell> changes = new ArrayList<Cell>();

    // VERY BAD HACK!!! If it pravate, then can not extends BotModel
    protected Model() {
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
    public void setPlayerLives(int lives) {
        player.setLives(lives);
    }
    public int getPlayerLives() {
        return player.getLife();
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
    public void setConnector(IConnector connector) {
        this.connector = connector;
    }
    public IConnector getConnector() {
        return connector;
    }
    public void addListener(IView view) {
        listener.add(view);
    }
    public void removeListener(IView view) {
        listener.remove(view);
    }
    /*public void plantBomb(int number){
        Connector.getInstance().plantBomb();
        map = Connector.getInstance().getMap();
        this.updateListeners();
    }*/
    private void updateListeners() {
        for (IView elem : listener) {
            elem.update();
        }
    }
    public void addBot(Bot botThread) {
        bots.add(botThread);
    }
    public void startBots() {
        for (Bot bot: bots) {
            if (!bot.isAlive()){
                bot.start();
            } 
        }
    }
    public void removeBots() {
        for (Bot bot: bots) {
            bot.kill();
        }
        for (int i = bots.size(); i > 0; i--) {
            bots.remove(bots.get(i-1));
        }
    }
}
