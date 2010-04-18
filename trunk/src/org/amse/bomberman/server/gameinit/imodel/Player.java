
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit.imodel;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Pair;

/**
 * Class that represents player. Player is part of Model not part of Game.
 * @author Kirilchuk V.E
 */
public class Player implements MoveableObject {
    private int          bombs = 0;
    private int          id = 1;
    private int          lives = 3;
    private String       nickName = "unnamed";
    private int          maxBombs = Constants.PLAYER_DEFAULT_MAX_BOMBS;
    private int          explRadius = Constants.PLAYER_DEFAULT_BOMB_RADIUS;
    private final Object BOMBS_LOCK = new Object();
    private DieListener  playerDieListener;
    private Pair         position;

    /**
     * Default constructor. Empty.
     */
    public Player() {}

    /**
     * Constructor of player with defined nickName.
     * @param nickName nickName of this player.
     */
    public Player(String nickName) {
        this.nickName = nickName;
    }

    /**
     * Setting dieListener.
     * @see DieListener
     * @param gameDieListener listener to set.
     */
    public void setDieListener(DieListener gameDieListener) {
        this.playerDieListener = gameDieListener;
    }

    /**
     * Returns info about player state in next format:
     * <p> positionX positionY nickName lives bombs maxBombs
     * @return info about player state.
     */
    public String getInfo() {    // TODO ADD BONUSES AND OTHER INFO!
        String ret = this.position.getX() + " " + this.position.getY() + " " +
                     this.nickName + " " + this.lives + " " + this.bombs +
                     " " + this.maxBombs;

        return ret;
    }

    /**
     * Checks if this player is alive(if his lives more than zero).
     * @return true if alive, false otherwise.
     */
    public boolean isAlive() {
        return (this.lives > 0);
    }

    /**
     * Checks if this player can place bomb.
     * @return true if can, false otherwise.
     */
    public boolean canPlaceBomb() {    // CHECK THIS is deadLock possible by isAlive lock?
        synchronized (BOMBS_LOCK) {
            return ((this.bombs < this.maxBombs) && isAlive());
        }
    }

    /**
     * Returns how much bombes player already setted.
     * <p> This is not the amount of bombes that player placed during all game.
     * This is number of bombs placed simultaneously.
     * @return
     */
    public int getBombs() {
        return bombs;
    }

    /**
     * If this method calls, then number of bombs placed simultaneously
     * must increase.
     */
    public void placedBomb() {
        synchronized (BOMBS_LOCK) {
            this.bombs += 1;
        }
    }

    /**
     * If this method calls, then number of bombs placed simultaneously
     * must decrease.
     */
    public void detonatedBomb() {
        synchronized (BOMBS_LOCK) {
            this.bombs -= 1;
        }
    }

    /**
     * Setting ID of player.
     * @param id id to set.
     */
    public void setID(int id) {
        this.id = id;
    }

    /**
     * Returns ID of this player.
     * @return ID of this player.
     */
    public int getID() {
        return this.id;
    }

    /**
     * Returnes position of this player.
     * @return position of this player.
     */
    public Pair getPosition() {
        return position;
    }

    /**
     * Setting new position to this player.
     * @param position position to set.
     */
    public void setPosition(Pair position) {
        this.position = position;
    }

    /**
     * This method calls if player was damaged. So he must lost some bonuses
     * and one live.
     */
    public synchronized void bombed() {    // TODO synchronized(player)
        this.lives -= 1;
        this.decBonuses();

        if (this.lives <= 0) {    // CHECK THIS
            playerDieListener.playerDied(this);
        }
    }

    /**
     * Returns current explosion radius parameter.
     * @return current explosion radius parameter.
     */
    public int getRadius() {
        return this.explRadius;
    }

    /**
     * Returns nickName of this player.
     * @return nickName of this player.
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * If this method was called, then depending on bonus
     * some player pareametres must increase or change.
     * @param bonusID ID of taked bonus.
     */
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

            default : {
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
