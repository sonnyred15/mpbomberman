package org.amse.bomberman.server.gameinit;

import java.util.Random;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.amse.bomberman.server.gameinit.imodel.IModel;
import org.amse.bomberman.util.*;
import org.amse.bomberman.util.Constants.Direction;

/**
 * Class for adding bots to the games on server
 * @author michail korovkin
 */
public class Bot extends Player implements Runnable {

    private Player player;
    private IModel model;
    private Pair target;
    private int cons = 1000; //WHAT IS THIS??
    private int[][] temp;

    public Bot(String nickName, int id, IModel model) {
        this.player = new Player(nickName, id);
        this.model = model;
    }

    @Override
    public void setDieListener(DieListener gameDieListener) {
        this.player.setDieListener(gameDieListener);
    }

    @Override
    public String getInfo() {
        return this.player.getInfo();
    }

    @Override
    public boolean isAlive() {
        return this.player.isAlive();
    }

    @Override
    public boolean canPlaceBomb() {
        return this.player.canPlaceBomb();
    }

    @Override
    public void placedBomb() {
       this.player.placedBomb();
    }

    @Override
    public void detonatedBomb() {
        this.player.detonatedBomb();
    }

    @Deprecated
    @Override
    public void takedBonus(int bonus) {//Bonuses must be enum!
        this.player.takedBonus(bonus);
    }

    @Override
    public void setID(int id) {
        this.player.setID(id);
    }

    @Override
    public int getID() {
        return this.player.getID();
    }

    @Override
    public int getX() {
        return this.player.getX();
    }

    @Override
    public int getY() {
        return this.player.getY();
    }

    @Override
    public void setX(int x) {
        this.player.setX(x);
    }

    @Override
    public void setY(int y) {
        this.player.setY(y);
    }

    @Override
    public void bombed() { //synchronized(player)
        this.player.bombed();
    }

    @Override
    public int getRadius() {
        return this.player.getRadius();
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

        while (this.player.isAlive()) {

            findNewTarget();
            while ((this.player.isAlive()) && ((this.player.getX() != target.getX()) || (this.player.getY() != target.getY()))) {
                if ((this.player.getX() == target.getX()) && (this.player.getY() == target.getY())) {
                    break;
                }
                temp = cloneField(map.getMapArray());
                try {
                    Direction direct = findWay(new Pair(this.player.getX(), this.player.getY()), target);
                    model.doMove(this.player, direct);
                    try {
                        Thread.sleep(Constants.GAME_STEP_TIME);
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
