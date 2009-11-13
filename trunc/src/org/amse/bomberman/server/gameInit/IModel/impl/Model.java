/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameInit.IModel.impl;

import org.amse.bomberman.server.gameInit.Constants;
import org.amse.bomberman.server.gameInit.IModel.IModel;
import org.amse.bomberman.server.gameInit.Map;
import org.amse.bomberman.server.gameInit.Player;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Model implements IModel {
    //private List<ChangeListener> listeners;
    private Map map;

    private Model() {
    }

    public Model(Map map) {
        this.map = map;
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

        this.map.setSquare(x, y, Constants.MAP_EMPTY);
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
}
