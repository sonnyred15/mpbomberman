/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit;

/**
 *
 * @author chibis
 */
public class Player {

    private String nickName = "unnamed";
    private int lives = 3;
    private int id = 1;
    private int x = 0;
    private int y = 0;
    private int speed = 1; // still not used.
    private int explRadius = 2; //for better testing :))
    private int bombs = 0;
    private int maxBombs = 2; //for better testing :))

    public Player(String nickName, int id) {
        this.nickName = nickName;
        this.id = id;
    }

    public boolean isAlive() {
        return (this.lives > 0);
    }

    public boolean canPlaceBomb() {
        return ((this.bombs < this.maxBombs) && isAlive());
    }

    public void placedBomb() {
        this.bombs += 1;
    }

    public void detonatedBomd() {
        this.bombs -= 1;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void bombed() {
        this.lives -= 1;
    }

    public int getRadius() {
        return this.explRadius;
    }
}
