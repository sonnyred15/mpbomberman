package org.amse.bomberman.client.model;

import java.util.ArrayList;
import java.util.List;
import org.amse.bomberman.client.net.IConnector;
import org.amse.bomberman.client.view.IView;

/**
 *
 * @author michail korovkin
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

    // VERY BAD HACK!!! If it pravate, then can not extends BotModel
    protected Model() {
    }

    public static IModel getInstance() {
        if (model == null) {
            model = new Model();
        }
        return model;
    }
    public void setMap(BombMap map) {
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
