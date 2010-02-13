/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit;

/**
 *
 * @author Kirilchuk V.E
 */
public class Player {

    private DieListener playerDieListener;

    private String nickName = "unnamed";
    private int lives = 3;
    private int id = 1;
    private int x = 0;
    private int y = 0;    
    private int explRadius = 2; //for better testing :))
    private int bombs = 0;
    private final Object BOMBS_LOCK = new Object();
    private int maxBombs = 4; //for better testing :))

    public Player(String nickName, int id) {
        this.nickName = nickName;
        this.id = id;
    }

    public void setDieListener(DieListener gameDieListener){
        this.playerDieListener = gameDieListener;
    }

    public String getInfo() {
        String ret = this.x + " " +
                this.y + " " +
                this.nickName + " " +
                this.lives + " " +
                this.bombs + " " +
                this.maxBombs;
        return ret;
    }

    public synchronized boolean isAlive() {
        return (this.lives > 0);
    }

    public boolean canPlaceBomb() {
        synchronized (BOMBS_LOCK) {
            return ((this.bombs < this.maxBombs) && isAlive());
        }
    }

    public void placedBomb() {
        synchronized (BOMBS_LOCK) {
            this.bombs += 1;
        }
    }

    public void detonatedBomb() {
        synchronized (BOMBS_LOCK) {
            this.bombs -= 1;
        }
    }

    @Deprecated
    public void takedBonus(int bonus){//Bonuses must be enum!
        switch (bonus){
            case 1:{
                this.lives+=1;
                break;
            }
            default:{
                throw new IllegalArgumentException("Such bonus is not supported." +
                        " Bonus = " + bonus);
            }
        }
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

    public synchronized void bombed() { //synchronized(player)
        this.lives -= 1;
        if (this.lives <= 0){//CHECK THIS
            playerDieListener.playerDied(this);
        }
    }

    public int getRadius() {
        return this.explRadius;
    }
}
