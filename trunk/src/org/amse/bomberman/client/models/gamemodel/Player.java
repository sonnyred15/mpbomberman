package org.amse.bomberman.client.models.gamemodel;

/**
 *
 * @author Michail Korovkin
 */
public interface Player {

    void setName(String string);

    String getName();

    void setBombAmount(int amount);

    int getBombAmount();

    void setLives(int lives);

    int getLife();

    void setCoord(Cell cell);

    int getBombRadius();

    void setBombRadius(int radius);

    Cell getCoord();
}
