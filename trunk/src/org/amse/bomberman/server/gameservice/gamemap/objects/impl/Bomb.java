package org.amse.bomberman.server.gameservice.gamemap.objects.impl;

import org.amse.bomberman.server.gameservice.gamemap.impl.GameMap;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Pair;
import org.amse.bomberman.server.gameservice.models.Model;
import org.amse.bomberman.server.gameservice.gamemap.impl.MoveableGameMapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.amse.bomberman.server.gameservice.models.impl.ModelPlayer;

/**
 * Class that represents Bomb - object of bomberman game.
 * @author Kirilchuk V.E.
 */
public class Bomb implements MoveableGameMapObject {

    private boolean                        wasDetonated = false;
    private final Model                    model;
    private final ModelPlayer              owner;
    private final Pair                     position = new Pair();
    private final int                      radius;
    private final ScheduledExecutorService timer;

    /**
     * Constructor of Bomb. Should be called in model when someone places bomb.
     * @param model IModel that owns this Bomb.
     * @param player Player that setted the bomb.
     * @param bombPosition coordinates on gameMap where Bomb was placed.
     */
    public Bomb(Model model, ModelPlayer player, Pair bombPosition, ScheduledExecutorService timer) {

        /* init object fields */
        this.model    = model;
        this.owner    = player;
        setPosition(bombPosition);
        this.radius   = this.owner.getRadius();

        /* additional stuff */
        this.timer = timer;
        this.owner.placedBomb(); //takes bomb from player
        this.timer.schedule(new DetonateTask(), Constants.BOMB_TIMER_VALUE,
                            TimeUnit.MILLISECONDS);
    }

    /**
     * Method of MoveableObject interface.
     * When bomb is moving to explosion it must detonate.
     * @see MoveableObject
     */
    public void bombed() {
        this.detonate(true);
    }

    /**
     * Method to detonate Bomb. It is called either from Model, when other bomb
     * explode and detonate this bomb, or from DetonateTask,
     * when time to explode has come.
     */
    public void detonate(boolean chained) {
        if (this.wasDetonated) {
            return;
        }

        this.wasDetonated = true;

        // removing bomb from model bomb list
        // and clearing it from gameMap
        this.model.bombDetonated(this);

        //
        GameMap gameMap = this.model.getGameMap();
        ArrayList<Pair> explosions = new ArrayList<Pair>();

        // explosion lines
        int     i;         // common iterator
        int     k;         // common radius counter
        boolean contin;    // common continue boolean
        int     bombX = this.position.getX();
        int     bombY = this.position.getY();

        // uplines
        k = radius;

        for (i = bombX - 1; ((i >= 0) && (k > 0)); --i, --k) {
            contin = explodeSquare(i, bombY, gameMap);
            explosions.add(new Pair(i, bombY));

            if (!contin) {
                break;
            }
        }

        // downlines
        k = radius;

        for (i = bombX + 1; ((i < gameMap.getDimension()) && (k > 0)); ++i, --k) {
            contin = explodeSquare(i, bombY, gameMap);
            explosions.add(new Pair(i, bombY));

            if (!contin) {
                break;
            }
        }

        // leftlines
        k = radius;

        for (i = bombY - 1; ((i >= 0) && (k > 0)); --i, --k) {
            contin = explodeSquare(bombX, i, gameMap);
            explosions.add(new Pair(bombX, i));

            if (!contin) {
                break;
            }
        }

        // rightlines
        k = radius;

        for (i = bombY + 1; ((i < gameMap.getDimension()) && (k > 0));
                ++i, --k) {
            contin = explodeSquare(bombX, i, gameMap);
            explosions.add(new Pair(bombX, i));

            if (!contin) {
                break;
            }
        }

        // center of Explosion
        explosions.add(this.position);

        // if owner still staying in bomb square and not under other explosion.
        if (this.owner.getPosition().equals(this.position)
                &&!this.model.isExplosion(this.position)) {
            this.model.playerBombed(this.owner, this.owner);
        }

        this.model.addExplosions(explosions);    // add explosions to model
        this.owner.detonatedBomb(); //must return bomb to player
        this.timer.schedule(new ClearExplosionTask(explosions),
                            Constants.BOMB_DETONATION_TIME,
                            TimeUnit.MILLISECONDS);

        if(!chained){
            this.model.tryEnd();
        }
    }

    // true if we must continue cycle
    // false if we must break cycle;
    private boolean explodeSquare(int x, int y, GameMap gameMap) {
        Pair squareToExplode = new Pair(x, y);

        if (gameMap.isEmpty(squareToExplode)) {
            if (model.isExplosion(squareToExplode)) {    // explosion
                return true;
            }

            return true;                                 // emptySquare
        } else if (gameMap.isBlock(squareToExplode)) { // blockSquare
            gameMap.damageBlock(squareToExplode);

            return false;
        } else if (gameMap.playerIdAt(squareToExplode) != -1) {  // playerSquare
            int id = gameMap.playerIdAt(squareToExplode);
            model.playerBombed(this.owner, id);

            return false;
        } else if (gameMap.isBomb(squareToExplode)) {  // another bomb
            model.detonateBombAt(squareToExplode);

            return false;
        }

        return true;
    }

    /**
     * Returns Constants.MAP_BOMB integer.
     * This method may need to use bomb as MoveableObject and after move
     * use gameMap.setSquare(moveObj.getID).
     * @return Constants.MAP_BOMB integer value.
     */
    public int getId() {
        return Constants.MAP_BOMB;
    }

    /**
     * Returns the owner of this bomb.
     * Owner - it is player that setted this bomb.
     * @return owner of this bomb.
     */
    public ModelPlayer getOwner() {
        return owner;
    }

    /**
     * Returns the position of this bomb on gameMap.
     * @return position of this bomb on gameMap
     */
    public Pair getPosition() {
        return this.position;
    }

    /**
     * Sets the new position of this bomb.
     * @param newPosition new position on gameMap.
     */
    public void setPosition(Pair newPosition) {
        this.position.setX(newPosition.getX());
        this.position.setY(newPosition.getY());
    }

    public void move(GameMap where, Pair destination) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private class ClearExplosionTask implements Runnable {
        private final List<Pair> explosions;

        public ClearExplosionTask(List<Pair> toClear) {
            this.explosions = toClear;
        }

        @Override
        public void run() {
            model.removeExplosions(explosions);
        }
    }

    private class DetonateTask implements Runnable {
        @Override
        public void run() {
            detonate(false);
        }
    }
}
