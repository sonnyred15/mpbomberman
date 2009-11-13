/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameInit;

/**
 *
 * @author chibis
 */
public class Player {

    private String nickName = "unnamed";
    private int id = 1;
    private int x = 0;
    private int y = 0;
    private int speed = 1;
    private int explRadius = 1;
    private int bombs = 0;
    private int maxBombs = 1;

    public Player(String nickName, int id) {
        this.nickName = nickName;
        this.id = id;
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
}
