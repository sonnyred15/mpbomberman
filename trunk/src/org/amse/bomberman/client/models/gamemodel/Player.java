package org.amse.bomberman.client.models.gamemodel;

import org.amse.bomberman.client.models.gamemodel.impl.ImmutableCell;

/**
 *
 * @author Michail Korovkin
 */
public interface Player {

    void setBombAmount(int amount);

    int getBombAmount();

    void setLives(int lives);

    int getLifes();

    void setCoord(ImmutableCell cell);

    int getBombRadius();

    void setBombRadius(int radius);

    ImmutableCell getCoord();
}
