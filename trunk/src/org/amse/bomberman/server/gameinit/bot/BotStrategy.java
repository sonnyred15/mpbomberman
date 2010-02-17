/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit.bot;

import org.amse.bomberman.server.gameinit.Pair;
import org.amse.bomberman.server.gameinit.imodel.IModel;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Constants.Direction;

/**
 *
 * @author Kirilchuk V.E.
 */
public abstract class BotStrategy {

    protected int[][] temp;
    private int cons = 1000; // on temp map bot put 1000 + num of steps to destination

    public Direction findWay(Pair begin, Pair end, final int[][] mapArray) throws IllegalArgumentException {
        this.temp = cloneField(mapArray);
        
        rec(begin, cons);
        int steps = temp[end.getX()][end.getY()] - cons;
        Pair currentPair = end;
        for (int i = steps; i > 1; i--) {
            int x = currentPair.getX();
            int y = currentPair.getY();
            if ((x > 0) && temp[x - 1][y] - cons == i - 1) {
                currentPair = new Pair(x - 1, y);
            } else {
                if ((y > 0) && temp[x][y - 1] - cons == i - 1) {
                    currentPair = new Pair(x, y - 1);
                } else {
                    if ((x < temp.length - 1) && temp[x + 1][y] - cons == i - 1) {
                        currentPair = new Pair(x + 1, y);
                    } else {
                        if ((y < temp.length - 1) && temp[x][y + 1] - cons == i - 1) {
                            currentPair = new Pair(x, y + 1);
                        }
                    }
                }
            }
        }
        if (currentPair.getX() == begin.getX() + 1) {
            return Direction.DOWN;
        } else {
            if (currentPair.getX() == begin.getX() - 1) {
                return Direction.UP;
            } else {
                if (currentPair.getY() == begin.getY() - 1) {
                    return Direction.LEFT;
                } else {
                    if (currentPair.getY() == begin.getY() + 1) {
                        return Direction.RIGHT;
                    } else {
                        // ???? what is it??? what to do???
                        throw new IllegalArgumentException();
                    }
                }
            }
        }
    }

    public abstract IAction thinkAction(Bot bot, IModel model);

    private int[][] cloneField(final int[][] mapArray) {
        int[][] result = new int[mapArray.length][mapArray.length];
        for (int i = 0; i < result.length; ++i) {
            for (int j = 0; j < result.length; ++j) {
                result[i][j] = mapArray[i][j];
            }
        }
        return result;
    }

    private void rec(Pair current, int steps) {
        int x = current.getX();
        int y = current.getY();
        temp[x][y] = steps;
        if (y < temp.length - 1) {
            int nextX = x;
            int nextY = y + 1;
            if ((temp[nextX][nextY] == Constants.MAP_EMPTY) ||
                    ((temp[nextX][nextY] > 1000) && (temp[nextX][nextY] > steps + 1))) {
                rec(new Pair(nextX, nextY), steps + 1);
            }
        }
        if (y > 0) {
            int nextX = x;
            int nextY = y - 1;
            if ((temp[nextX][nextY] == Constants.MAP_EMPTY) ||
                    ((temp[nextX][nextY] > 1000) && (temp[nextX][nextY] > steps + 1))) {
                rec(new Pair(nextX, nextY), steps + 1);
            }
        }
        if (x > 0) {
            int nextX = x - 1;
            int nextY = y;
            if ((temp[nextX][nextY] == Constants.MAP_EMPTY) ||
                    ((temp[nextX][nextY] > 1000) && (temp[nextX][nextY] > steps + 1))) {
                rec(new Pair(nextX, nextY), steps + 1);
            }
        }
        if (x < temp.length - 1) {
            int nextX = x + 1;
            int nextY = y;
            if ((temp[nextX][nextY] == Constants.MAP_EMPTY) ||
                    ((temp[nextX][nextY] > 1000) && (temp[nextX][nextY] > steps + 1))) {
                rec(new Pair(nextX, nextY), steps + 1);
            }
        }
    }
}
