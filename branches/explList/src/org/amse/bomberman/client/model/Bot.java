package org.amse.bomberman.client.model;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Random;
import org.amse.bomberman.client.net.Connector;
import org.amse.bomberman.client.net.IConnector;
import org.amse.bomberman.util.*;
import org.amse.bomberman.util.Constants.Direction;

/**
 * Class for adding bots to the games on server
 * @author michail korovkin
 */
public class Bot extends Thread{
    private Cell target = null;
    // how receive myCoord???
    private Cell myCoord = null;
    // how receive myNumber???
    private int myNumber = -1;
    private BombMap temp;
    private int cons = 1000;
    private IConnector connector = null;
    private boolean isDead;

    /**
     * Constructor, that create new socket and join bot to the game by its gameNumber
     * @param gameNumber number of the game on server, where this bot must be added
     * @param address InetAddress of socket to this bot
     * @param port port to this bot
     * @throws java.io.IOException if occures any troubles with connection to server
     */
    public Bot(int gameNumber, InetAddress address, int port) throws IOException {
        connector = new Connector();
        isDead = false;
        connector.сonnect(address, port);
        // BAD HACK!!! What is another way to know myNumber on the map???
        int buf = getMyNumber(connector.takeGamesList(), gameNumber);
        if (buf!= -1) {
            myNumber = buf;
            connector.joinGame(gameNumber);
        } else {
            throw new IOException("Wrong game for connect.");
        }
    }
    private void findNewTarget() {
        BombMap map = Model.getInstance().getMap();
        int x = 0;
        int y = 0;
        Random random = new Random();
        while (map.getValue(new Cell(x, y)) != Constants.MAP_EMPTY) {
                x = random.nextInt(map.getSize() - 1);
                y = random.nextInt(map.getSize() - 1);
            }
            target = new Cell(x, y);
    }
    private boolean isDead() {
        return (isDead);
    }
    public void kill() {
        isDead = true;
    }
    @Override
    public void run() {
        BombMap map = Model.getInstance().getMap();
        myCoord = findMyCoord(map);
        //Random random = new Random();
        //int x = 0;
        //int y = 0;
        while (!isDead()) {
            //map = Model.getInstance().getMap();
            findNewTarget();
            while ((!isDead()) &&((myCoord.getX() != target.getX())
                    || (myCoord.getY() != target.getY()))) {
                map = Model.getInstance().getMap();
                myCoord = findMyCoord(map);
                if (myCoord == null) {
                    this.kill();
                } else {
                    if ((myCoord.getX() == target.getX()) && (myCoord.getY()
                            == target.getY())) {
                        break;
                    }
                    temp = map.clone();
                    try {
                        Direction direct = findWay(myCoord, target);
                        connector.doMove(direct);
                    } catch (UnsupportedOperationException ex) {
                        System.out.println("Bot can not find the way to the target cell.");
                        findNewTarget();
                        System.out.println("Bot dreamed new target.");
                    }
                }
            }
        }
        System.out.println("Bot is dead!!!");
    }
    private Direction findWay(Cell begin, Cell end) {
        rec(begin, cons);
        //System.out.println(temp.toString());
        int steps = temp.getValue(end) - cons;
        Cell currentCell = end;
        for (int i = steps; i > 1; i --) {
            int x = currentCell.getX();
            int y = currentCell.getY();
            if ((x > 0) && temp.getValue(new Cell (x-1,y))-cons == i-1) {
                currentCell = new Cell(x-1,y);
            } else {
                if ((y > 0) && temp.getValue(new Cell (x,y-1))-cons == i-1) {
                    currentCell = new Cell(x,y-1);
                } else {
                    if ((x < temp.getSize()-1) && temp.getValue(new Cell (x+1,y))
                            -cons == i-1) {
                        currentCell = new Cell(x+1,y);
                    } else {
                        if ((y < temp.getSize()-1) && temp
                                .getValue(new Cell (x,y+1))-cons == i-1) {
                            currentCell = new Cell(x,y+1);
                        }
                    }
                }
            }
        }
        if (currentCell.getX() == begin.getX() + 1) {
            return Direction.DOWN;
        } else {
            if (currentCell.getX() == begin.getX() - 1) {
                return Direction.UP;
            } else {
                if (currentCell.getY() == begin.getY() - 1) {
                    return Direction.LEFT;
                } else {
                    if (currentCell.getY() == begin.getY() + 1) {
                        return Direction.RIGHT;
                    } else {
                        // ???? what is it??? what to do???
                        throw new UnsupportedOperationException();
                    }
                }
            }
        }
    }
    private void rec(Cell current, int steps) {
        int x = current.getX();
        int y = current.getY();
        temp.setCell(current, steps);
        if (y < temp.getSize() - 1) {
            Cell next = new Cell(x, y+1);
            if ((temp.getValue(next) == Constants.MAP_EMPTY)
                    || ((temp.getValue(next) > 1000) && (temp.getValue(next) > steps + 1))) {
                rec(next, steps+1);
            }
        }
        if (y > 0) {
            Cell next = new Cell(x, y-1);
            if ((temp.getValue(next) == Constants.MAP_EMPTY)
                    || ((temp.getValue(next) > 1000) && (temp.getValue(next) > steps + 1))) {
                rec(next, steps+1);
            }
        }
        if (x > 0) {
            Cell next = new Cell(x-1, y);
            if ((temp.getValue(next) == Constants.MAP_EMPTY)
                    || ((temp.getValue(next) > 1000) && (temp.getValue(next) > steps + 1))) {
                rec(next, steps+1);
            }
        }
        if (x < temp.getSize() - 1) {
            Cell next = new Cell(x+1, y);
            if ((temp.getValue(next) == Constants.MAP_EMPTY)
                    || ((temp.getValue(next) > 1000) && (temp.getValue(next) > steps + 1))) {
                rec(next, steps+1);
            }
        }
    }
    private int getMyNumber(ArrayList<String> gameList, int gameNumber) {
        for (String game:gameList) {
            String[] info = game.split(" ");
            if ((int)Integer.parseInt(info[0]) == gameNumber) {
                if ((int)Integer.parseInt(info[3]) < (int)Integer.parseInt(info[4])) {
                    return ((int)Integer.parseInt(info[3]) + 1);
                } else {
                    return -1;
                }
            }
        }
        return -1;
    }
    private Cell findMyCoord(BombMap map) {
        Cell res = null;
        for (int i = 0; i < map.getSize(); i++) {
            for (int j = 0; j < map.getSize(); j++) {
                if (map.getValue(new Cell(i, j)) == myNumber) {
                    res = new Cell(i, j);
                    break;
                }
            }
        }
        return res;
    }
}
