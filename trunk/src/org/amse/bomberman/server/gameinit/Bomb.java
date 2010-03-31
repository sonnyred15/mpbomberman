
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.imodel.IModel;
import org.amse.bomberman.util.Constants;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Bomb implements MoveableObject {
    private static final ScheduledExecutorService timer =
        Executors.newSingleThreadScheduledExecutor();
    private boolean       wasDetonated = false;
    private final GameMap gameMap;
    private final IModel  model;
    private final Player  owner;
    private final Pair    position;
    private final int     radius;

    public Bomb(IModel model, Player player, Pair bombPosition) {
        //init object fields
        this.model = model;
        this.owner = player;
        this.gameMap = this.model.getGameMap();
        this.position = bombPosition;
        this.radius = this.owner.getRadius();
        //additional stuff
        this.owner.placedBomb();
        Bomb.timer.schedule(new DetonateTask(), Constants.BOMB_TIMER_VALUE,
                            TimeUnit.MILLISECONDS);
    }

    public void bombed() {
        this.model.detonateBombAt(this.position);
    }

    public void detonate() {
        if (this.wasDetonated) {
            return;
        }

        this.wasDetonated = true;

        // removing bomb from model bomb list
        // and clearing it from gameMap
        this.model.bombDetonated(this);

        ArrayList<Pair> explosions = new ArrayList<Pair>();

        // explosion lines
        int     i;    // common iterator
        int     k;    // common radius counter
        boolean contin;    // common continue boolean
        int     bombX = this.position.getX();
        int     bombY = this.position.getY();

        // uplines
        k = radius;

        for (i = bombX - 1; ((i >= 0) && (k > 0)); --i, --k) {
            contin = explodeSquare(i, bombY);
            explosions.add(new Pair(i, bombY));

            if (!contin) {
                break;
            }
        }

        // downlines
        k = radius;

        for (i = bombX + 1; ((i < gameMap.getDimension()) && (k > 0));
                ++i, --k) {
            contin = explodeSquare(i, bombY);
            explosions.add(new Pair(i, bombY));

            if (!contin) {
                break;
            }
        }

        // leftlines
        k = radius;

        for (i = bombY - 1; ((i >= 0) && (k > 0)); --i, --k) {
            contin = explodeSquare(bombX, i);
            explosions.add(new Pair(bombX, i));

            if (!contin) {
                break;
            }
        }

        // rightlines
        k = radius;

        for (i = bombY + 1; ((i < gameMap.getDimension()) && (k > 0));
                ++i, --k) {
            contin = explodeSquare(bombX, i);
            explosions.add(new Pair(bombX, i));

            if (!contin) {
                break;
            }
        }

        // center of Explosion
        explosions.add(this.position);

        // if owner still staying in bomb square and not under explosion.
        if (this.owner.getPosition().equals(this.position)
                &&!this.model.isExplosion(this.position)) {
            this.model.playerBombed(this.owner, this.owner);
        }

        this.model.addExplosions(explosions);    // add explosions to model
        this.owner.detonatedBomb();
        Bomb.timer.schedule(new ClearExplosionTask(explosions),
                            Constants.BOMB_DETONATION_TIME,
                            TimeUnit.MILLISECONDS);
    }

    // true if we must continue cycle
    // false if we must break cycle;
    private boolean explodeSquare(int x, int y) {
        Pair squareToExplode = new Pair(x, y);

        if (gameMap.isEmpty(x, y)) {
            if (model.isExplosion(squareToExplode)) {    // explosion
                return true;
            }

            return true;    // emptySquare
        } else if (gameMap.blockAt(x, y) != -1) {    // blockSquare
            if (gameMap.blockAt(x, y) == 1) {    // undestroyableBlock

                // undestroyable so do nothing
                // going to return false;
            } else {    // destroyable block
                if (gameMap.blockAt(x, y) == 8) {    // destroyed block
                    gameMap.destroyBlock(x, y);
                } else {
                    gameMap.setSquare(x, y, gameMap.blockAt(x, y) + 1 - 9);
                }
            }

            return false;
        } else if (gameMap.playerIDAt(x, y) != -1) {    // playerSquare
            int id = gameMap.playerIDAt(x, y);

            model.playerBombed(this.owner, id);

            return false;
        } else if (gameMap.isBomb(x, y)) {    // another bomb
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
    public int getID() {
        return Constants.MAP_BOMB;
    }

    public Player getOwner() {
        return owner;
    }

    public Pair getPosition() {
        return this.position;
    }

    public void setPosition(Pair newPosition) {
        this.position.setX(newPosition.getX());
        this.position.setY(newPosition.getY());
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
            detonate();
        }
    }
}
