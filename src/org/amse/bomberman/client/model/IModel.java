package org.amse.bomberman.client.model;

import java.util.List;
import org.amse.bomberman.client.view.IView;
/**
 *
 * @author Michail Korovkin
 */
public interface IModel {
    public void setMap(BombMap map);
    public void addListener(IView view);
    public void removeListeners();
    public BombMap getMap();
    public List<Cell> getChanges();
    public void setPlayerLives(int lives);
    public int getPlayerLives();
    public void setPlayerCoord(Cell cell);
    public Cell getPlayerCoord();
    public void setPlayerName(String name);
    public String getPlayerName();
    public void setPlayerBombs(int amount);
    public int getPlayerBombs();
    public void setPlayerRadius(int radius);
    public int getPlayerRadius();
    public boolean isStarted();
    public void setStart(boolean flag);
}
