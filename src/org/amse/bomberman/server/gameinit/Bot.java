package org.amse.bomberman.server.gameinit;

import java.util.Random;

import org.amse.bomberman.server.gameinit.imodel.IModel;
import org.amse.bomberman.util.*;
import org.amse.bomberman.util.Constants.Direction;

/**
 * Class for adding bots to the games on server
 * @author michail korovkin
 */
public class Bot extends Player implements Runnable {

    private static final long BOT_STEP_DELAY = 100L;

    private IModel model;
    private Pair target;
    private int cons = 1000; //WHAT IS THIS??
    private int[][] temp;

    public Bot(String nickName, int id, IModel model) {
        super(nickName, id);
        this.model = model;
    }

    private int[][] cloneField(int[][] mapArray) {
        int[][] result = new int[mapArray.length][mapArray.length];
        for (int i = 0; i < result.length; ++i) {
            for (int j = 0; j < result.length; ++j) {
                result[i][j] = mapArray[i][j];
            }
        }
        return result;
    }

    private void findNewTarget() {
        GameMap map = model.getMap();
        int x = 0;
        int y = 0;
        Random random = new Random();
        while (!map.isEmpty(x, y)) {
            x = random.nextInt(map.getDimension() - 1);
            y = random.nextInt(map.getDimension() - 1);
        }
        target = new Pair(x, y);
        try {
            Thread.sleep(Constants.GAME_STEP_TIME);
        } catch (InterruptedException ex) {
            System.out.println("INTERRUPTED EXCEPTION IN BOT THREAD!!!!");
        }
    }

    public void run() {
        GameMap map = model.getMap();

        while (this.isAlive()) {

            findNewTarget();
            while ((this.isAlive()) && ((this.getX() != target.getX()) || (this.getY() != target.getY()))) {
                if ((this.getX() == target.getX()) && (this.getY() == target.getY())) {
                    break;
                }
                temp = cloneField(map.getMapArray());
                try {
                    Direction direct = findWay(new Pair(this.getX(), this.getY()), target);
                    model.doMove(this, direct);
                    try {
                        Thread.sleep(Bot.BOT_STEP_DELAY);
                    } catch (InterruptedException ex) {
                        System.out.println("INTERRUPTED EXCEPTION IN BOT THREAD!!!!");
                    }
                } catch (UnsupportedOperationException ex) {
                    System.out.println("Bot can not find the way to the target Pair.");
                    findNewTarget();
                    System.out.println("Bot dreamed new target.");
                }

            }
        }
        System.out.println("Bot is dead!!!");
    }

    private Direction findWay(Pair begin, Pair end) {
        rec(begin, cons);
        //System.out.println(temp.toString());
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
                        throw new UnsupportedOperationException();
                    }
                }
            }
        }
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
