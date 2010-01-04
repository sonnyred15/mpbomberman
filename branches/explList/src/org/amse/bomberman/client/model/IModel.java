package org.amse.bomberman.client.model;
import java.util.List;
import org.amse.bomberman.client.net.IConnector;
import org.amse.bomberman.client.view.IView;
/**
 *
 * @author michail korovkin
 */
public interface IModel {
    //public boolean movePlayer(int number, Direction dir);
    public void setMap(BombMap map);
    public void addListener(IView view);
    public void removeListener(IView view);
    public BombMap getMap();
    public List<Cell> getChanges();
    public void setPlayerLives(int lives);
    public int getPlayerLives();
    //public void plantBomb(int number);
    // how do it??? who must start bot thread???
    public void addBot(Bot botThread);
    public void startBots();
    public void removeBots();
    public void setConnector(IConnector connector);
    public IConnector getConnector();
}
