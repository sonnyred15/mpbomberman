
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit.imodel.impl;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.Bomb;
import org.amse.bomberman.server.gameinit.DieListener;
import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.GameMap;
import org.amse.bomberman.server.gameinit.MoveableObject;
import org.amse.bomberman.server.gameinit.Pair;
import org.amse.bomberman.server.gameinit.Player;
import org.amse.bomberman.server.gameinit.bot.Bot;
import org.amse.bomberman.server.gameinit.bot.RandomFullBotStrategy;
import org.amse.bomberman.server.gameinit.imodel.IModel;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Constants.Direction;

//~--- JDK imports ------------------------------------------------------------

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Model that is responsable for game rules and responsable for connection
 * between GameMap and Game.
 * @author Kirilchuk V.E.
 */
public class Model implements IModel {
    private final List<Bomb>               bombs;
    private final DieListener              dieListener;
    private final List<Pair>               explosionSquares;
    private final List<Integer>            freeIDs;
    private final Game                     game;
    private final GameMap                  gameMap;
    private final List<Player>             players;
    private final ScheduledExecutorService timer;

    /**
     * Constructor of Model.
     * @param map GameMap that correspond to game
     * @param game Game for which model was created
     */
    public Model(GameMap gameMap, Game game) {
        this.gameMap = gameMap;
        this.game = game;
        this.timer = Executors.newSingleThreadScheduledExecutor();
        this.bombs = new CopyOnWriteArrayList<Bomb>();
        this.players = new CopyOnWriteArrayList<Player>();

        Integer[] freeIDArray = new Integer[this.game.getMaxPlayers()];

        for (int i = 0; i < freeIDArray.length; ++i) {
            freeIDArray[i] = i + 1;
        }

        this.freeIDs = new CopyOnWriteArrayList<Integer>(freeIDArray);
        this.explosionSquares = new CopyOnWriteArrayList<Pair>();
        this.dieListener = new DieListener(this);
    }

    // TODO is this is normal realization?
    public Player addBot(String botName) {
        Bot bot = new Bot(botName, this.game, this,
                          new RandomFullBotStrategy());

        this.players.add(bot);

        // bot.setID(this.players.indexOf(bot) + 1);
        bot.setID(getFreeID());
        bot.setDieListener(this.dieListener);

        return bot;
    }

    public void addExplosions(List<Pair> explSq) {
        this.explosionSquares.addAll(explSq);

//      this.game.notifyGameMapUpdateListeners();
    }

    // TODO is this is normal realization?
    public Player addPlayer(String name) {
        Player playerToAdd = new Player(name);

        // playerToAdd.setID(this.players.indexOf(playerToAdd) + 1);
        playerToAdd.setID(getFreeID());
        this.players.add(playerToAdd);
        playerToAdd.setDieListener(this.dieListener);

        return playerToAdd;
    }

    public void bombDetonated(Bomb bomb) {
        this.bombs.remove(bomb);

        Pair bombPosition = bomb.getPosition();

        for (Player player : players) {
            if (player.getPosition().equals(bombPosition) && player.isAlive()) {
                this.gameMap.setSquare(bombPosition.getX(),
                                       bombPosition.getY(), player.getID());
            } else {
                this.gameMap.setSquare(bombPosition.getX(),
                                       bombPosition.getY(),
                                       Constants.MAP_EMPTY);
            }
        }

//      this.gameMap.setSquare(bombPosition.getX(), bombPosition.getY(),
//                             Constants.MAP_DETONATED_BOMB);
//      this.game.notifyGameMapUpdateListeners();
    }

    public void detonateBombAt(int x, int y) {
        Bomb bombToDetonate = null;
        Pair square = new Pair(x, y);

        for (Bomb bomb : bombs) {
            if (bomb.getPosition().equals(square)) {
                bombToDetonate = bomb;

                break;
            }
        }

        bombToDetonate.detonate();
    }

    public boolean doMove(Player player, Direction direction) {
        return this.tryDoMove(player, direction);
    }

    public int getCurrentPlayersNum() {
        return this.players.size();
    }

    /**
     * Return list of explosions.
     * @return List of explosions
     */
    public List<Pair> getExplosionSquares() {
        return this.explosionSquares;
    }

    private int getFreeID() {
        return this.freeIDs.remove(0);
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    /**
     * Return matrix of GameMap.
     * @return matrix of GameMap
     */
    public int[][] getGameMapArray() {
        return this.gameMap.getField();
    }

    /**
     * Return name of GameMap of this Model.
     * @return Name of GameMap in String
     */
    public String getGameMapName() {
        return this.gameMap.getName();
    }

    public Player getPlayer(int playerID) {
        for (Player player : players) {
            if (player.getID() == playerID) {
                return player;
            }
        }

        return null;
    }

    public List<Player> getPlayersList() {
        return Collections.unmodifiableList(players);
    }

    public boolean isExplosion(Pair coords) {
        return this.explosionSquares.contains(coords);
    }

    private boolean isMoveToReserved(int x, int y) {    // note that on explosions isEmpty = true!!!
        return (this.gameMap.isEmpty(x, y)) ? false
                                            : true;
    }

    private boolean isOutMove(int x, int y) {
        int dim = this.gameMap.getDimension();

        if ((x < 0) || (x > dim - 1)) {
            return true;
        }

        if ((y < 0) || (y > dim - 1)) {
            return true;
        }

        return false;
    }

    private void makeMove(MoveableObject objectToMove, int newX, int newY) {
        int x = objectToMove.getPosition().getX();
        int y = objectToMove.getPosition().getY();

        if (objectToMove instanceof Player) {
            if (this.gameMap.isBomb(x, y)) {    // if player setted mine but still in same square
                this.gameMap.setSquare(x, y, Constants.MAP_BOMB);
            } else {
                this.gameMap.setSquare(x, y, Constants.MAP_EMPTY);
            }
        } else if (objectToMove instanceof Bomb) {
            Bomb bomb = (Bomb) objectToMove;

            if (bomb.getOwner().getPosition().equals(bomb.getPosition())) {
                this.gameMap.setSquare(x, y, bomb.getOwner().getID());
            } else {
                this.gameMap.setSquare(x, y, Constants.MAP_EMPTY);
            }
        }

        this.gameMap.setSquare(newX, newY, objectToMove.getID());

        Pair newPosition = new Pair(newX, newY);

        objectToMove.setPosition(newPosition);

        // if player is making move to explosion zone.
        if (isExplosion(newPosition)) {
            objectToMove.bombed();
        }
    }

    private int[] newCoords(Pair currentPosition, Direction direction) {    // whats about catch illegalArgumentException???
        int[] arr = new int[2];

        switch (direction) {
            case DOWN : {
                arr[0] = currentPosition.getX() + 1;
                arr[1] = currentPosition.getY();

                break;
            }

            case LEFT : {
                arr[0] = currentPosition.getX();
                arr[1] = currentPosition.getY() - 1;

                break;
            }

            case UP : {
                arr[0] = currentPosition.getX() - 1;
                arr[1] = currentPosition.getY();

                break;
            }

            case RIGHT : {
                arr[0] = currentPosition.getX();
                arr[1] = currentPosition.getY() + 1;

                break;
            }

            default : {
                throw new IllegalArgumentException("Default block " +
                        "in switch(ENUM). Error in code.");
            }
        }

        return arr;
    }

    public void playerBombed(Player atacker, int victimID) {
        Player victim = this.getPlayer(victimID);

        victim.bombed();
        this.game.addMessageToChat(victim,
                                   "was bombed by " + atacker.getNickName());
    }

    public void playerBombed(Player atacker, Player victim) {
        victim.bombed();
        this.game.addMessageToChat(victim,
                                   "was bombed by " + atacker.getNickName());
    }

    public void playerDied(Player player) {
        this.gameMap.removePlayer(player.getID());
    }

    /**
     * Printing matrix of GameMap to console. Maybe would be deleted soon.
     */
    public void printToConsole() {    // useless?
        int dim = this.gameMap.getDimension();

        for (int i = 0; i < dim; i++) {
            System.out.println();

            for (int j = 0; j < dim; j++) {
                System.out.print(this.gameMap.getSquare(i, j) + " ");
            }
        }

        System.out.println();
    }

    public void removeExplosion(Pair explosion) {
        this.explosionSquares.remove(explosion);

//      this.game.notifyGameMapUpdateListeners();
    }

    public void removePlayer(int playerID) {
        for (Player player : players) {
            if (player.getID() == playerID) {
                this.players.remove(player);
                this.freeIDs.add(playerID);
                this.gameMap.removePlayer(playerID);
            }
        }
    }

    public void startup() {
        this.gameMap.changeMapForCurMaxPlayers(this.players.size());

        int playerX;
        int playerY;

        for (Player player : players) {
            playerX = this.xCoordOf(player.getID());
            playerY = this.yCoordOf(player.getID());

            Pair playerCoords = new Pair(playerX, playerY);

            player.setPosition(playerCoords);
        }
    }

    private void takeBonus(Player player, int x, int y) {
        int bonus = this.gameMap.bonusAt(x, y);

        if (bonus == -1) {
            return;
        } else {
            this.gameMap.setSquare(player.getPosition().getX(),
                                   player.getPosition().getY(),
                                   Constants.MAP_EMPTY);
            player.setPosition(new Pair(x, y));
            player.takeBonus(bonus);
            this.gameMap.setSquare(x, y, player.getID());
        }
    }

    /**
     * Trying to move the player in defined direction.
     * @param player Player to move
     * @param direction Direction of move
     * @return true if player moved, false otherwise
     */
    public boolean tryDoMove(MoveableObject objToMove, Direction direction) {    // TODO synchronization?
        synchronized (gameMap) {
            synchronized (objToMove) {
                int arr[] = newCoords(objToMove.getPosition(), direction);
                int newX = arr[0];
                int newY = arr[1];

                if (!isOutMove(newX, newY)) {
                    if (this.gameMap.isBomb(newX, newY)
                            && (objToMove instanceof Player)) {
                        Bomb bombToMove = null;

                        for (Bomb bomb : bombs) {
                            if (bomb.getPosition().equals(new Pair(newX,
                                                                   newY))) {
                                bombToMove = bomb;
                                tryDoMove(bombToMove, direction);

                                break;
                            }
                        }
                    } else if (this.gameMap.isBonus(newX, newY)
                               && (objToMove instanceof Player)) {
                        takeBonus((Player) objToMove, newX, newY);
                    }

                    if (!isMoveToReserved(newX, newY)) {
                        makeMove(objToMove, newX, newY);

//                      this.game.notifyGameMapUpdateListeners();
                        return true;
                    }
                }

                return false;
            }
        }
    }

    /**
     * Trying to place bomb of defined player.
     * @param player Player which trying to place bomb.
     */
    public boolean tryPlaceBomb(Player player) {    // whats about synchronization??
        synchronized (gameMap) {
            synchronized (player) {    // whats about syncronize(map)???
                if (player.canPlaceBomb()) {    // player is alive and have bombs to set up
                    int x = player.getPosition().getX();
                    int y = player.getPosition().getY();

                    if (this.gameMap.isBomb(x, y)) {
                        return false;    // if player staying under the bomb
                    }

                    Bomb bomb = new Bomb(this, player, gameMap, new Pair(x, y),
                                         timer);

                    this.bombs.add(bomb);

                    Pair bombPosition = bomb.getPosition();

                    this.gameMap.setSquare(bombPosition.getX(),
                                           bombPosition.getY(),
                                           Constants.MAP_BOMB);

//                  this.game.notifyGameMapUpdateListeners();
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Give x coordinate for player. Search respawn point
     * of this player on map and return x coordinate of this respawn.
     * @param id ID of player
     * @return x coordinate of player
     */
    private int xCoordOf(int playerID) {
        int[][] mapArray = this.gameMap.getField();

        for (int i = 0; i < mapArray.length; i++) {
            for (int j = 0; j < mapArray.length; j++) {
                if (mapArray[i][j] == playerID) {
                    return i;
                }
            }
        }

        return 0;
    }

    /**
     * See xCoordOf
     * @param id
     * @return
     */
    private int yCoordOf(int playerID) {
        int[][] mapArray = this.gameMap.getField();

        for (int i = 0; i < mapArray.length; i++) {
            for (int j = 0; j < mapArray.length; j++) {
                if (mapArray[i][j] == playerID) {
                    return j;
                }
            }
        }

        return 0;
    }
}
