package org.amse.bomberman.server.gameservice.models.impl;

import org.amse.bomberman.server.gameservice.gamemap.impl.GameMap;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Pair;
import org.amse.bomberman.server.gameservice.models.DieListener;
import org.amse.bomberman.server.gameservice.gamemap.impl.MoveableGameMapObject;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import org.amse.bomberman.util.structs.VolatileIntegerHolder;

/**
 * Class that represents player. Player is part of Model not part of Game.
 * This class is thread safe.
 * 
 * @author Kirilchuk V.E
 */
public class ModelPlayer implements MoveableGameMapObject {
    //
    private final ScheduledExecutorService timer;
    private final String nickName;

    //
    private final VolatileIntegerHolder lives
            = new VolatileIntegerHolder(Constants.PLAYER_DEFAULT_LIVES);

    private final VolatileIntegerHolder settedBombs
            = new VolatileIntegerHolder(0);

    private final VolatileIntegerHolder maxBombs
            = new VolatileIntegerHolder(Constants.PLAYER_DEFAULT_MAX_BOMBS);
    
    private final VolatileIntegerHolder explRadius
            = new VolatileIntegerHolder(Constants.PLAYER_DEFAULT_BOMB_RADIUS);

    //
    private final    Pair        position;
    private volatile PlayerState state = PlayerState.NORMAL;
    private volatile int         id = 1;

    //
    private DieListener  playerDieListener;

    /**
     * Constructor of player with defined nickName.
     * @param nickName nickName of this player.
     */
    public ModelPlayer(String nickName, ScheduledExecutorService timer) {
        this.nickName = nickName;
        this.timer    = timer;
        this.position = new Pair();
    }

    /**
     * Setting dieListener.
     * @see DieListener
     * @param listener listener to set.
     */
    public void setDieListener(DieListener listener) {
        playerDieListener = listener;
    }

    /**
     * Checks if this player is alive(if his lives more than zero).
     * @return true if alive, false otherwise.
     */
    public synchronized boolean isAlive() {
        return (lives.getValue() > 0);
    }

    /**
     * Checks if this player can place bomb.
     * @return true if can, false otherwise.
     */
    public synchronized boolean canPlaceBomb() {
        return ((settedBombs.getValue() < maxBombs.getValue()) && isAlive());
    }

    /**
     * If this method calls, then number of bombs placed simultaneously
     * must increase.
     */
    public synchronized void placedBomb() {
        settedBombs.increment();
    }

    /**
     * If this method calls, then number of bombs placed simultaneously
     * must decrease.
     */
    public void detonatedBomb() {
       settedBombs.decrement();
    }

    /**
     * Setting ID of player.
     * @param id id to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns ID of this player.
     * @return ID of this player.
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Setting new position to this player.
     * @param newPosition position to set.
     */
    @Override
    public synchronized void setPosition(Pair newPosition) {
        position.setX(newPosition.getX());
        position.setY(newPosition.getY());
    }

    /**
     * Returnes position of this player.
     * @return position of this player.
     */
    @Override
    public synchronized Pair getPosition() {
        return position;
    }

    /**
     * This method calls if player was damaged. If he wasnt immortal at this moment
     * he must lost some bonuses and one live. Then he became immortal for some time.
     *
     * If player was immortal then nothing will happen.
     */
    @Override
    public synchronized void bombed() {
        state.bombed(this);
    }

    public synchronized void accept(ModelPlayerVisitor visitor) {
        visitor.visit(this);
    }
   
    /**
     * Returns current explosion radius parameter.
     * @return current explosion radius parameter.
     */
    public int getRadius() {
        return explRadius.getValue();
    }

    public synchronized int changeRadius(int delta) {
        return explRadius.changeValue(delta);
    }

    /**
     * Returns nickName of this player.
     * @return nickName of this player.
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * Returns how much bombes player already setted.
     * <p> This is not the amount of bombes that player placed during all game.
     * This is number of bombs placed simultaneously.
     * @return
     */
    public int getSettedBombsNum() {
        return settedBombs.getValue();
    }

    public synchronized int changeSettedBombs(int delta) {
        return settedBombs.changeValue(delta);
    }
    
    public int getLives() {
        return lives.getValue();
    }

    public synchronized int changeLives(int delta) {
        int value = lives.changeValue(delta);
        if(value == 0) {
            playerDieListener.playerDied(this);
        }
        return value;
    }
    
    public int getMaxBombs() {
        return maxBombs.getValue();
    }

    public synchronized int changeMaxBombs(int delta) {
        return maxBombs.changeValue(delta);
    }

    void setState(PlayerState state) {
        this.state = state;
    }

    ScheduledExecutorService getTimer(){
        return timer;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ModelPlayer other = (ModelPlayer) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + this.id;
        return hash;
    }

    @Override
    public void move(GameMap where, Pair destination) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
