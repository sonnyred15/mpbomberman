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
    public void removeListener(IView view);
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
    //needed from controller
    public void setGamesList(List<String> gamesList);
    public void setCreated(boolean created);
    public void setJoined(boolean joined);
    public void setStarted(boolean started);
    public void setLeavedGame(boolean leaved);
    public void downloadedGameMap(List<String> gameMap);
    public void setGameMapsList(List<String> gameMapsList);
    public void setBotAdded(boolean wasAdded);
    public void setGameInfo(List<String> gameInfo);
    public void setNewChatMessages(List<String> newChatMessages);
}
