/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameservice.bots;

import java.util.Collections;
import java.util.List;
import org.amse.bomberman.server.gameservice.Game;
import org.amse.bomberman.util.Pair;
import org.amse.bomberman.server.gameservice.models.Model;
import org.amse.bomberman.server.gameservice.models.impl.Bonus;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Constants.Direction;

/**
 * Class that represents bot strategy. Strategy is something like methods
 * about how to findBest way to target and how to make desicion what to do...
 * @author Kirilchuk V.E.
 */
public abstract class BotStrategy {

    /**
     * Clone of game field. Bot can do with it whatever he wants.
     */
    private int cons = 1000; // on temp map bot put 1000 + num of steps to destination

    /**
     * Method to find way to target. Not the best algorithm but can be overrided
     * by inherited class.
     * @param begin start position to fin way from.
     * @param end end position to find way to.
     * @param mapArray game field to find way on.
     * @return direction to move to became closer on one step to target.
     * @throws IllegalArgumentException if bot can`t find way to current target.
     */
    public Direction
            findWay(Game game, Pair begin, Pair end)
            throws IllegalArgumentException {
        int[][] field = game.getGameField();
        List<Pair> explosions = Collections.unmodifiableList(game.getExplosions());
        int[][] temp = cloneField(field);

        rec(temp, explosions, begin, cons);
        int steps = temp[end.getX()][end.getY()] - cons;

        if (steps < 0) { //bad desicion...it fixes one bug but error is in alghorithm...
            throw new IllegalArgumentException();
        }

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
                        // can`t find way
                        throw new IllegalArgumentException();
                    }
                }
            }
        }
    }

    /**
     * Abstract method that must make decision about what to do.
     * @param bot bot that thinking about action.
     * @param model model that owns this bot.
     * @return action for bot to do.
     */
    public abstract Action thinkAction(Game game, BotGamePlayer bot);

    private int[][] cloneField(final int[][] mapArray) {
        int[][] result = new int[mapArray.length][mapArray.length];
        for (int i = 0; i < result.length; ++i) {
            for (int j = 0; j < result.length; ++j) {
                result[i][j] = mapArray[i][j];
            }
        }
        return result;
    }

    private void rec(int[][] temp, List<Pair> expl, Pair current, int steps) {
        int x = current.getX();
        int y = current.getY();
        temp[x][y] = steps;
        Pair next;
        if (y < temp.length - 1) {
            int nextX = x;
            int nextY = y + 1;
            next = new Pair(nextX, nextY);
            if ((temp[nextX][nextY] == Constants.MAP_EMPTY)
                    && !(expl.contains(next))
                    || (Bonus.isBonus(temp[nextX][nextY]))
                    || ((temp[nextX][nextY] > 1000) && (temp[nextX][nextY] > steps + 1))) {
                rec(temp, expl, new Pair(nextX, nextY), steps + 1);
            }
        }
        if (y > 0) {
            int nextX = x;
            int nextY = y - 1;
            next = new Pair(nextX, nextY);
            if ((temp[nextX][nextY] == Constants.MAP_EMPTY)
                    && !(expl.contains(next))
                    || (Bonus.isBonus(temp[nextX][nextY]))
                    || ((temp[nextX][nextY] > 1000) && (temp[nextX][nextY] > steps + 1))) {
                rec(temp, expl, new Pair(nextX, nextY), steps + 1);
            }
        }
        if (x > 0) {
            int nextX = x - 1;
            int nextY = y;
            next = new Pair(nextX, nextY);
            if ((temp[nextX][nextY] == Constants.MAP_EMPTY)
                    && !(expl.contains(next))
                    || (Bonus.isBonus(temp[nextX][nextY]))
                    || ((temp[nextX][nextY] > 1000) && (temp[nextX][nextY] > steps + 1))) {
                rec(temp, expl, new Pair(nextX, nextY), steps + 1);
            }
        }
        if (x < temp.length - 1) {
            int nextX = x + 1;
            int nextY = y;
            next = new Pair(nextX, nextY);
            if ((temp[nextX][nextY] == Constants.MAP_EMPTY)
                    && !(expl.contains(next))
                    || (Bonus.isBonus(temp[nextX][nextY]))
                    || ((temp[nextX][nextY] > 1000) && (temp[nextX][nextY] > steps + 1))) {
                rec(temp, expl, new Pair(nextX, nextY), steps + 1);
            }
        }
    }
}
