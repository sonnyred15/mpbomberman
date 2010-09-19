package org.amse.bomberman.client.model;

import org.amse.bomberman.util.Constants.Direction;

/**
 * @author Michail Korovkin
 */
public class Cell {
    private int myX;
    private int myY;

    public Cell(int x, int y) {
        myX = x;
        myY = y;
    }
    public int getX() {
        return myX;
    }
    public int getY() {
        return myY;
    }

    public Cell nextCell(Direction direction) throws UnsupportedOperationException {
        switch (direction) {
            case LEFT: {
                return new Cell(myX, myY - 1);
            }
            case RIGHT: {
                return new Cell(myX, myY + 1);
            }
            case UP: {
                return new Cell(myX - 1, myY);
            }
            case DOWN: {
                return new Cell(myX + 1, myY);
            }
            default:
                throw new UnsupportedOperationException("Unregistered ERROR!!!");
        }
    }
}
