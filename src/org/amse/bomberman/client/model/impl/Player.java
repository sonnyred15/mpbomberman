package org.amse.bomberman.client.model.impl;

import org.amse.bomberman.client.model.Cell;
import org.amse.bomberman.client.model.IPlayer;
import org.amse.bomberman.util.Constants;

/**
 * @author Mikhail Korovkin
 */
public class Player implements IPlayer{
    private static IPlayer player = null;
    private String name = "Noname";
    private int bombAmount = Constants.PLAYER_DEFAULT_MAX_BOMBS;
    private int life = 0;
    private int bombRadius = 0;
    private Cell myCoord = new Cell(0,0);

    private Player() {
    }
    public static IPlayer getInstance() {
        if (player == null) {
            player = new Player();
        }
        return player;
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
        this.bombRadius = r;
    }
    public int getBombRadius() {
        return this.bombRadius;
    }
}
