package org.amse.bomberman.client.model;

/**
 *
 * @author michail korovkin
 */
public class Player {
    private String name;
    private int bombAmount;
    private int bombRange;
    private int life;
    private int speed;

    public Player(String playerName) {
        name = playerName;
        bombAmount = 2;
        bombRange = 2;
        life = 3;
        //  do 100 with coefficient ?
        speed = 1;
    }
    public int getBombAmount() {
        return bombAmount;
    }
    public int getBombRange() {
        return bombRange;
    }
    public int getLife() {
        return life;
    }
    public int getSpeed() {
        return speed;
    }
    public void incBomb() {
        bombAmount++;
    }
    public void incBombRange() {
        bombRange++;
    }
    public void setLives(int lives) {
        life = lives;
    }
    public void incSpeed() {
        speed++;
    }
}
