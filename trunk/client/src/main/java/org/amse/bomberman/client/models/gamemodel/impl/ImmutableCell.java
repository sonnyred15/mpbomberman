package org.amse.bomberman.client.models.gamemodel.impl;

import org.amse.bomberman.client.models.gamemodel.Cell;
import org.amse.bomberman.util.Direction;

/**
 * Class that represents gameMap cell.
 * Immutable.
 * 
 * @author Michail Korovkin
 */
public class ImmutableCell implements Cell {

    private int myX;
    private int myY;

    public ImmutableCell(int x, int y) {
        myX = x;
        myY = y;
    }

    @Override
    public int getX() {
        return myX;
    }

    @Override
    public int getY() {
        return myY;
    }

    @Override
    public ImmutableCell nextCell(Direction direction) {
        switch(direction) {
            case LEFT: {
                return new ImmutableCell(myX, myY - 1);
            }
            case RIGHT: {
                return new ImmutableCell(myX, myY + 1);
            }
            case UP: {
                return new ImmutableCell(myX - 1, myY);
            }
            case DOWN: {
                return new ImmutableCell(myX + 1, myY);
            }
            default: {
                throw new RuntimeException("Unsupported direction");
            }
        }
    }
}
