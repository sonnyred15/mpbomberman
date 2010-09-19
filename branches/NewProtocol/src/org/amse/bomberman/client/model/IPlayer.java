package org.amse.bomberman.client.model;
/**
 *
 * @author Michail Korovkin
 */
public interface IPlayer {
    public void setName(String string);
    public String getName();
    public void setBombAmount(int amount);
    public int getBombAmount();
    public void setLives(int lives);
    public int getLife();
    public void setCoord(Cell cell);
    public int getBombRadius();
    public void setBombRadius(int radius);
    public Cell getCoord();
}
