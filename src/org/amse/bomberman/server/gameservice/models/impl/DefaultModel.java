
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameservice.models.impl;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameservice.models.DieListener;
import org.amse.bomberman.server.gameservice.Game;
import org.amse.bomberman.server.gameservice.GameMap;
import org.amse.bomberman.server.gameservice.models.MoveableObject;
import org.amse.bomberman.util.Pair;
import org.amse.bomberman.server.gameservice.models.Model;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Constants.Direction;

//~--- JDK imports ------------------------------------------------------------

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.amse.bomberman.server.gameservice.models.ModelListener;
import org.amse.bomberman.server.gameservice.models.impl.StatsTable.Stat;

/**
 * Model that is responsable for game rules and responsable for connection
 * between GameMap and Game.
 * @author Kirilchuk V.E.
 */
public class DefaultModel implements Model, DieListener {
    private static final ScheduledExecutorService timer =
                                   Executors.newSingleThreadScheduledExecutor();

    private final List<Bomb>                  bombs;
    private final List<Pair>                  explosionSquares;
    private final List<Integer>               freeIDs;
    private final List<ModelListener>         listeners;
    private final GameMap                     gameMap;
    private final List<ModelPlayer>           players;
    private StatsTable                        stats;
    private volatile boolean                  started;
    private volatile boolean                  ended;

    private int maxPlayersToEnd;

    /**
     * Constructor of Model.
     * @param map GameMap that correspond to game
     * @param game Game for which model was created
     */
    public DefaultModel(GameMap gameMap, Game game) {
        this.gameMap = gameMap;

        this.listeners = new CopyOnWriteArrayList<ModelListener>();
        this.bombs = new CopyOnWriteArrayList<Bomb>();
        this.players = new CopyOnWriteArrayList<ModelPlayer>();

        Integer[] freeIDArray = new Integer[game.getMaxPlayers()];

        for (int i = 0; i < freeIDArray.length; ++i) {
            freeIDArray[i] = i + 1;//cause players id`s are from 1 to ..
        }

        this.freeIDs = new CopyOnWriteArrayList<Integer>(freeIDArray);
        this.explosionSquares = new CopyOnWriteArrayList<Pair>();

        this.started = false;
        this.ended = false;
        this.listeners.add(game);
    }

    @Override
    public void addExplosions(List<Pair> explosions) {
        if(this.ended){
            return;
        }
        this.explosionSquares.addAll(explosions);
        notifyListenersFieldChange();
    }

    @Override
    public int addPlayer(String name) {
        ModelPlayer playerToAdd = new ModelPlayer(name, DefaultModel.timer);

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
        
        ModelPlayer owner = bomb.getOwner();

        if (owner.getPosition().equals(bomb.getPosition())) {
            this.gameMap.setSquare(owner.getPosition(), owner.getID());
        } else {
            this.gameMap.setSquare(bomb.getPosition(), Constants.MAP_EMPTY);
        }

        notifyListenersFieldChange();
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
        for (ModelPlayer pl : players) {
            if (pl.isAlive()) {
                aliveCount++;
            }
        }
        if (aliveCount <= maxPlayersToEnd && !this.ended) {
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
    public ModelPlayer getPlayer(int playerID) {
        for (ModelPlayer player : players) {
            if (player.getID() == playerID) {
                return player;
            }
        }

        return null;
    }

    @Override
    public List<ModelPlayer> getPlayersList() {
        return Collections.unmodifiableList(players);
    }

    @Override
    public boolean isExplosion(Pair coords) {
        return this.explosionSquares.contains(coords);
    }
    
    private boolean isMoveToReserved(Pair pair) {    // note that on explosions isEmpty = true!!!
        int x = pair.getX();
        int y = pair.getY();

        boolean reserved = !(this.gameMap.isEmpty(x, y)
                          || this.gameMap.isBonus(x, y));

        return reserved;
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

        if (objectToMove instanceof ModelPlayer) {
            if(!((ModelPlayer) objectToMove).isAlive()) {
                return;
            }
            if (this.gameMap.isBomb(x, y)) {    // if player setted mine but still in same square
                this.gameMap.setSquare(x, y, Constants.MAP_BOMB);
            } else {
                this.gameMap.setSquare(x, y, Constants.MAP_EMPTY);
            }

            if (this.gameMap.isBonus(newX, newY)) {
                int bonus = this.gameMap.getSquare(newX, newY);

                ((ModelPlayer) objectToMove).takeBonus(bonus);
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
            objectToMove.bombed();
        }
    }

    private Pair newPosition(Pair curPos, Direction direction) {

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
                throw new AssertionError("Unsupported direction.");
            }
        }
    }

    @Override
    public void playerBombed(ModelPlayer atacker, int victimID) {
        if(this.ended){
            return;
        }
        ModelPlayer victim = this.getPlayer(victimID);
        this.playerBombed(atacker, victim);
    }

    @Override
    public void playerBombed(ModelPlayer atacker, ModelPlayer victim) {
        Stat atackerStat = stats.getStats().get(atacker);
        Stat victimStat = stats.getStats().get(victim);

        if (atacker != victim) {
            atackerStat.increaseKills();
            victimStat.increaseDeaths();
        } else {//suicide
            atackerStat.increaseSuicides();
        }

        victim.bombed();
        for(ModelListener listener : listeners) {
            listener.fireStatsChanged();
        }
    }

    @Override
    public void playerDied(ModelPlayer player) {
        this.gameMap.removePlayer(player.getID());

        notifyListenersFieldChange();
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

        notifyListenersFieldChange();
    }

    @Override
    public boolean removePlayer(int playerID) {
        for (ModelPlayer player : players) {
            if (player.getID() == playerID) {
                this.players.remove(player);
                this.freeIDs.add(playerID); 
                if(started) {
                    this.gameMap.removePlayer(playerID);
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public void startup() {
        int playersCount = this.players.size();

        this.gameMap.changeMapForCurMaxPlayers(playersCount);
        this.maxPlayersToEnd = (playersCount > 1 ? 1 : 0);
        this.stats = new StatsTable(this.players);

        for (ModelPlayer player : players) {
            Pair playerCoords = this.gameMap.getPlayerPosition(player.getID());
            player.setPosition(playerCoords);
        }
        this.started = true;
    }

    public void end() {
        this.ended = true;
        for (ModelListener listener : listeners) {
            listener.end();
        }
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
                            && (objToMove instanceof ModelPlayer)) {
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
    public boolean tryPlaceBomb(ModelPlayer player) {    // whats about synchronization??
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

                    Bomb bomb = new Bomb(this, player, new Pair(x, y), DefaultModel.timer);

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

    public StatsTable getStatsTable() {
        return this.stats;
    }

    private void notifyListenersFieldChange() {
        for (ModelListener listener : listeners) {
            listener.fireFieldChanged();
        }
    }
}
