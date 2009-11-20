/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit.imodel.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.amse.bomberman.server.gameinit.Constants;
import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.imodel.IModel;
import org.amse.bomberman.server.gameinit.GameMap;
import org.amse.bomberman.server.gameinit.Pair;
import org.amse.bomberman.server.gameinit.Player;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Model implements IModel {

    private GameMap map;
    private Game game;
    private Timer timer = new Timer();
    private List<Pair> explosionSquares;
    private List<DetonateTask> bombes;

    private Model() {
    }

    public Model(GameMap map, Game game) {
        this.map = map;
        this.game = game;
        this.explosionSquares = new ArrayList<Pair>();
        this.bombes = new ArrayList<DetonateTask>();
    }

    public int[][] getMapArray() {
        return this.map.getMapArray();
    }
    
    public List<Pair> getExplosionSquares(){
        return this.explosionSquares;
    }

    /**
     * Give x coordinate for player. Search respawn point 
     * of this player on map and return x coordinate of this respawn.
     * @param playerID ID of player
     * @return x coordinate in mapArray
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

    public boolean doMove(Player player, int direction) {
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

        if (explosionSquares.contains(new Pair(newX, newY))){
            this.game.playerBombed(player.getID());
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

    private int[] newCoords(int x, int y, int direction) {
        int[] arr = new int[2];
        switch (direction) {
            case Constants.DIRECTION_DOWN: {
                arr[0] = x + 1;
                arr[1] = y;
                break;
            }
            case Constants.DIRECTION_LEFT: {
                arr[0] = x;
                arr[1] = y - 1;
                break;
            }
            case Constants.DIRECTION_UP: {
                arr[0] = x - 1;
                arr[1] = y;
                break;
            }
            case Constants.DIRECTION_RIGHT: {
                arr[0] = x;
                arr[1] = y + 1;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported direction"
                        + " dir=" + direction + ". In Model.newCoords()");
            }
        }
        return arr;
    }

    public void printToConsole() {
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
     * Remove unused players from mapArray.
     * @param maxPlayers
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

    public void placeBomb(Player player) {
        if (player.canPlaceBomb()) { //player is alive and have bombs to set up
            int x = player.getX();
            int y = player.getY();
            if (this.map.isBomb(x, y)){
                return;
            }
            this.map.setSquare(x, y, Constants.MAP_BOMB);
            DetonateTask dt = new DetonateTask(player, x, y);
            bombes.add(dt);            
            this.timer.schedule(dt, Constants.BOMB_TIMER_VALUE);
        }
    }

    private class ClearExplosion extends TimerTask {

        private List<Pair> explSqToClear;
        private Player player;
        private int bombX;
        private int bombY;

        public ClearExplosion(List<Pair> toClear, Pair bombToClear, Player player) {
            this.explSqToClear = toClear;
            this.player = player;
            this.bombX = bombToClear.getX();
            this.bombY = bombToClear.getY();
        }

        @Override
        public void run() {
            if (player.getX()==bombX && player.getY()==bombY && player.isAlive()){
                map.setSquare(bombX, bombY, this.player.getID());    
            }else{
                map.setSquare(bombX, bombY, Constants.MAP_EMPTY); //clear from map    
            }
            for (Pair pair : explSqToClear) { // clear from explosions list
                explosionSquares.remove(pair);
            }
        }
    }

    private class DetonateTask extends TimerTask {

        private final Player player;
        private final int radius;
        private final int x;
        private final int y;
        //CHECK V THIS//    
        public DetonateTask(Player player, int x, int y) {
            this.player = player;
            this.radius = player.getRadius();
            this.x = x;
            this.y = y;

            player.placedBomb();
        }

        @Override
        public void run() {
            ArrayList<Pair> explSq = new ArrayList<Pair>();
            
            if(this.player.getX()==this.x && this.player.getY()==this.y){
                player.bombed();
            }
            map.setSquare(x, y, Constants.MAP_DETONATED_BOMB);
            bombes.remove(this);
            //explotion lines
            int i; // x-line iterator
            int j; // y-line iterator
            int k; // radius counter

            //uplines
            k = radius;
            for (i = x - 1; (i >= 0 && k > 0); i--, k--) {
                boolean contin = explodeSquare(i, y);
                explSq.add(new Pair(i, y));
                if (!contin) {
                    break;
                }
            }

            //downlines
            k = radius;
            for (i = x + 1; (i < map.getDimension() && k > 0); i++, k--) {
                boolean contin = explodeSquare(i, y);
                explSq.add(new Pair(i, y));
                if (!contin) {
                    break;
                }
            }

            //leftlines
            k = radius;
            for (j = y - 1; (j >= 0 && k > 0); j--, k--) {
                boolean contin = explodeSquare(x, j);
                explSq.add(new Pair(x, j));
                if (!contin) {
                    break;
                }
            }

            //rightlines
            k = radius;
            for (j = y + 1; (j < map.getDimension() && k > 0); j++, k--) {
                boolean contin = explodeSquare(x, j);
                explSq.add(new Pair(x, j));
                if (!contin) {
                    break;
                }
            }
            explosionSquares.addAll(explSq); //add explosion from this to others
            player.detonatedBomd();
            timer.schedule(new ClearExplosion(explSq, new Pair(x, y), this.player), Constants.BOMB_DETONATION_TIME);
        }

        public boolean isCorrespondTo(int x, int y) {
            if (this.x == x && this.y == y) {
                return true;
            }
            return false;
        }
        //true if we must put explosion square
        //false if we must break cycle;
        private boolean explodeSquare(int x, int y) {
            if (map.isEmpty(x, y)) {                                 //emptySquare
                //map.setSquare(x, y, Constants.MAP_EXPLOSION_LINE);
                return true;
            } else if (map.blockAt(x, y) != -1) {                     //blockSquare
                if (map.blockAt(x, y) == 1) {                          //undestroyableBlock
                    //undestroyable so do nothing
                    } else {
                    map.setSquare(x, y, map.blockAt(x, y) + 1 - 9);   //destroyable block
                }
                return false;
            } else if (map.playerIdAt(x, y) != -1) {                 //playerSquare
                int id = map.playerIdAt(x, y);
                game.playerBombed(id);

                return false;
            } else if (map.isBomb(x, y)) {  //another bomb
                DetonateTask dt = null;
                for (DetonateTask detonateTask : bombes) {
                    if(detonateTask.isCorrespondTo(x, y)){
                        dt = detonateTask;
                    }
                }
                dt.cancel(); //removeFromTimer
                bombes.remove(dt); //removeFromList
                dt.run(); //execute
                return false;
            }
            return true; //CHECK < THIS// is this ok?
        }
    }
}
