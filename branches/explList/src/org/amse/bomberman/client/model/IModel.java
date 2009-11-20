package org.amse.bomberman.client.model;
import org.amse.bomberman.client.model.BombMap.Direction;
import org.amse.bomberman.client.view.IView;
/**
 *
 * @author maverick
 */
public interface IModel {
    //public boolean movePlayer(int number, Direction dir);
    public void addListener(IView view);
    public void removeListener(IView view);
    public BombMap getMap();
    public void setPlayerLives(int lives);
    public int getPlayerLives();
    //public void plantBomb(int number);
}
