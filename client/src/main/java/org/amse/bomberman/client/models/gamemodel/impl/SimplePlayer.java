package org.amse.bomberman.client.models.gamemodel.impl;

import org.amse.bomberman.client.models.gamemodel.Player;
import org.amse.bomberman.util.Constants;

/**
 * @author Mikhail Korovkin
 */
public class SimplePlayer implements Player {

    private int    life;
    private int    bombAmount;
    private int    settedBombs;
    private int    bombRadius;
    private ImmutableCell   myCoord;

    public SimplePlayer() {
        reset();
    }

    @Override
    public int getBombAmount() {
        return bombAmount;
    }

    @Override
    public void setBombAmount(int amount) {
        bombAmount = amount;
    }

    @Override
    public int getLifes() {
        return life;
    }

    @Override
    public void setLives(int lives) {
        life = lives;
    }

    @Override
    public void setCoord(ImmutableCell cell) {
        myCoord = cell;
    }

    @Override
    public ImmutableCell getCoord() {
        return myCoord;
    }

    @Override
    public void setBombRadius(int r) {
        bombRadius = r;
    }

    @Override
    public int getBombRadius() {
        return bombRadius;
    }

    public int getSettedBombs() {
        return settedBombs;
    }

    public void setSettedBombs(int amount) {
        settedBombs = amount;
    }

    /**
     * Resets player to initial state.
     * Sets parameters to defaults.
     * <p> Just creating new Player would do same thing but will
     * produce new object. So, you may choose between creating new object and
     * just reseting state of existing player.
     */
    public void reset() {
        this.life        = Constants.PLAYER_DEFAULT_LIVES;
        this.bombAmount  = Constants.PLAYER_DEFAULT_MAX_BOMBS;
        this.bombRadius  = Constants.PLAYER_DEFAULT_BOMB_RADIUS;
        this.settedBombs = 0;
        this.myCoord = new ImmutableCell(0, 0);
    }
}
