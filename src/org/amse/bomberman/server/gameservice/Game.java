package org.amse.bomberman.server.gameservice;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameservice.models.Model;
import org.amse.bomberman.server.gameservice.models.impl.ModelPlayer;
import org.amse.bomberman.util.Direction;
import org.amse.bomberman.util.Pair;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.amse.bomberman.server.gameservice.listeners.GameChangeListener;
import org.amse.bomberman.server.gameservice.models.ModelListener;
import org.amse.bomberman.server.gameservice.models.ModelFactory;
import org.amse.bomberman.server.gameservice.models.impl.StatsTable;

/**
 * Class that represents Game.
 * It correspond to:
 * <p>
 * 1) Before game starts players try join to game. Owner of game can add bots.
 * Players can chatting and so on.
 * <p>
 * 2) After start, game became something like a dealer between
 * players controllers and model.
 * 
 * @author Kirilchuk V.E.
 */
public class Game implements ModelListener {
    private final String                  gameName;
    
    private final Set<GameChangeListener> gameChangeListeners
            = new CopyOnWriteArraySet<GameChangeListener>();

    private final Set<GamePlayer>         gamePlayers
            = new CopyOnWriteArraySet<GamePlayer>();

    private final int   maxPlayers;
    private final Model model;
    final AsynchroChat  chat;
    
    GamePlayer          owner; // perhaps owner can change during game
    volatile boolean    started;

    /**
     * Constructor of Game.
     * Create Game object and add Game in server games list.
     * Autojoin creator in game as owner.
     * <p> if maxPlayers argument would be less than zero then
     * gameMap maxPlayers must be setted as maxPLayers of this game.
     * <p>
     * And if maxPlayers would be greater than supported by gameMap
     * then gameMap maxPlayers must be setted as maxPLayers of this game.
     * @param server server on which game was created.
     * @param gameMap gameMap of this game.
     * @param gameName name of this game.
     * @param maxPlayers maxPLayers of thisGame.
     * @param owner controller that created game. If he leaves game terminates
     * except situations when owner was changed during game.
     * Owner autojoins into game.
     */
    public Game(GamePlayer owner, GameMap gameMap, String gameName,
                int maxPlayers) {

        // initialization
        this.owner = owner;
        this.gameName = gameName;

        int gameMapMaxPlayers = gameMap.getMaxPlayers();

        /* If maxPlayers param was incorrect then gameMap maxPlayers will be used */
        if ((maxPlayers > 0) && (maxPlayers <= gameMapMaxPlayers)) {
            this.maxPlayers = maxPlayers;
        } else {
            this.maxPlayers = gameMapMaxPlayers;
        }

        this.chat = new AsynchroChat();
        this.model = ModelFactory.createModel(this, gameMap);

        //
        int playerID = this.tryJoin(owner);         
        owner.setPlayerId(playerID);

        this.started = false;
    }

    public void addMessageToChat(String message) {
        this.chat.addMessage(message, gameChangeListeners);
    }

    /**
     * This method tryes to move player with specified ID with defined direction.
     * <p>Note that if game is not started method will return false.
     * @param playerId ID of player to move.
     * @param direction moving direction.
     * @return true if move was done. false if game is not started
     * or move was not done.
     */
    public boolean tryDoMove(int playerId, Direction direction) {
        //all synchronization of such actions as Move, PlaceBomb etc must be provided by Model
        if(!this.started){
            return false;
        }

        boolean moved = false;
        
        ModelPlayer player = this.model.getPlayer(playerId);
        moved = model.tryDoMove(player, direction);

        if (moved) {
            fireFieldChanged();
        }

        return moved;
    }

    /**
     * Joins client(controller) into game if it is possible and
     * returns ingame ID.
     * @param name nickName of client.
     * @param player controller of player that tryes to join.
     * @return ingame ID for this controller or -1 if client can not be joined.
     */
    public int tryJoin(GamePlayer player) {
        if(this.started){
            return -1;
        }

        int playerID = -1;

        synchronized (this) {
            if (this.model.getCurrentPlayersNum() < this.maxPlayers) {
                String name = player.getNickName();
                playerID = this.model.addPlayer(name);
                player.setPlayerId(playerID);
                this.gamePlayers.add(player);
                fireParametersChanged();
            }
        }

        return playerID;
    }

    public boolean tryKickPlayer(GamePlayer caller, int playerToKickId) {
        if (caller != this.owner) {
            return false;
        }

        synchronized (this) {
            boolean result = this.model.removePlayer(playerToKickId);
            boolean result2 = false;
            for (GamePlayer gamePlayer : gamePlayers) {
                if(gamePlayer.getPlayerId() == playerToKickId) {
                    result2 = this.gamePlayers.remove(gamePlayer);
                    break;
                }
            }
            
            if(result && result2) {
                fireParametersChanged();
            }
            return result && result2;
        }
    }

    /**
     * Remove client(controller) from game.
     * If game was started then removes player of this client from game field.
     * <p> If leaving client was the owner of the game then game ends.
     * And all clients joined to this game automatically leaves game.
     * <p> Terminated game must remove from server.
     * @param player client that is leaving game.
     */
    public void leaveFromGame(GamePlayer player) {
        this.gamePlayers.remove(player);
        this.model.removePlayer(player.getPlayerId()); // not notifying

        if (!this.started) {
            for (GameChangeListener gameChangeListener : gameChangeListeners) {
                gameChangeListener.parametersChanged(this);
            }
        } else {
            fireFieldChanged();
        }

        if (player == owner) {
            terminateGame();    // will affect listeners
        }
    }

    /**
     * Tryes to place bomb. If game is not started returns false.
     * @param playerId ID of player that is trying to place bomb.
     * @return true if bomb was placed, false otherwise.
     */
    public boolean tryPlaceBomb(int playerID) {
        //all synchronization of such actions as Move, PlaceBomb etc must be provided by Model
        if (!this.started) {
            return false;
        }

        boolean placed = false;
        ModelPlayer player = this.model.getPlayer(playerID);
        placed = this.model.tryPlaceBomb(player);

        if (placed) {
            fireFieldChanged();
        }

        return placed;
    }

    /**
     * Tryes to start game. Note that only owner of the game can start it.
     * @param player client(controller) that is trying to start game.
     * @return true if game was started, false otherwise.
     */
    public synchronized boolean tryStartGame(GamePlayer player) {
        if (player != this.owner) {
            return false;
        }

        // Here model must change gameMap to support current num of players
        // and then give coordinates to Players.
        this.model.startup();
        this.started = true;

        for (GameChangeListener listener : gameChangeListeners) {
            listener.gameStarted(this);
        }

        return true;
    }

    /**
     * Adding listener that listens for game ended event.
     * @param listener listener.
     */
    public void addGameChangeListener(GameChangeListener listener) {
        this.gameChangeListeners.add(listener);
    }

    /**
     * Removing game end listener from game end listeners list.
     * @param listener listener to remove.
     */
    public void removeGameChangeListener(GameChangeListener listener) {
        this.gameChangeListeners.remove(listener);
    }

    /**
     * Setting the owner of game.
     * @param owner controller that will be setted as owner of the game.
     */
    public void setOwner(GamePlayer player) {
        this.owner = player;
    }

    /**
     * Return unmodifiableList of all Players that are playing in game including
     * bots.
     * @return unmodifiableList of all Players that are playing in game.
     */
    public List<ModelPlayer> getCurrentPlayers() {
        //model must return unmodifiable list by Model contract.
        return this.model.getPlayersList();
    }

    /**
     * Return number of all players in game including bots.
     * @return number of all players in game including bots.
     */
    public int getCurrentPlayersNum() {
        return this.model.getCurrentPlayersNum();
    }

    /**
     * This method is needed, cause model is hidden by private modificator.
     * But explosions are needed to send them to client, so this method is
     * just delegation to model of this game.
     * @return list of explosions.
     */
    public List<Pair> getExplosions() {
        return this.model.getExplosionSquares();
    }

    /**
     * This method is needed, cause model is hidden by private modificator.
     * But field is needed to send it to client, so this method is
     * just delegation.
     * @return matrix of game field.
     */
    public int[][] getGameField() {//TODO let`s return GameMap
        return this.model.getGameMap().getField();
    }

    /**
     * Return name of gameMap of this game.
     * @return name of gameMap of this game.
     */
    public String getGameMapName() {
        return this.model.getGameMap().getName();
    }

    /**
     * Returns this game maxPlayers.
     * @return this game maxPlayers.
     */
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public StatsTable getPlayersStats() {
        return this.model.getStatsTable();
    }

    /**
     * Return name of this game.
     * @return name of this game.
     */
    public String getGameName() {
        return this.gameName;
    }

    public GamePlayer getOwner() {
        return owner;
    }

    public boolean isGameOwner(GamePlayer player) {
        return (owner == player);
    }

    /**
     * Return the reference to Player that have defined ID.
     * @param playerId id of player.
     * @return the reference to Player that have defined ID.
     */
    public ModelPlayer getPlayer(int playerID) {
        return this.model.getPlayer(playerID);
    }

    /**
     * Checks if game is full or not.
     * @return true if game already have maxPlayers joined, false otherwise.
     */
    public boolean isFull() { //TODO whats about sync
        return (this.model.getCurrentPlayersNum() == this.maxPlayers);
    }

    /**
     * Checks if game is started.
     * @return true if game is started, false otherwise.
     */
    public boolean isStarted() {
        return this.started;
    }

    /**
     * Terminates game. Removing all clients from it. Then remove the game from
     * server.
     */
    private void terminateGame() {
        for (GameChangeListener listener : gameChangeListeners) {
            listener.gameTerminated(this);
        }
        gameChangeListeners.clear();//!?!?good or bad?
    }

    public void fireFieldChanged() {
        for (GameChangeListener gameChangeListener : gameChangeListeners) {
            gameChangeListener.fieldChanged();
        }
    }

    public void fireStatsChanged() {
        for (GameChangeListener listener : gameChangeListeners) {
            listener.statsChanged(this);
        }
    }

    private void fireParametersChanged() {
        // notifying clients
        for (GameChangeListener gameChangeListener : gameChangeListeners) {
            gameChangeListener.parametersChanged(this);
        }
    }

    public void end() {
        for (GameChangeListener listener : gameChangeListeners) {
            listener.gameEnded(this);
        }
    }
}
