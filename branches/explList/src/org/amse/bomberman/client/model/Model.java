package org.amse.bomberman.client.model;

import java.util.ArrayList;
import java.util.List;
import org.amse.bomberman.client.model.BombMap.Direction;
import org.amse.bomberman.client.view.IView;

/**
 *
 * @author michail korovkin
 */
public class Model implements IModel{
    private static IModel model= null;
    private BombMap map;
    // here?
    private Player player = new Player("Mavr");
    private List<IView> listener = new ArrayList<IView>();
    // ??? how do it? how start bot Threads?
    private List<Thread> bots = new ArrayList<Thread>();

    private Model() {
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
    public void addBot(Thread botThread) {
        bots.add(botThread);
    }
    public void startBots() {
        for (Thread bot: bots) {
            bot.start();
        }
    }
}
