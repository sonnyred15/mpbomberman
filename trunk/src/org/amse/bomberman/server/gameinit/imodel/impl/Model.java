
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit.imodel.impl;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.imodel.Bomb;
import org.amse.bomberman.server.gameinit.imodel.DieListener;
import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.GameMap;
import org.amse.bomberman.server.gameinit.imodel.MoveableObject;
import org.amse.bomberman.util.Pair;
import org.amse.bomberman.server.gameinit.imodel.Player;
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
import org.amse.bomberman.util.ProtocolConstants;
import org.amse.bomberman.util.Stringalize;

/**
 * Model that is responsable for game rules and responsable for connection
 * between GameMap and Game.
 * @author Kirilchuk V.E.
 */
public class Model implements IModel, DieListener {
    private static final ScheduledExecutorService timer =
                                   Executors.newSingleThreadScheduledExecutor();

    private final List<Bomb>                  bombs;
    private final List<Pair>                  explosionSquares;
    private final List<Integer>               freeIDs;
    private final Game                        game;
    private final GameMap                     gameMap;
    private final List<Player>                players;
    private boolean                           ended;

    /**
     * Constructor of Model.
     * @param map GameMap that correspond to game
     * @param game Game for which model was created
     */
    public Model(GameMap gameMap, Game game) {
        this.gameMap = gameMap;
        this.game = game;
        this.bombs = new CopyOnWriteArrayList<Bomb>();
        this.players = new CopyOnWriteArrayList<Player>();
        this.ended = false;

        Integer[] freeIDArray = new Integer[this.game.getMaxPlayers()];

        for (int i = 0; i < freeIDArray.length; ++i) {
            freeIDArray[i] = i + 1;//cause players indexes are from 1 to ..
        }

        this.freeIDs = new CopyOnWriteArrayList<Integer>(freeIDArray);
        this.explosionSquares = new CopyOnWriteArrayList<Pair>();
    }
    
    @Override
    public Player addBot(String botName) {
        Bot bot = new Bot(botName, this.game, this,
                          new RandomFullBotStrategy(), Model.timer);

        bot.setID(getFreeID());
        bot.setDieListener(this);
        this.players.add(bot);

        return bot;
    }

    @Override
    public void addExplosions(List<Pair> explSq) {
        if(this.ended){
            return;
        }
        this.explosionSquares.addAll(explSq);
        this.game.notifyGameSessions(ProtocolConstants.UPDATE_GAME_MAP);
    }

    @Override
    public int addPlayer(String name) {
        Player playerToAdd = new Player(name, Model.timer);

        playerToAdd.setID(getFreeID());
        playerToAdd.setDieListener(this);
        this.players.add(playerToAdd);

        return playerToAdd.getID();
    }

    @Override
    public void bombDetonated(Bomb bomb) {
        this.bombs.remove(bomb);

        if(this.ended){
            return;
        }
        
        Player owner = bomb.getOwner();

        if (owner.getPosition().equals(bomb.getPosition())) {
            this.gameMap.setSquare(owner.getPosition(), owner.getID());
        } else {
            this.gameMap.setSquare(bomb.getPosition(), Constants.MAP_EMPTY);
        }

        this.game.notifyGameSessions(ProtocolConstants.UPDATE_GAME_MAP);
    }

    @Override
    public void detonateBombAt(Pair position) {
        if(this.ended){
            return;
        }
        Bomb bombToDetonate = null;

        for (Bomb bomb : bombs) {
            if (bomb.getPosition().equals(position)) {
                bombToDetonate = bomb;

                break;
            }
        }

        bombToDetonate.detonate(true);
    }

    public void tryEnd() {
        int aliveCount = 0;
        for (Player pl : players) {
            if (pl.isAlive()) {
                aliveCount++;
            }
        }
        if (aliveCount <= 1 && !this.ended) {
            this.end();
        }
    }

    @Override
    public int getCurrentPlayersNum() {
        return this.players.size();
    }

    /**
     * Return list of explosions.
     * @return List of explosions
     */
    @Override
    public List<Pair> getExplosionSquares() {
        return this.explosionSquares;
    }

    private int getFreeID() {
        return this.freeIDs.remove(0);
    }

    @Override
    public GameMap getGameMap() {
        return gameMap;
    }

    @Override
    public Player getPlayer(int playerID) {
        for (Player player : players) {
            if (player.getID() == playerID) {
                return player;
            }
        }

        return null;
    }

    @Override
    public List<Player> getPlayersList() {
        return Collections.unmodifiableList(players);
    }

    @Override
    public boolean isExplosion(Pair coords) {
        return this.explosionSquares.contains(coords);
    }

    //TODO rewrite this casuistic logic
    private boolean isMoveToReserved(Pair pair) {    // note that on explosions isEmpty = true!!!
        int x = pair.getX();
        int y = pair.getY();

        boolean isFree = this.gameMap.isEmpty(x, y)
                         || this.gameMap.isBonus(x, y);

        return !isFree;
    }

    private boolean isOutMove(Pair pair) {
        int x = pair.getX();
        int y = pair.getY();

        int dim = this.gameMap.getDimension();

        if ((x < 0) || (x > dim - 1)) {
            return true;
        }

        if ((y < 0) || (y > dim - 1)) {
            return true;
        }

        return false;
    }

    //TODO use polymorphism instead of instanceof construction
    private void makeMove(MoveableObject objectToMove, Pair destination) {
        int x = objectToMove.getPosition().getX();
        int y = objectToMove.getPosition().getY();
        int newX = destination.getX();
        int newY = destination.getY();

        if (objectToMove instanceof Player) {
            if (this.gameMap.isBomb(x, y)) {    // if player setted mine but still in same square
                this.gameMap.setSquare(x, y, Constants.MAP_BOMB);
            } else {
                this.gameMap.setSquare(x, y, Constants.MAP_EMPTY);
            }

            if (this.gameMap.isBonus(newX, newY)) {
                int bonus = this.gameMap.getSquare(newX, newY);

                ((Player) objectToMove).takeBonus(bonus);
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

        // if object is making move to explosion zone.
        if (isExplosion(newPosition)) {
            if(objectToMove instanceof Player) {
                String name = ((Player)objectToMove).getNickName();
                this.game.addMessageToChat("Ouch, " + name +
                                           " just rushed into the fire.");
            }
            objectToMove.bombed();
        }
    }

    private Pair newPosition(Pair curPos, Direction direction) {    // whats about catch illegalArgumentException???

        switch (direction) {
            case DOWN : {
                return new Pair(curPos.getX() + 1,curPos.getY());
            }

            case LEFT : {
                return new Pair(curPos.getX(),curPos.getY() - 1);
            }

            case UP : {
                return new Pair( curPos.getX() - 1,curPos.getY());
            }

            case RIGHT : {
                return new Pair( curPos.getX(),curPos.getY() + 1);
            }

            default : {
                throw new IllegalArgumentException("Default block " +
                        "in switch(ENUM). Error in code.");
            }
        }
    }

    @Override
    public void playerBombed(Player atacker, int victimID) {
        if(this.ended){
            return;
        }
        Player victim = this.getPlayer(victimID);
        this.playerBombed(atacker, victim);
    }

    @Override
    public void playerBombed(Player atacker, Player victim) {        
        this.game.addMessageToChat(/*"Bomb of " + */atacker.getNickName() +
                                   " damaged " + victim.getNickName());
        if (atacker != victim) {
            atacker.damagedSomeone();     //TODO clear such damn hardcoded code
            atacker.changePoints(+1);
        }
        victim.bombed();
        victim.changePoints(-1);
    }

    @Override
    public void playerDied(Player player) {
        this.gameMap.removePlayer(player.getID());
        this.game.notifyGameSessions(ProtocolConstants.UPDATE_GAME_MAP);
        this.game.addMessageToChat("Oh, no. " + player.getNickName() +
                                   " was cruelly killed.");
    }

    /**
     * Printing matrix of GameMap to console. Maybe would be deleted soon.
     */
    @Override
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

    @Override
    public void removeExplosions(List<Pair> explosions) {

        for (Pair pair : explosions) {
            this.explosionSquares.remove(pair);
        }

        this.game.notifyGameSessions(ProtocolConstants.UPDATE_GAME_MAP);
    }

    @Override
    public boolean removePlayer(int playerID) {
        for (Player player : players) {
            if (player.getID() == playerID) {
                this.players.remove(player);
                this.freeIDs.add(playerID);
                if(this.game.isStarted()){
                    this.gameMap.removePlayer(playerID);
                }
                return true;
            }
        }

        return false;
    }

    @Override
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

    public void end() {
        this.ended = true;
        for (Player player : players) {
            if(player.isAlive()){
                player.changePoints(+3);
            }
        }
        List<String> stats = Stringalize.playersStats(this.players);
        stats.add(0, ProtocolConstants.CAPTION_GAME_ENDED);
        this.game.notifyGameSessions(stats);
    }

    /**
     * Trying to move the player in defined direction.
     * @param player Player to move
     * @param direction Direction of move
     * @return true if player moved, false otherwise
     */
    @Override
    public boolean tryDoMove(MoveableObject objToMove, Direction direction) {    // TODO synchronization?
        if(this.ended){
            return false;
        }
        synchronized (gameMap) {
            synchronized (objToMove) {
                Pair destination = newPosition(objToMove.getPosition(), direction);

                if (!isOutMove(destination)) {
                    if (this.gameMap.isBomb(destination)
                            && (objToMove instanceof Player)) {
                        Bomb bombToMove = null;

                        for (Bomb bomb : bombs) {
                            if (bomb.getPosition().equals(destination)) {
                                bombToMove = bomb;
                                tryDoMove(bombToMove, direction);

                                break;
                            }
                        }
                    }

                    if (!isMoveToReserved(destination)) {
                        makeMove(objToMove, destination);

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
    @Override
    public boolean tryPlaceBomb(Player player) {    // whats about synchronization??
        if(this.ended){
            return false;
        }
        synchronized (gameMap) {
            synchronized (player) {    // whats about syncronize(map)???
                if (player.canPlaceBomb()) {    // player is alive and have bombs to set up
                    int x = player.getPosition().getX();
                    int y = player.getPosition().getY();

                    if (this.gameMap.isBomb(x, y)
                            || this.isExplosion(new Pair(x, y))) {
                        return false;    // if player staying under the bomb or explosion
                    }

                    Bomb bomb = new Bomb(this, player, new Pair(x, y), Model.timer);

                    this.bombs.add(bomb);

                    Pair bombPosition = bomb.getPosition();

                    this.gameMap.setSquare(bombPosition.getX(),
                                           bombPosition.getY(),
                                           Constants.MAP_BOMB);                    

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
