/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameInit.imodel.impl;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.amse.bomberman.server.gameInit.Constants;
import org.amse.bomberman.server.gameInit.Game;
import org.amse.bomberman.server.gameInit.imodel.IModel;
import org.amse.bomberman.server.gameInit.GameMap;
import org.amse.bomberman.server.gameInit.Player;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Model implements IModel {
    //private List<ChangeListener> listeners;
    private GameMap map;
    private Game game;
    private Timer timer = new Timer();

    private Model() {
    }

    public Model(GameMap map, Game game) {
        this.map = map;
        this.game = game;
//        this.listeners=new ArrayList<ChangeListener>();
    }

    public int[][] getMapArray() {
        return this.map.getMapArray();
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

//    public void addChangeListener(ChangeListener changeModelListener){
//        this.listeners.add(changeModelListener);
//    }

//    public void notifyListeners(){
//        for (ChangeListener changeListener : listeners) {
//            changeListener.stateChanged(null);
//        }
//    }
    private void makeMove(Player player, int newX, int newY) {
        int x = player.getX();
        int y = player.getY();

        if (this.map.isMine(x, y)) {
            this.map.setSquare(x, y, Constants.MAP_BOMB);
        } else {
            this.map.setSquare(x, y, Constants.MAP_EMPTY);
        }
        this.map.setSquare(newX, newY, player.getID());

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
        if (this.map.isEmpty(x, y)) {
            return false;
        } else {
            return true;
        }
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
                //throw new Exception;
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
        if (player.canPlaceBomb()) {
            int x = player.getX();
            int y = player.getY();
            this.map.setSquare(x, y, Constants.MAP_BOMB);
            this.timer.schedule(new DetonateTask(player, x, y), Constants.stepTime * 10);
        }
    }

    public void detonate(Player player, int radius, int x, int y) {
    }

    private class ClearExplosion extends TimerTask {

        private ArrayList<Pair> squaresToClear;

        public ClearExplosion(ArrayList<Pair> toClear) {
            this.squaresToClear = toClear;
        }

        @Override
        public void run() {
            for (Pair pair : squaresToClear) {
                int x = pair.x;
                int y = pair.y;
                map.setSquare(x, y, Constants.MAP_EMPTY);
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
            ArrayList<Pair> explLines = new ArrayList<Pair>();

            map.setSquare(x, y, Constants.MAP_DETONATED_BOMB);
            explLines.add(new Pair(x, y));
            //explotion lines
            int i; // x-line iterator
            int j; // y-line iterator
            int k; // radius counter
            
            //uplines
            k = radius;
            for (i = x - 1; (i >= 0 && k > 0); i--, k--) {
                boolean contin = explodeSquare(i, y);
                if (contin) {
                    explLines.add(new Pair(i, y));
                } else {
                    break;
                }
            }

            //downlines
            k = radius;
            for (i = x + 1; (i < map.getDimension() && k > 0); i++, k--) {
                boolean contin = explodeSquare(i, y);
                if (contin) {
                    explLines.add(new Pair(i, y));
                } else {
                    break;
                }
            }

            //leftlines
            k = radius;
            for (j = y - 1; (j >= 0 && k > 0); j--, k--) {
                boolean contin = explodeSquare(x, j);
                if (contin) {
                    explLines.add(new Pair(x, j));
                } else {
                    break;
                }
            }

            //rightlines
            k = radius;
            for (j = y + 1; (j < map.getDimension() && k > 0); j++, k--) {
                boolean contin = explodeSquare(x, j);
                if (contin) {
                    explLines.add(new Pair(x, j));
                } else {
                    break;
                }
            }
            
            player.detonatedBomd();
            timer.schedule(new ClearExplosion(explLines), Constants.stepTime * 3);
        }
    }
    
    //true if we must put explosion square
    //false if we must break cycle;
    private boolean explodeSquare(int x, int y){
                if (map.isEmpty(x, y)) {                                 //emptySquare
                    map.setSquare(x, y, Constants.MAP_EXPLOSION_LINE);
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
                }
                return true; //need to be checked!!!!!
    }

    private class Pair {

        protected int x;
        protected int y;

        public Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
