
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.Main;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Pair;

//~--- JDK imports ------------------------------------------------------------

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.amse.bomberman.server.gameinit.imodel.Bonus;

/**
 * Class that represents game field and stuff to work with this field.
 * @author Kirilchuk V.E.
 */
public final class GameMap {

    private int           maxPlayers = Constants.MAX_PLAYERS;
    private final int     dimension;
    private final int[][] field;
    private final String  gameMapName;

    /**
     * Constructor of GameMap from square matrix.
     * @param field square matrix of ints.
     * @param gameMapName name of this GameMap
     * @param dimension
     * @param maxPlayers
     */
    public GameMap(String gameMapName, int[][] field, int dimension, int maxPlayers) {
        assert (field.length==field[0].length);
        this.gameMapName = gameMapName;
        this.field = field;
        this.dimension = dimension;
        this.maxPlayers = maxPlayers;
    }

    /**
     * Creates GameMap from file.
     * @param fileName file with field.
     * @throws java.io.FileNotFoundException if no file with such name found.
     * @throws java.io.IOException if an error occurs while reading from file.
     */
    public GameMap(String fileName) throws FileNotFoundException,
                                           IOException {
        InputStream is =
            Main.class.getResourceAsStream("/org/amse/bomberman/server/resources/" +
                                           fileName);

        if (is == null) {
            throw new FileNotFoundException("null returned by getResourceAsStream." +
                                            fileName);
        }

        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader    reader = null;

        try {
            reader = new BufferedReader(isr);

            int buf = Integer.parseInt(reader.readLine());

            this.dimension = buf;

            int[][] arr = new int[buf][buf];

            for (int i = 0; i < buf; i++) {
                String[] line = reader.readLine().split(" ");

                if (line.length != buf) {
                    throw new IOException("Incorrect map.");
                }

                for (int j = 0; j < buf; j++) {
                    arr[i][j] = Integer.parseInt(line[j]);
                }
            }

            this.field = arr;
            this.maxPlayers = countMaxPlayers(this.field);
            this.gameMapName = fileName;
        } catch (NumberFormatException ex) {
            throw new IOException("Incorrect map.");
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public boolean isBlock(Pair position){
        return this.isBlock(position.getX(), position.getY());
    }

    public boolean isBlock(int x, int y) {
        int square = this.field[x][y];

        if (square >= Constants.MAP_PROOF_WALL && square <= -1) {
            return true;
        }

        return false;
    }

    /**
     * Change GameMap for defined in argument number of players by
     * removing unused players from GameMap.
     * @param maxPlayers number of players to use.
     */
    public void changeMapForCurMaxPlayers(int curMaxPlayers) {
        for (int i = curMaxPlayers + 1; i <= this.maxPlayers; ++i) {
            removePlayer(i);
        }
    }

    /**
     * Damage block at the defined position or do nothing if
     * there block is undestroyable. If block was destroyed
     * then there is some probability that bonus will appear instead of it.
     * @param position position of block to damage.
     */
    public void damageBlock(Pair position) {
        this.damageBlock(position.getX(), position.getY());
    }

    /**
     * Damage block at the defined position or do nothing if
     * there block is undestroyable. If block was destroyed
     * then there is some probability that bonus will appear instead of it.
     * @param x position by x.
     * @param y position by y.
     */
    public void damageBlock(int x, int y) {
        if (this.field[x][y] == Constants.MAP_PROOF_WALL) {
            return;
        }

        this.field[x][y] += 1; // note that blocks has negative values.

        //if after damage block was destroyed then bonus can appear.
        if (this.field[x][y] == Constants.MAP_EMPTY) {
            generateBonusAndAddToField(x, y);
        }
    }

    private void generateBonusAndAddToField(int x, int y) {
        Bonus randomBonus = Bonus.randomBonus();
        if (randomBonus == null) {
            this.field[x][y] = Constants.MAP_EMPTY;
        } else {
            this.field[x][y] = randomBonus.getID();
        }
    }

    /**
     * Returns the dimension of field.
     * @return the dimension of field.
     */
    public int getDimension() {
        return this.dimension;
    }

    /**
     * Returnes field matrix.
     * @return field matrix.
     */
    public int[][] getField() {
        return this.field;
    }

    /**
     * Returnes max players num supported by this GameMap.
     * @returnmax players num supported by this GameMap.
     */
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    /**
     * Returnes name of this GameMap.
     * @return
     */
    public String getName() {
        return this.gameMapName;
    }

    /**
     * Returns int that represents some object at the specified position.
     * @param position position to check.
     * @return int that represents some object at the specified position.
     */
    public int getSquare(Pair position) {
        return this.getSquare(position.getX(), position.getY());
    }

    /**
     * Returns int that represents some object at the specified position.
     * @param x position by x.
     * @param y position by y.
     * @return int that represents some object at the specified position.
     */
    public int getSquare(int x, int y) {
        return this.field[x][y];
    }

    /**
     * Checks if there is bomb at the defined position.
     * @param position position to check.
     * @return true if there is bomb, false otherwise.
     */
    public boolean isBomb(Pair position) {
        return this.isBomb(position.getX(), position.getY());
    }

    /**
     * Checks if there is bomb at the defined position.
     * @param x position by x.
     * @param y position by y.
     * @return true if there is bomb, false otherwise.
     */
    public boolean isBomb(int x, int y) {
        return (this.field[x][y] == Constants.MAP_BOMB);
    }

    /**
     * Checks if there is bonus at the defined position.
     * @param position position to check.
     * @return true if there is bonus, false otherwise.
     */
    public boolean isBonus(Pair position) {
        return this.isBonus(position.getX(), position.getY());
    }

    /**
     * Checks if there is bonus at the defined position.
     * @param x position by x.
     * @param y position by y.
     * @return true if there is bomb, false otherwise.
     */
    public boolean isBonus(int x, int y) {
        return Bonus.isBonus(this.field[x][y]);
    }

    /**
     * Checks if field at the sdefined position is empty.
     * @param position position to check.
     * @return true if there is empty cell, false otherwise.
     */
    public boolean isEmpty(Pair position) {
        return this.isEmpty(position.getX(), position.getY());
    }

    /**
     * Checks if field at the sdefined position is empty.
     * @param x position by x.
     * @param y position by y.
     * @return true if there is empty cell, false otherwise.
     */
    public boolean isEmpty(int x, int y) {
        return this.field[x][y] == Constants.MAP_EMPTY;
    }

    /**
     * Returns int which is ID that represents player staying at the defined
     * position.
     * <p> -1 if there is no player.
     * @param position position to check.
     * @return int which is ID that represents player or -1 if
     * there is no player.
     */
    public int playerIDAt(Pair position) {
        return this.playerIDAt(position.getX(), position.getY());
    }

    /**
     * Returns int which is ID that represents player staying at the defined
     * position.
     * @param x position by x.
     * @param y position by y.
     * @return int which is ID that represents player or -1 if
     * there is no player.
     */
    public int playerIDAt(int x, int y) {
        int square = this.field[x][y];

        if ((square < 1) || (square > Constants.MAX_PLAYERS)) {
            return -1;
        }

        return square;
    }

    /**
     * Remove concrete player from gameMap.
     * @param playerID player to remove.
     */
    public void removePlayer(int playerID) {
        int dim = this.dimension;

        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (this.field[i][j] == playerID) {
                    this.field[i][j] = Constants.MAP_EMPTY;

                    return;    // a little bit optimize
                }
            }
        }
    }

    /**
     * Setting value to square of field at the defined position.
     * @param position position to set at.
     * @param value value to set.
     */
    public void setSquare(Pair position, int value) {
        this.setSquare(position.getX(), position.getY(), value);
    }

    /**
     * Setting value to square of field at the defined position.
     * @param x position by x.
     * @param y position by y.
     * @param value value to set.
     */
    public void setSquare(int x, int y, int value) {
        this.field[x][y] = value;
    }

    /**
     * Returns max players num of this GameMap.
     * @param field square matrix of GameMap.
     * @return max players num of this GameMap.
     */
    private int countMaxPlayers(int[][] field) {
        int counter = 0;
        int dim = this.dimension;

        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                int sq = field[i][j];

                if ((sq > 0) && (sq < Constants.MAX_PLAYERS)) {
                    ++counter;
                    field[i][j] = counter;
                }
            }
        }

        return counter;
    }
}
