package org.amse.bomberman.server.gameservice.models.impl;

import org.amse.bomberman.server.gameservice.gamemap.objects.impl.Bomb;
import org.amse.bomberman.server.gameservice.models.DieListener;
import org.amse.bomberman.server.gameservice.impl.Game;
import org.amse.bomberman.server.gameservice.gamemap.impl.GameMap;
import org.amse.bomberman.server.gameservice.gamemap.impl.MoveableGameMapObject;
import org.amse.bomberman.util.Pair;
import org.amse.bomberman.server.gameservice.models.Model;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Direction;
import org.amse.bomberman.server.gameservice.models.ModelListener;
import org.amse.bomberman.server.gameservice.models.impl.StatsTable.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.amse.bomberman.server.gameservice.gamemap.objects.impl.Bonus;

/**
 * Model that is responsable for game rules and responsable for connection
 * between GameMap and Game.
 * @author Kirilchuk V.E.
 */
public class DefaultModel implements Model, DieListener {
    private final ScheduledExecutorService sharedTimer =
                                   Executors.newSingleThreadScheduledExecutor();
    
    private final List<Integer>               freeIDs;
    private final List<ModelListener>         listeners;    
    private final List<ModelPlayer>           players;
    private final GameMap                     gameMap;
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
        this.players = new CopyOnWriteArrayList<ModelPlayer>();

        Integer[] freeIDArray = new Integer[game.getMaxPlayers()];

        for (int i = 0; i < freeIDArray.length; ++i) {
            freeIDArray[i] = i + 1;//cause players id`s are from 1 to ..
        }

        this.freeIDs = new CopyOnWriteArrayList<Integer>(freeIDArray);//TODO maybe just simple Vector?        

        this.started = false;
        this.ended = false;
        this.listeners.add(game);
    }

    @Override
    public void addExplosions(List<Pair> explosions) {//TODO move to gameMap and redesign Bomb
        if(this.ended){
            return;
        }
        gameMap.getExplosions().addAll(explosions);
        fireGameMapChange();
    }

    @Override
    public int addPlayer(String name) {
        ModelPlayer playerToAdd = new ModelPlayer(name, sharedTimer);

        playerToAdd.setId(getFreeId());
        playerToAdd.setDieListener(this);
        players.add(playerToAdd);

        return playerToAdd.getId();
    }

    @Override
    public void bombDetonated(Bomb bomb) {
        if(ended){
            return;
        }

        gameMap.getBombs().remove(bomb);
        ModelPlayer owner = bomb.getOwner();

        if (owner.getPosition().equals(bomb.getPosition())) {
            gameMap.setValue(owner.getPosition(), owner.getId());
        } else {
            gameMap.setValue(bomb.getPosition(), Constants.MAP_EMPTY);
        }

        fireGameMapChange();
    }

    @Override
    public void detonateBombAt(Pair position) {
        if(this.ended){
            return;
        }
        Bomb bombToDetonate = null;

        for (Bomb bomb : gameMap.getBombs()) {
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
        return players.size();
    }

    @Override
    public GameMap getGameMap() {
        return gameMap;
    }

    @Override
    public ModelPlayer getPlayer(int playerId) {
        for (ModelPlayer player : players) {
            if (player.getId() == playerId) {
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
    public boolean isExplosion(Pair position) {
        return gameMap.isExplosion(position);
    }

    @Override
    public void playerBombed(ModelPlayer atacker, int victimID) {
        if(ended){
            return;
        }
        ModelPlayer victim = getPlayer(victimID);

        playerBombed(atacker, victim);
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
        
        fireStatsChanged();
    }

    @Override
    public void playerDied(ModelPlayer player) {
        this.gameMap.removePlayer(player.getId());

        fireGameMapChange();
    }

    @Override
    public void removeExplosions(List<Pair> explosions) {
        List<Pair> all = gameMap.getExplosions();
        for (Pair pair : explosions) {
            all.remove(pair);
        }

        fireGameMapChange();
    }

    @Override
    public boolean removePlayer(int playerId) {
        for (ModelPlayer player : players) {
            if (player.getId() == playerId) {
                players.remove(player);
                freeIDs.add(playerId);
                if(started) {
                    gameMap.removePlayer(playerId);
                    fireGameMapChange();
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public void startup() {
        int playersCount = players.size();

        gameMap.changeMapFor(playersCount);
        maxPlayersToEnd = (playersCount > 1 ? 1 : 0);
        stats = new StatsTable(players);

        for (ModelPlayer player : players) {
            Pair playerCoords = gameMap.getPlayerPosition(player.getId());
            player.setPosition(playerCoords);
        }
        started = true;

        fireStatsChanged();
    }

    public void end() {
        ended = true;
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
    public boolean tryDoMove(ModelPlayer player, Direction direction) {    // TODO synchronization?
        if(ended){
            return false;
        }
 
        Pair destination = newPosition(player.getPosition(), direction);

        if (!isOutMove(destination)) {
            if (!isMoveToReserved(destination)) {
                makeMove(player, destination);
                fireGameMapChange();
                return true;
            }
        }

        return false;
    }

    /**
     * Trying to place bomb of defined player.
     * @param player Player which trying to place bomb.
     */
    @Override
    public boolean tryPlaceBomb(ModelPlayer player) {    //TODO whats about synchronization??
        if(ended){
            return false;
        }
      
        if (player.canPlaceBomb()) {    // player is alive and have bombs to set up
            Pair position = player.getPosition();

            if (gameMap.isBomb(position) || isExplosion(position)) {
                return false;    // if player staying under the bomb or explosion
            }

            Bomb bomb = new Bomb(this, player, position, sharedTimer);
            gameMap.getBombs().add(bomb);
            gameMap.setValue(position, Constants.MAP_BOMB);
            fireGameMapChange();
            return true;

        }

        return false;
    }

    public StatsTable getStatsTable() {
        return stats;
    }


    private boolean isMoveToReserved(Pair position) {    // note that on explosions isEmpty = true!!!

        boolean reserved = !gameMap.isEmpty(position)
                        && !gameMap.isBonus(position);

        return reserved;
    }

    private boolean isOutMove(Pair pair) {
        int x = pair.getX();
        int y = pair.getY();

        int dim = gameMap.getDimension();

        if ((x < 0) || (x > dim - 1)) {
            return true;
        }

        if ((y < 0) || (y > dim - 1)) {
            return true;
        }

        return false;
    }

    private void makeMove(ModelPlayer player, Pair destination) {
        Pair position = player.getPosition();

        if (!player.isAlive()) { //TODO synchronization player can die after checkd
            return;
        }
        if (gameMap.isBomb(position)) {    // if player setted mine but still in same square
            gameMap.setValue(position, Constants.MAP_BOMB);
        } else {
            gameMap.setValue(position, Constants.MAP_EMPTY);
        }

        if (gameMap.isBonus(destination)) {
            int bonus = gameMap.getValue(destination);
            player.accept(Bonus.valueOf(bonus));
        }

        gameMap.setValue(destination, player.getId());
        player.setPosition(destination);

        // if object is making move to explosion zone.
        if (isExplosion(destination)) {
            player.bombed();
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

    private void fireGameMapChange() {//TODO move this to GameMap
        for (ModelListener listener : listeners) {
            listener.gameMapChanged();
        }
    }

    private void fireStatsChanged() {
        for (ModelListener listener : listeners) {
            listener.statsChanged();
        }
    }

    private int getFreeId() {
        return freeIDs.remove(0);
    }
}
