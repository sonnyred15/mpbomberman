/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.amse.bomberman.util.Constants;

/**
 *
 * @author Kirilchuk V.E.
 */
public final class GameMap {

    private final int dimension;
    private final int[][] mapArray;
    private final String mapName;
    private final List<Pair> explosionSquares = new ArrayList<Pair>();
    private final List<Bomb> bombs = new ArrayList<Bomb>();
    private int maxPlayers = Constants.MAX_PLAYERS;

    public GameMap(int[][] mapArray) { //CHECK < THIS// What if we get non square matrix???
        this.dimension = mapArray.length;
        this.mapArray = mapArray;
        this.maxPlayers = countMaxPlayers(this.mapArray);
        this.mapName = "intArrayMap";
    }

    /**
     * Creates map from file.
     * @param fileName file with map.
     * @throws java.io.FileNotFoundException if no file with such name found
     * @throws java.io.IOException if an error occurs while reading from file.
     */
    public GameMap(String fileName) throws FileNotFoundException, IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
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

            this.mapArray = arr;
            this.maxPlayers = countMaxPlayers(this.mapArray);
            this.mapName = fileName;
        } catch (NumberFormatException ex) {
            throw new IOException("Incorrect map.");
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public List<Pair> getExplosionSquares() {
        return explosionSquares;
    }

    public int[][] getMapArray() {
        return this.mapArray;
    }

    public int getDimension() {
        return this.dimension;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public String getName() {
        return this.mapName;
    }

    public int getSquare(int x, int y) {
        return this.mapArray[x][y];
    }

    public void setSquare(int x, int y, int value) {
        this.mapArray[x][y] = value;
    }

    /**
     * players 1..15
     * @param x
     * @param y
     * @return PayerId if there is player.
     * Returns -1 if there is NO player.
     */
    public int playerIdAt(int x, int y) {
        int square = this.mapArray[x][y];
        if (square < 1 || square > Constants.MAX_PLAYERS) {
            return -1;
        }
        return square;
    }

    /**
     *
     * @param x
     * @param y
     * @return True if map[x][y]=-16
     */
    public boolean isBomb(int x, int y) {
        return (this.mapArray[x][y] == Constants.MAP_BOMB);
    }

    public boolean isExplosion(Pair coords) {
        return this.explosionSquares.contains(coords);
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
        int square = this.mapArray[x][y];
        if (square >= 0 || square < -8) {
            return -1;
        }
        return square + 9;
    }

    public int bonusAt(int x, int y) {
        throw new UnsupportedOperationException("Not supported yet");
    }

    public boolean isEmpty(int x, int y) {
        return this.mapArray[x][y] == Constants.MAP_EMPTY;
    }

    public void addBomb(Bomb bomb) {
        this.bombs.add(bomb);
        this.setSquare(bomb.getX(), bomb.getY(), Constants.MAP_BOMB);
    }

    public void addExplosions(List<Pair> explSq) {
        this.explosionSquares.addAll(explSq);
    }

    public void detonateBomb(int x, int y) {
        Bomb bombToDetonate = null;
        for (Bomb bomb : bombs) {
            if (bomb.getX() == x && bomb.getY() == y) {
                bombToDetonate = bomb;
                break;
            }
        }
        bombToDetonate.detonate();
    }

    public void bombStartDetonating(Bomb bomb) {
            this.bombs.remove(bomb);
    }

    void removeExplosion(Pair explosion) {
        this.explosionSquares.remove(explosion);
    }

    //count maxPlayers for this mapArray.
    private int countMaxPlayers(int[][] mapArray) {
        int counter = 0;
        int dim = this.dimension;
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                int sq = mapArray[i][j];
                if (sq > 0 && sq < Constants.MAX_PLAYERS) {
                    ++counter;
                    mapArray[i][j] = counter;
                }
            }
        }
        return counter;
    }
    //remove concrete player from map.

    public void removePlayer(int playerID) {
        int dim = this.dimension;
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (this.mapArray[i][j] == playerID) {
                    this.mapArray[i][j] = 0;
                    return; //a little bit optimize
                }
            }
        }
    }
    //remove `unused` players from map

    public void changeMapForCurMaxPlayers(int curMaxPlayers) {
        for (int i = curMaxPlayers + 1; i <= this.maxPlayers; ++i) {
            removePlayer(i);
        }
    }
}
