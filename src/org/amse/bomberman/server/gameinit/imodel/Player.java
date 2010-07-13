
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit.imodel;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Pair;

//~--- JDK imports ------------------------------------------------------------

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class that represents player. Player is part of Model not part of Game.
 * @author Kirilchuk V.E
 */
public class Player implements MoveableObject {
    //
    private final ScheduledExecutorService timer;

    //
    private int deaths = 0;
    private int kills  = 0;

    //
    private int          id = 1;
    private final String nickName;

    //
    protected int lives       = Constants.PLAYER_DEFAULT_LIVES;
    protected int settedBombs = 0;
    protected int maxBombs    = Constants.PLAYER_DEFAULT_MAX_BOMBS;
    protected int explRadius  = Constants.PLAYER_DEFAULT_BOMB_RADIUS;


    //
    private Pair             position; //TODO maybe must be volatile?

    //
    private final Object BOMBS_LOCK = new Object();
    private DieListener  playerDieListener;

    /**
     * Constructor of player with defined nickName.
     * @param nickName nickName of this player.
     */
    public Player(String nickName, ScheduledExecutorService timer) {
        this.nickName = nickName;
        this.timer    = timer;
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
    public String getInfo() {
        String ret = this.position.getX() + " " +
                     this.position.getY() + " " +
                     this.nickName + " " +
                     this.lives + " " +
                     this.settedBombs + " " +
                     this.maxBombs + " " +
                     this.explRadius;

        return ret;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    /**
     * Checks if this player is alive(if his lives more than zero).
     * @return true if alive, false otherwise.
     */
    public synchronized boolean isAlive() {
        return (this.lives > 0);
    }

    /**
     * Checks if this player can place bomb.
     * @return true if can, false otherwise.
     */
    public boolean canPlaceBomb() {
        synchronized (BOMBS_LOCK) {
            return ((this.settedBombs < this.maxBombs) && this.isAlive());
        }
    }

    /**
     * Returns how much bombes player already setted.
     * <p> This is not the amount of bombes that player placed during all game.
     * This is number of bombs placed simultaneously.
     * @return
     */
    public int getSettedBombsNum() {
        return this.settedBombs;
    }

    /**
     * If this method calls, then number of bombs placed simultaneously
     * must increase.
     */
    public void placedBomb() {
        synchronized (BOMBS_LOCK) {
            this.settedBombs += 1;
        }
    }

    /**
     * If this method calls, then number of bombs placed simultaneously
     * must decrease.
     */
    public void detonatedBomb() {
        synchronized (BOMBS_LOCK) {
            this.settedBombs -= 1;
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
     * This method calls if player was damaged. If he wasnt immortal at this moment
     * he must lost some bonuses and one live. Then he became immortal for some time.
     *
     * If player was immortal then nothing will happen.
     */
    public synchronized void bombed() {
        this.state.bombed(this);
    }

    public synchronized void damagedSomeone() {//TODO maybe model must calculate all such things?
        this.kills += 1;
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
    public synchronized void takeBonus(int bonusID) {
        Bonus bonus = Bonus.valueOf(bonusID);
        bonus.applyBy(this);
    }

    private synchronized void decBonuses() {
        if (explRadius > Constants.PLAYER_DEFAULT_BOMB_RADIUS) {
            explRadius--;
        }

        if (maxBombs > Constants.PLAYER_DEFAULT_MAX_BOMBS) {
            maxBombs--;
        }
    }

    private volatile PlayerState state = PlayerState.NORMAL;

    static enum PlayerState{
        NORMAL {
            @Override
            void bombed(Player player){
                player.lives  -= 1;
                player.deaths += 1;
                player.decBonuses();

                assert (player.lives >= 0);    // here may be synchronization problem

                if (player.lives == 0) {
                    player.playerDieListener.playerDied(player);
                } else {
                    makeImmortal(player);
                }
            }

            private void makeImmortal(final Player player) {
                player.state = PlayerState.IMMORTAL;
                player.timer.schedule(new Runnable() {

                    @Override
                    public void run() {
                        player.state = PlayerState.NORMAL;
                    }
                }, Constants.PLAYER_IMMORTALITY_TIME, TimeUnit.MILLISECONDS);
            }
        },

        IMMORTAL {
            void bombed(Player player){
                ; //do nothing.
            }
        };

        abstract void bombed(Player player);
    }
}
