/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit.imodel.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.imodel.IModel;
import org.amse.bomberman.server.gameinit.GameMap;
import org.amse.bomberman.server.gameinit.Pair;
import org.amse.bomberman.server.gameinit.Player;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Constants.Direction;

/**
 * Model that is responsable for game rules and responsable for connection
 * between Map and Game. Additionally responsable for bomb timers and
 * bombs detonations.
 * @author Kirilchuk V.E.
 */
public class Model implements IModel {

    private final GameMap map;
    private final Game game;
    private final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
    private final List<Pair> explosionSquares;
    private final List<DetonateControl> detonateControls;

    /**
     * Constructor of Model.
     * @param map GameMap that correspond to game
     * @param game Game for which model was created
     */
    public Model(GameMap map, Game game) {
        this.map = map;
        this.game = game;
        this.explosionSquares = new ArrayList<Pair>(); //what`s about syncronization?
        this.detonateControls = new ArrayList<DetonateControl>();
    }

    /**
     * Return matrix of GameMap.
     * @return matrix of GameMap
     */
    public int[][] getMapArray() {
        return this.map.getMapArray();
    }

    /**
     * Return list of explosions.
     * @return List of explosions
     */
    public List<Pair> getExplosionSquares() {
        return this.explosionSquares;
    }

    /**
     * Give x coordinate for player. Search respawn point
     * of this player on map and return x coordinate of this respawn.
     * @param playerID ID of player
     * @return x coordinate of player
     */
    public int xCoordOf(int playerID) {
        int[][] mapArray = this.map.getMapArray();
        for (int i = 0; i < mapArray.length; i++) {
            for (int j = 0; j < mapArray.length; j++) {
                if (mapArray[i][j] == playerID) {
                    return i;
                }
            }
        }
        return 0;
    }

    /**
     * See xCoordOf
     * @param playerID
     * @return
     */
    public int yCoordOf(int playerID) {
        int[][] mapArray = this.map.getMapArray();
        for (int i = 0; i < mapArray.length; i++) {
            for (int j = 0; j < mapArray.length; j++) {
                if (mapArray[i][j] == playerID) {
                    return j;
                }
            }
        }
        return 0;
    }

    /**
     * Trying to move the player in defined direction.
     * @param player Player to move
     * @param direction Direction of move
     * @return true if player moved, false otherwise
     */
    public synchronized boolean doMove(Player player, Direction direction) {
        int arr[] = newCoords(player.getX(), player.getY(), direction);
        int newX = arr[0];
        int newY = arr[1];
        if (!isOutMove(newX, newY)) {
            if (!isMoveToReserved(newX, newY)) {
                makeMove(player, newX, newY);
                return true;
            }
        }
        return false;
    }

    private void makeMove(Player player, int newX, int newY) {
        int x = player.getX();
        int y = player.getY();

        if (this.map.isBomb(x, y)) { //if player setted mine but still in same square
            this.map.setSquare(x, y, Constants.MAP_BOMB);
        } else {
            this.map.setSquare(x, y, Constants.MAP_EMPTY);
        }
        this.map.setSquare(newX, newY, player.getID());

        //if player is making move to explosion zone.
        if (explosionSquares.contains(new Pair(newX, newY))) {
            player.bombed();
        }

        player.setX(newX);
        player.setY(newY);
    }

    private boolean isOutMove(int x, int y) {
        int dim = this.map.getDimension();
        if (x < 0 || x > dim - 1) {
            return true;
        }
        if (y < 0 || y > dim - 1) {
            return true;
        }
        return false;
    }

    private boolean isMoveToReserved(int x, int y) {
        return (this.map.isEmpty(x, y)) ? false : true;
    }

    private int[] newCoords(int x, int y, Direction direction) { //whats about catch illegalArgumentException???
        int[] arr = new int[2];

        switch (direction) {
            case DOWN: {
                arr[0] = x + 1;
                arr[1] = y;
                break;
            }
            case LEFT: {
                arr[0] = x;
                arr[1] = y - 1;
                break;
            }
            case UP: {
                arr[0] = x - 1;
                arr[1] = y;
                break;
            }
            case RIGHT: {
                arr[0] = x;
                arr[1] = y + 1;
                break;
            }
            default: {
                throw new IllegalArgumentException("Default block " +
                        "in switch(ENUM). Error in code.");
            }
        }

        return arr;
    }

    /**
     * Printing matrix of GameMap to console. Maybe would be deleted soon.
     */
    public void printToConsole() { //useless?
        int dim = this.map.getDimension();
        for (int i = 0; i < dim; i++) {
            System.out.println();
            for (int j = 0; j < dim; j++) {
                System.out.print(this.map.getSquare(i, j) + " ");
            }
        }
        System.out.println();
    }

    /**
     * Change GameMap for defined in argument number of players by
     * removing unused players from GameMap.
     * @param maxPlayers number of players to use
     */
    public void changeMapForCurMaxPlayers(int maxPlayers) {
        this.map.changeMapForCurMaxPlayers(maxPlayers);
    }

    /**
     * Remove one player from mapArray
     * @param playerID ID of player we need to remove
     */
    public void removePlayer(int playerID) {
        this.map.removePlayer(playerID);
    }

    /**
     * Trying to place bomb of defined player.
     * @param player Player which trying to place bomb.
     */
    public void placeBomb(Player player) {// whats about synchronization?? //Maybe return value must be boolean type???
        synchronized (player) {
            if (player.canPlaceBomb()) { //player is alive and have bombs to set up
                int x = player.getX();
                int y = player.getY();
                if (this.map.isBomb(x, y)) {
                    return;
                }

                this.map.setSquare(x, y, Constants.MAP_BOMB);
                DetonateTask detonationTask = new DetonateTask(player, x, y); //task to execute
                ScheduledFuture<?> detonation = timer.schedule(detonationTask, //need to cancel detonation
                        Constants.BOMB_TIMER_VALUE, TimeUnit.MILLISECONDS);

                detonateControls.add(new DetonateControl(x, y, detonation, detonationTask));
            }
        }
    }

    /**
     * Return name of GameMap of this Model.
     * @return Name of GameMap in String
     */
    public String getMapName() {
        return this.map.getName();
    }

    private class DetonateControl {

        private final int bombX;
        private final int bombY;
        private final ScheduledFuture<?> detonation; // need to cancel timer Task
        private final DetonateTask detonationTask;

        public DetonateControl(int x, int y, ScheduledFuture<?> boom, DetonateTask dt) {//public of private. Javadoc???
            this.bombX = x;
            this.bombY = y;
            this.detonation = boom;
            this.detonationTask = dt;
        }

        public boolean isCorrespondTo(int x, int y) {
            if (this.bombX == x && this.bombY == y) {
                return true;
            }
            return false;
        }

        public void cancelDetonation() {
            this.detonation.cancel(false);
        }

        private void detonate() {
            detonationTask.run();
        }
    }

    private class ClearExplosionTask implements Runnable {

        private final int bombX;
        private final int bombY;
        private final List<Pair> explSqToClear;
        private final Player player;

        public ClearExplosionTask(List<Pair> toClear, Pair bombToClear, Player player) {
            this.bombX = bombToClear.getX();
            this.bombY = bombToClear.getY();
            this.explSqToClear = toClear;
            this.player = player;
        }

        @Override
        public void run() {
            if (player.getX() == bombX && player.getY() == bombY && player.isAlive()) {
                map.setSquare(bombX, bombY, this.player.getID());
            } else {
                map.setSquare(bombX, bombY, Constants.MAP_EMPTY); //clear from map    
            }
            for (Pair pair : explSqToClear) { // clear from explosions list
                explosionSquares.remove(pair);
            }
        }
    }

    private class DetonateTask implements Runnable {

        private final int bombX;
        private final int bombY;
        private final Player player;
        private final int radius;

        public DetonateTask(Player player, int x, int y) {
            this.player = player;
            this.radius = player.getRadius();
            this.bombX = x;
            this.bombY = y;

            player.placedBomb();
        }

        @Override
        public void run() {
            ArrayList<Pair> explSq = new ArrayList<Pair>();

            //if player still staying in bomb square.
            if (this.player.getX() == this.bombX && this.player.getY() == this.bombY) {
                player.bombed();
            }

            map.setSquare(bombX, bombY, Constants.MAP_DETONATED_BOMB);
            detonateControls.remove(this);
            //explosion lines
            int i; // common iterator
            int k; // common radius counter
            boolean contin; //common continue boolean

            //uplines
            k = radius;
            for (i = bombX - 1; (i >= 0 && k > 0); --i, --k) {
                contin = explodeSquare(i, bombY);
                explSq.add(new Pair(i, bombY));
                if (!contin) {
                    break;
                }
            }

            //downlines
            k = radius;
            for (i = bombX + 1; (i < map.getDimension() && k > 0); ++i, --k) {
                contin = explodeSquare(i, bombY);
                explSq.add(new Pair(i, bombY));
                if (!contin) {
                    break;
                }
            }

            //leftlines
            k = radius;
            for (i = bombY - 1; (i >= 0 && k > 0); --i, --k) {
                contin = explodeSquare(bombX, i);
                explSq.add(new Pair(bombX, i));
                if (!contin) {
                    break;
                }
            }

            //rightlines
            k = radius;
            for (i = bombY + 1; (i < map.getDimension() && k > 0); ++i, --k) {
                contin = explodeSquare(bombX, i);
                explSq.add(new Pair(bombX, i));
                if (!contin) {
                    break;
                }
            }

            explosionSquares.addAll(explSq); //add explosions from this to others
            player.detonatedBomd();
            timer.schedule(new ClearExplosionTask(explSq, new Pair(bombX, bombY), this.player), Constants.BOMB_DETONATION_TIME, TimeUnit.MILLISECONDS);
        }

        //true if we must continue cycle
        //false if we must break cycle;
        private boolean explodeSquare(int x, int y) {
            if (map.isEmpty(x, y)) {
                if (explosionSquares.contains(new Pair(x, y))) {     //explosion
                    return false;
                }
                return true;                                         //emptySquare
            } else if (map.blockAt(x, y) != -1) {                    //blockSquare
                if (map.blockAt(x, y) == 1) {                        //undestroyableBlock
                    //undestroyable so do nothing //going to return false;
                    } else {                                         //destroyable block
                    map.setSquare(x, y, map.blockAt(x, y) + 1 - 9);
                }
                return false;
            } else if (map.playerIdAt(x, y) != -1) {                 //playerSquare
                int id = map.playerIdAt(x, y);
                game.getPlayer(id).bombed();
                return false;
            } else if (map.isBomb(x, y)) {                           //another bomb
                DetonateControl bombControl = null;
                for (DetonateControl control : detonateControls) {
                    if (control.isCorrespondTo(x, y)) {
                        bombControl = control;
                        break;
                    }
                }
                bombControl.cancelDetonation(); //removeFromTimer
                detonateControls.remove(bombControl); //removeFromList
                bombControl.detonate(); //detonate
                return false;
            }

            return true;
        }
    }
}
