package org.amse.bomberman.client.models.gamemodel.impl;

import org.amse.bomberman.client.models.gamemodel.Cell;
import org.amse.bomberman.client.models.gamemodel.Player;
import org.amse.bomberman.util.Constants;

/**
 * @author Mikhail Korovkin
 */
public class PlayerImpl implements Player {

    private String name = "Noname";
    private int    life;
    private int    bombAmount;
    private int    settedBombs;
    private int    bombRadius;
    private Cell   myCoord = new Cell(0, 0);

    public PlayerImpl() {
        this.life       = Constants.PLAYER_DEFAULT_LIVES;
        this.bombAmount = Constants.PLAYER_DEFAULT_MAX_BOMBS;
        this.bombRadius = Constants.PLAYER_DEFAULT_BOMB_RADIUS;
        this.settedBombs = 0;
    }

    public void setName(String string) {
        name = string;
    }

    public String getName() {
        return name;
    }

    public int getBombAmount() {
        return bombAmount;
    }

    public void setBombAmount(int amount) {
        bombAmount = amount;
    }

    public int getLife() {
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
}
