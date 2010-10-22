package org.amse.bomberman.client.models.gamemodel.impl;

import org.amse.bomberman.client.models.gamemodel.Cell;
import org.amse.bomberman.client.models.gamemodel.Player;
import org.amse.bomberman.util.Constants;

/**
 * @author Mikhail Korovkin
 */
public class PlayerImpl implements Player {

    private int    life;
    private int    bombAmount;
    private int    settedBombs;
    private int    bombRadius;
    private Cell   myCoord;

    public PlayerImpl() {
        reset();
    }

    public int getBombAmount() {
        return bombAmount;
    }

    public void setBombAmount(int amount) {
        bombAmount = amount;
    }

    public int getLifes() {
        return life;
    }

    public void setLives(int lives) {
        life = lives;
    }

    public void setCoord(Cell cell) {
        myCoord = cell;
    }

    public Cell getCoord() {
        return myCoord;
    }

    public void setBombRadius(int r) {
        bombRadius = r;
    }

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
        this.myCoord = new Cell(0, 0);
    }
}
