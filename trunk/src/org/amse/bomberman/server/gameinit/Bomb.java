/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.amse.bomberman.server.gameinit.imodel.IModel;
import org.amse.bomberman.util.Constants;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Bomb implements MoveableObject{

    private final IModel model;
    private final Player owner;
    private final GameMap gameMap;
    private final ScheduledExecutorService timer;
    private final Pair position; //still can change position by this.position methods.
    private final int radius;
    private boolean wasDetonated = false;

    public Bomb(IModel model, Player player, GameMap map, Pair bombPosition, ScheduledExecutorService timer) {
        this.model = model;
        this.owner = player;
        this.gameMap = map;
        this.timer = timer;

        this.position = bombPosition;
        this.radius = this.owner.getRadius();

        this.owner.placedBomb();
//        System.out.println(owner.getBombs());
        timer.schedule(new DetonateTask(),
                Constants.BOMB_TIMER_VALUE, TimeUnit.MILLISECONDS);
    }

    public void detonate() {

        if (this.wasDetonated) {
            return;
        }

        this.wasDetonated = true;
        this.model.bombDetonated(this);

        ArrayList<Pair> explosions = new ArrayList<Pair>();

        //if owner still staying in bomb square.
        if (this.owner.getPosition().equals(this.position)) {
            this.model.playerBombed(this.owner, this.owner);
        }

        //explosion lines
        int i; // common iterator
        int k; // common radius counter
        boolean contin; //common continue boolean

        int bombX = this.position.getX();
        int bombY = this.position.getY();

        //uplines
        k = radius;
        for (i = bombX - 1; (i >= 0 && k > 0); --i, --k) {
            contin = explodeSquare(i, bombY);
            explosions.add(new Pair(i, bombY));
            if (!contin) {
                break;
            }
        }

        //downlines
        k = radius;
        for (i = bombX + 1; (i < gameMap.getDimension() && k > 0); ++i, --k) {
            contin = explodeSquare(i, bombY);
            explosions.add(new Pair(i, bombY));
            if (!contin) {
                break;
            }
        }

        //leftlines
        k = radius;
        for (i = bombY - 1; (i >= 0 && k > 0); --i, --k) {
            contin = explodeSquare(bombX, i);
            explosions.add(new Pair(bombX, i));
            if (!contin) {
                break;
            }
        }

        //rightlines
        k = radius;
        for (i = bombY + 1; (i < gameMap.getDimension() && k > 0); ++i, --k) {
            contin = explodeSquare(bombX, i);
            explosions.add(new Pair(bombX, i));
            if (!contin) {
                break;
            }
        }

        this.model.addExplosions(explosions); //add explosions to model
        this.owner.detonatedBomb();
        timer.schedule(new ClearExplosionTask(explosions, new Pair(bombX, bombY), this.owner), Constants.BOMB_DETONATION_TIME, TimeUnit.MILLISECONDS);
    }

    //true if we must continue cycle
    //false if we must break cycle;
    private boolean explodeSquare(int x, int y) {
        if (gameMap.isEmpty(x, y)) {
            if (this.model.isExplosion(new Pair(x, y))) {     //explosion
                return true;
            }
            return true;                                         //emptySquare
        } else if (gameMap.blockAt(x, y) != -1) {                    //blockSquare
            if (gameMap.blockAt(x, y) == 1) {                        //undestroyableBlock
                //undestroyable so do nothing
                //going to return false;
            } else {                                         //destroyable block
                if (gameMap.blockAt(x, y) == 8) {                // destroyed block
                    gameMap.destroyBlock(x, y);
                } else {
                    gameMap.setSquare(x, y, gameMap.blockAt(x, y) + 1 - 9);
                }
            }
            return false;
        } else if (gameMap.playerIdAt(x, y) != -1) {                 //playerSquare
            int id = gameMap.playerIdAt(x, y);
            this.model.playerBombed(this.owner, id);
            return false;
        } else if (gameMap.isBomb(x, y)) {                           //another bomb
            this.model.detonateBombAt(x, y);
            return false;
        }

        return true;
    }

    public Player getOwner() {
        return owner;
    }

    public Pair getPosition() {
        return this.position;
    }

    public int getID() {
        return Constants.MAP_BOMB;
    }

    public void setPosition(Pair newPosition) {
        this.position.setX(newPosition.getX());
        this.position.setY(newPosition.getY());
    }

    public void bombed() {
        this.model.detonateBombAt(this.position.getX(), this.position.getY());
    }

    private class DetonateTask implements Runnable {

        public DetonateTask() {//whats about syncronization(owner)
        }

        @Override
        public void run() {
            detonate();
//            System.out.println("BOMBBBBBBBBBBBBBBBB DETOOOOOOOOOOOOONAAAAAAAAAAAATEEEEEEEEEEEEDDDDDDd");
//            System.out.println(owner.getBombs());
        }
    }

    private class ClearExplosionTask implements Runnable {

        private final Pair bombToClear;
        private final List<Pair> explSqToClear;
        private final Player player;

        public ClearExplosionTask(List<Pair> toClear, Pair bombToClear, Player player) {
            this.bombToClear = bombToClear;
            this.explSqToClear = toClear;
            this.player = player;
        }

        @Override
        public void run() {//whats about syncronization(owner,map)
            if (player.getPosition().equals(bombToClear) && player.isAlive()) {
                gameMap.setSquare(bombToClear.getX(), bombToClear.getY(), this.player.getID());
            } else {
                gameMap.setSquare(bombToClear.getX(), bombToClear.getY(), Constants.MAP_EMPTY); //clear from map
            }
            for (Pair pair : explSqToClear) { // clear from explosions list
                model.removeExplosion(pair);
            }
        }
    }
}
