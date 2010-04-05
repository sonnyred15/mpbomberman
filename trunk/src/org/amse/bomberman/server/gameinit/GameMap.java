
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.util.Pair;
import org.amse.bomberman.server.Main;
import org.amse.bomberman.util.Constants;

//~--- JDK imports ------------------------------------------------------------

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Random;

/**
 *
 * @author Kirilchuk V.E.
 */
public final class GameMap {
    private int           maxPlayers = Constants.MAX_PLAYERS;
    private final int     dimension;
    private final int[][] field;
    private final String  gameMapName;

    public GameMap(int[][] gameMapArray) {    // CHECK < THIS// What if we get non square matrix???
        this.dimension = gameMapArray.length;
        this.field = gameMapArray;
        this.maxPlayers = countMaxPlayers(this.field);
        this.gameMapName = "intArrayMap";
    }

    /**
     * Creates map from file.
     * @param fileName file with map.
     * @throws java.io.FileNotFoundException if no file with such name found
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

            // reader = new BufferedReader(new FileReader(fileName));
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

    public int blockAt(Pair position) {
        return this.blockAt(position.getX(), position.getY());
    }

    /**
     *
     * @param x
     * @param y
     * @return 1 if block undestroyable.
     * n in [2..8] if destroyable by n mines.
     * -1 if it is not block.
     */
    public int blockAt(int x, int y) {
        int square = this.field[x][y];

        if ((square >= 0) || (square < -8)) {
            return -1;
        }

        return square + 9;
    }

    public int bonusAt(Pair position) {
        return this.bonusAt(position.getX(), position.getY());
    }

    /**
     * return value of bonus at specific cell with coords x and y.
     * @param x coord in line.
     * @param y coord in column.
     * @return integer value of bonus and -1 if it is not a bonus.
     */
    public int bonusAt(int x, int y) {
        if ((this.field[x][y] == Constants.MAP_BONUS_BOMB_COUNT)
                || (this.field[x][y] == Constants.MAP_BONUS_BOMB_RADIUS)
                || (this.field[x][y] == Constants.MAP_BONUS_LIFE)) {
            return this.field[x][y];
        } else {
            return -1;
        }
    }

    /**
     * Change GameMap for defined in argument number of players by
     * removing unused players from GameMap.
     * @param maxPlayers number of players to use
     */
    public void changeMapForCurMaxPlayers(int curMaxPlayers) {
        for (int i = curMaxPlayers + 1; i <= this.maxPlayers; ++i) {
            removePlayer(i);
        }
    }

    // count maxPlayers for this mapArray.
    private int countMaxPlayers(int[][] mapArray) {
        int counter = 0;
        int dim = this.dimension;

        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                int sq = mapArray[i][j];

                if ((sq > 0) && (sq < Constants.MAX_PLAYERS)) {
                    ++counter;
                    mapArray[i][j] = counter;
                }
            }
        }

        return counter;
    }

    public void destroyBlock(Pair position) {
        this.destroyBlock(position.getX(), position.getY());
    }

    public void destroyBlock(int x, int y) {
        if (this.field[x][y] != -1) {
            return;
        }

        Random generator = new Random();
        int    random = generator.nextInt(99);

        if (random < 5) {
            this.field[x][y] = Constants.MAP_BONUS_LIFE;
        } else {
            if (random < 10) {
                this.field[x][y] = Constants.MAP_BONUS_BOMB_COUNT;
            } else {
                if (random < 15) {
                    this.field[x][y] = Constants.MAP_BONUS_BOMB_RADIUS;
                } else {
                    this.field[x][y] = Constants.MAP_EMPTY;
                }
            }
        }
    }

    public int getDimension() {
        return this.dimension;
    }

    public int[][] getField() {
        return this.field;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public String getName() {
        return this.gameMapName;
    }

    public int getSquare(Pair position) {
        return this.getSquare(position.getX(), position.getY());
    }

    public int getSquare(int x, int y) {
        return this.field[x][y];
    }

    public boolean isBomb(Pair position) {
        return this.isBomb(position.getX(), position.getY());
    }

    /**
     *
     * @param x
     * @param y
     * @return True if map[x][y]=-16
     */
    public boolean isBomb(int x, int y) {
        return (this.field[x][y] == Constants.MAP_BOMB);
    }

    public boolean isBonus(Pair position) {
        return this.isBonus(position.getX(), position.getY());
    }

    public boolean isBonus(int x, int y) {
        return ((this.field[x][y] == Constants.MAP_BONUS_LIFE)
                || (this.field[x][y] == Constants.MAP_BONUS_BOMB_RADIUS)
                || (this.field[x][y] == Constants.MAP_BONUS_BOMB_COUNT));
    }

    public boolean isEmpty(Pair position) {
        return this.isEmpty(position.getX(), position.getY());
    }

    public boolean isEmpty(int x, int y) {
        return this.field[x][y] == Constants.MAP_EMPTY;
    }

    public int playerIDAt(Pair position) {
        return this.playerIDAt(position.getX(), position.getY());
    }

    /**
     * players 1..15
     * @param x
     * @param y
     * @return PayerId if there is player.
     * Returns -1 if there is NO player.
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
     * @param playerID
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

    public void setSquare(Pair position, int value) {
        this.setSquare(position.getX(), position.getY(), value);
    }

    public void setSquare(int x, int y, int value) {
        this.field[x][y] = value;
    }
}
