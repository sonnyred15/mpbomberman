package org.amse.bomberman.server.gameservice.gamemap.impl;

import org.amse.bomberman.server.gameservice.Field;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Pair;

/**
 * ThreadSafe.
 * @author Kirilchuk V.E.
 */
public class SimpleField implements Field {
    private final int maxPlayers;
    private final int[][] field;

    public SimpleField(int[][] field) {//TODO what if field is not square?
        this.field = field;
        this.maxPlayers = countMaxPlayers(field);
    }

    /**
     * Returns max players num of this field.
     * @param field square matrix of field.
     * @return max players num of this field.
     */
    private int countMaxPlayers(int[][] field) {
        int counter = 0;
        int dim = field.length;

        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                int square = field[i][j];

                if ((square > 0) && (square < Constants.MAX_PLAYERS)) {
                    ++counter;
                    field[i][j] = counter;
                }
            }
        }

        return counter;
    }

    /**
     * @return max players for this field.
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }
    
    /**
     * Sets value at specified position.
     * 
     * @param position coordinate on field where to set value.
     * @param value value to set.
     */
    public void setValue(Pair position, int value) {
        int x = position.getX();
        int y = position.getY();

        synchronized (field) {
            this.field[x][y] = value;
        }
    }

    /**
     * @param position coordinate on field to get value from.
     * @return value at specified position.
     */
    public int getValue(Pair position) {
        int x = position.getX();
        int y = position.getY();
        
        synchronized(field) {
            return field[x][y];
        }
    }

    /**
     * @return field dimension.
     */
    public int getDimension() {//TODO what`s about non square?
        return field.length;
    }

    public int[][] getField() {
        return field;
    }
}
