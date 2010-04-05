/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit;

import org.amse.bomberman.util.Pair;
import org.amse.bomberman.util.Constants;

/**
 *
 * @author Kirilchuk V.E
 */
public class Player implements MoveableObject{

    private DieListener playerDieListener;

    private String nickName = "unnamed";
    private int lives = 3;
    private int id = 1;
    
    private Pair position;
    
    private int explRadius = Constants.PLAYER_DEFAULT_BOMB_RADIUS;
    private int bombs = 0;
    private final Object BOMBS_LOCK = new Object();
    private int maxBombs = Constants.PLAYER_DEFAULT_MAX_BOMBS;

    public Player() {
    }

    public Player(String nickName) {
        this.nickName = nickName;       
    }

    public void setDieListener(DieListener gameDieListener){
        this.playerDieListener = gameDieListener;
    }

    public String getInfo() {
        String ret = this.position.getX() + " " +
                this.position.getY() + " " +
                this.nickName + " " +
                this.lives + " " +
                this.bombs + " " +
                this.maxBombs;
        return ret;
    }

    public boolean isAlive() {
        return (this.lives > 0);
    }

    public boolean canPlaceBomb() {//CHECK THIS is deadLock possible by isAlive lock?
        synchronized (BOMBS_LOCK) {
            return ((this.bombs < this.maxBombs) && isAlive());
        }
    }

    public int getBombs() {
        return bombs;
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

    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public Pair getPosition(){
        return position;
    }

    public void setPosition(Pair position){
        this.position = position;
    }

    public synchronized void bombed() { //synchronized(player)
        this.lives -= 1;
        this.decBonuses();
        if (this.lives <= 0){//CHECK THIS
            playerDieListener.playerDied(this);
        }
    }

    public int getRadius() {
        return this.explRadius;
    }

    public String getNickName() {
        return nickName;
    }
    public void takeBonus(int bonusID) {
        switch (bonusID) {
            case Constants.MAP_BONUS_LIFE : {
                this.lives++;
                break;
            }
            case Constants.MAP_BONUS_BOMB_COUNT : {
                this.maxBombs++;
                break;
            }
            case Constants.MAP_BONUS_BOMB_RADIUS : {
                this.explRadius++;
                break;
            }
            default: {
                throw new IllegalArgumentException("Such bonus is not supported." +
                        " Bonus = " + bonusID);
            }
        }
    }
    
    private void decBonuses() {
        if (explRadius > Constants.PLAYER_DEFAULT_BOMB_RADIUS) {
            explRadius--;
        }
        if (maxBombs > Constants.PLAYER_DEFAULT_MAX_BOMBS) {
            maxBombs--;
        }
    }
}
