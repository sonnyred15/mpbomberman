package org.amse.bomberman.server.gameservice.gamemap.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.server.gameservice.Field;
import org.amse.bomberman.server.gameservice.gamemap.objects.impl.Bomb;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Pair;
import org.amse.bomberman.server.gameservice.gamemap.objects.impl.Bonus;

/**
 * Class that represents model of game map and provides methods
 * to work with field of this map.
 *
 * @author Kirilchuk V.E.
 */
public class GameMap {
    
    private final String     gameMapName;
    private final Field      field;
    private final List<Bomb> bombs;
    private final List<Pair> explosions;

    /**
     * Constructor of GameMap from square matrix with specified name.
     *
     * @param gameMapName name of this GameMap
     * @param field square matrix of ints.
     */
    public GameMap(String gameMapName, int[][] field) {
        if(field.length != field[0].length) {
            throw new IllegalArgumentException("Field must be square.");
        }
        this.gameMapName = gameMapName;
        this.field = new SimpleField(field);
        this.bombs = new CopyOnWriteArrayList<Bomb>();
        this.explosions = new CopyOnWriteArrayList<Pair>();
    }

    public void move(Pair from, Pair to) {

    }

    //TODO JAVADOC
    public boolean isBlock(Pair position){
        int square = field.getValue(position);
        if (square >= Constants.MAP_PROOF_WALL && square <= -1) {
            return true;
        }
        return false;
    }

    /**
     * Change GameMap for defined in argument number of players by
     * removing unused players from field.
     * 
     * @param maxPlayers number of players to use.
     */
    public void changeMapFor(int playersNum) {
        int maxSupported = field.getMaxPlayers();
        for (int i = playersNum + 1; i <= maxSupported; ++i) {//TODO maybe +1 must be deeper?
            removePlayer(i);
        }
    }

    /**
     * Damage block at the defined position or do nothing if
     * there block is undestroyable. If block was destroyed
     * then there is some probability that bonus will appear instead of it.
     *
     * @param position position of block to damage.
     */
    public void damageBlock(Pair position) {
        int square = field.getValue(position);
        if(square == Constants.MAP_PROOF_WALL) {
            return;
        }

        square +=1; // note that blocks has negative values.
        field.setValue(position, square);
        //if after damage block was destroyed then bonus can appear.
        if (square == Constants.MAP_EMPTY) {
            tryGenerateBonus(position);
        }
    }

    private void tryGenerateBonus(Pair position) {
        Bonus randomBonus = Bonus.randomBonus();
        if (randomBonus == null) {
            field.setValue(position, Constants.MAP_EMPTY);
        } else {
            field.setValue(position, randomBonus.getID());
        }
    }

    /**
     * @return dimension of field.
     */
    public int getDimension() {
        return field.getDimension();
    }

    /**
     * @return field of this game map.
     */
    public Field getField() {
        return field;
    }

    /**
     * @return max players num supported by this GameMap.
     */
    public int getMaxPlayers() {
        return field.getMaxPlayers();
    }

    /**
     * @return name of this GameMap.
     */
    public String getName() {
        return this.gameMapName;
    }

    /**
     * Checks if there is bomb at the defined position.
     *
     * @param position position to check.
     * @return true if there is bomb, false otherwise.
     */
    public boolean isBomb(Pair position) {
        return (field.getValue(position) == Constants.MAP_BOMB);
    }

    /**
     * Checks if there is bonus at the defined position.
     *
     * @param position position to check.
     * @return true if there is bonus, false otherwise.
     */
    public boolean isBonus(Pair position) {
        return Bonus.isBonus(field.getValue(position));
    }

    /**
     * Checks if field at the sdefined position is empty.
     *
     * @param position position to check.
     * @return true if there is empty cell, false otherwise.
     */
    public boolean isEmpty(Pair position) {
        return (field.getValue(position) == Constants.MAP_EMPTY);
    }

    public boolean isExplosion(Pair position) {
        return explosions.contains(position);
    }

    /**
     * Returns int which is ID that represents player staying at the defined
     * position.
     * <p> -1 if there is no player.
     *
     * @param position position to check.
     * @return int which is ID that represents player or -1 if
     * there is no player.
     */
    public int playerIdAt(Pair position) {
        int square = field.getValue(position);

        if ((square < 1) || (square > Constants.MAX_PLAYERS)) {
            return -1;
        }

        return square;
    }

    /**
     * Remove concrete player from gameMap.
     * 
     * @param playerId player to remove.
     */
    public void removePlayer(int playerId) {
        int dim = field.getDimension();

        Pair pair = new Pair();
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                pair.setX(i);
                pair.setY(j);
                if (field.getValue(pair) == playerId) {
                    field.setValue(pair, Constants.MAP_EMPTY);

                    return;    // a little bit optimize
                }
            }
        }
    }

    //TODO JAVADOC
    public Pair getPlayerPosition(int playerId) {
        int dim = field.getDimension();
        Pair pair = new Pair();
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                pair.setX(i);
                pair.setY(j);
                if (field.getValue(pair) == playerId) {
                    return pair;
                }
            }
        }

        return null;
    }

    public void setValue(Pair position, int value) {
        field.setValue(position, value);
    }

    public int getValue(Pair position) {
        return field.getValue(position);
    }

    public List<Bomb> getBombs() {
        return bombs;
    }

    public List<Pair> getExplosions() {
        return explosions;
    }

    public String getGameMapName() {
        return gameMapName;
    }
}
