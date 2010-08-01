
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.bot.Bot;
import org.amse.bomberman.server.net.tcpimpl.Controller;
import org.amse.bomberman.server.gameinit.control.GameEndedListener;
import org.amse.bomberman.server.gameinit.control.GameStartedListener;
import org.amse.bomberman.server.gameinit.imodel.IModel;
import org.amse.bomberman.server.gameinit.imodel.Player;
import org.amse.bomberman.util.Constants.Direction;
import org.amse.bomberman.util.Pair;
import org.amse.bomberman.protocol.ProtocolConstants;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.amse.bomberman.server.gameinit.control.GameChangeListener;
import org.amse.bomberman.server.gameinit.imodel.ModelFactory;

/**
 * Class that represents Game.
 * It correspond to:
 * <p>
 * 1) Before game starts players try join to game. Owner of game can add bots.
 * Players can chatting and so on.
 * <p>
 * 2) After start, game became something like a dealer between
 * players controllers and model.
 * @author Kirilchuk V.E.
 */
public class Game {
    private final AsynchroChat              chat;
    private final Set<Controller>           controllers;
    private final Set<GameEndedListener>    gameEndedListeners;
    private final String                    gameName;
    private final Set<GameStartedListener>  gameStartedListeners;
    private final int                       maxPlayers;
    private final IModel                    model;
    private       Controller                owner; // perhaps owner can change during game
    private final GameChangeListener        gameChangeListener;
    private volatile boolean                started; //TODO use State pattern

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
    public Game(GameStorage gameStorage, Controller owner, GameMap gameMap, String gameName,
                int maxPlayers) {

        // initialization
        this.gameChangeListener = gameStorage;
        this.owner = owner;
        this.gameName = gameName;

        int gameMapMaxPlayers = gameMap.getMaxPlayers();

        /* If maxPlayers param was incorrect then gameMap maxPlayers will be used*/
        if ((maxPlayers > 0) && (maxPlayers <= gameMapMaxPlayers)) {
            this.maxPlayers = maxPlayers;
        } else {
            this.maxPlayers = gameMapMaxPlayers;
        }

        this.chat = new AsynchroChat(this);
        this.model = ModelFactory.createModel(this, gameMap);

        //
        this.gameEndedListeners = new CopyOnWriteArraySet<GameEndedListener>();
        this.controllers = new CopyOnWriteArraySet<Controller>();
        this.gameStartedListeners =
            new CopyOnWriteArraySet<GameStartedListener>();

        //
        int playerID = this.tryJoin(owner);
        assert (playerID > 0); //if owner can`t autojoin it is very strange problem...
        owner.setPlayerID(playerID);

        //
        this.started = false;
        gameStorage.addGame(this); //only after this others can see the game.
    }

    /**
     * Adding bot into game if it is possible.
     * <p> Note that only owner of game can add bots.
     * @param name nickName of bot.
     * @param controller controller of player that tryes to add bot.
     * @return Null if controller is not owner or maxpPlayers reached.
     * Otherwise returns Bot wrapped by Player.
     */
    public Player tryAddBot(String name, Controller controller) {
        if (controller != this.owner) {
            return null;
        }

        Player bot = null;

        synchronized (this) {
            if (this.model.getCurrentPlayersNum() < this.maxPlayers) {
                bot = this.model.addBot(name);
                this.gameStartedListeners.add((GameStartedListener) bot);
                this.gameEndedListeners.add((GameEndedListener) bot);
                this.notifyGameSessions(ProtocolConstants.UPDATE_GAME_INFO);
                this.gameChangeListener.parametersChanged(this);
            }
        }

        return bot;
    }

    /**
     * Adding listener that listens for game started event.
     * @param gameStartedListener listener;
     */
    public void addGameStartedListener(
            GameStartedListener gameStartedListener) {
        this.gameStartedListeners.add(gameStartedListener);
    }

    /**
     * Adding listener that listens for game ended event.
     * @param gameEndedListener listener.
     */
    public void addGameEndedListener(GameEndedListener gameEndedListener) {
        this.gameEndedListeners.add(gameEndedListener);
    }

    /**
     * Adding message to chat.
     * @param player player which is adding message.
     * @param message message to add in chat.
     */
    public void addMessageToChat(Player player, String message) {
        this.chat.addMessage(player.getNickName(), message);
        notifyGameSessions(ProtocolConstants.UPDATE_CHAT_MSGS);
    }

    public void addMessageToChat(String message) {
        this.chat.addMessage(message);
        notifyGameSessions(ProtocolConstants.UPDATE_CHAT_MSGS);
    }

    /**
     * This method tryes to move player with specified ID with defined direction.
     * <p>Note that if game is not started method will return false.
     * @param playerID ID of player to move.
     * @param direction moving direction.
     * @return true if move was done. false if game is not started
     * or move was not done.
     */
    public boolean tryDoMove(int playerID, Direction direction) {
        //all synchronization of such actions as Move, PlaceBomb etc must be provided by IModel
        if(!this.started){
            return false;
        }

        boolean moved = false;
        moved = model.tryDoMove(this.model.getPlayer(playerID), direction);

        if (moved) {
            notifyGameSessions(ProtocolConstants.UPDATE_GAME_MAP);
        }

        return moved;
    }

    /**
     * Return list of controllers that joined to this game.
     * @return list of controllers that joined to this game.
     */
    public Set<Controller> getControllers() {
        return controllers;
    }

    /**
     * Return unmodifiableList of all Players that are playing in game including
     * bots.
     * @return unmodifiableList of all Players that are playing in game.
     */
    public List<Player> getCurrentPlayers() {
        //model must return unmodifiable list by IModel contract.
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
    public List<Pair> getExplosionSquares() {
        return this.model.getExplosionSquares();
    }

    /**
     * This method is needed, cause model is hidden by private modificator.
     * But field is needed to send it to client, so this method is
     * just delegation.
     * @return matrix of game field.
     */
    public int[][] getGameField() {
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

    /**
     * Return name of this game.
     * @return name of this game.
     */
    public String getName() {
        return this.gameName;
    }

    /**
     * Return list of new messages from chat. See SynchroChat class to view
     * what will be sended if there is no new messages.
     * @see SynchroChat
     * @param playerID ID of player that requests his new messages.
     * @return list of new messages from chat.
     * @deprecated Dead code. In current asynchro realization always sends
     * "No new messages". Need only cause documented in protocol.
     */
    public List<String> getNewMessagesFromChat(int playerID) {
        return this.chat.getNewMessages(playerID);
    }

    /**
     * Returns the reference to Controller that is the owner of this game.
     * @return the reference to Controller that is the owner of this game.
     */
    public Controller getOwner() {
        return owner;
    }

    /**
     * Return the reference to Player that have defined ID.
     * @param playerID id of player.
     * @return the reference to Player that have defined ID.
     */
    public Player getPlayer(int playerID) {
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
    public boolean isStarted() {//TODO introduce Lobby class where game must start
        return this.started;
    }

    /**
     * Joins client(controller) into game if it is possible and
     * returns ingame ID.
     * @param name nickName of client.
     * @param controller controller of player that tryes to join.
     * @return ingame ID for this controller or -1 if client can not be joined.
     */
    public int tryJoin(Controller controller) {
        if(this.started){
            return -1;
        }

        int playerID = -1;

        synchronized (this) {
            if (this.model.getCurrentPlayersNum() < this.maxPlayers) {
                String name = controller.getName();
                playerID = this.model.addPlayer(name);
                this.controllers.add(controller);

                // notifying clients
                this.notifyGameSessions(ProtocolConstants.UPDATE_GAME_INFO);
                this.gameChangeListener.parametersChanged(this);
            }
        }

        return playerID;
    }

    /**
     * Remove client(controller) from game.
     * If game was started then removes player of this client from game field.
     * <p> If leaving client was the owner of the game then game ends.
     * And all clients joined to this game automatically leaves game.
     * <p> Terminated game must remove from server.
     * @param controller client that is leaving game.
     */
    public void leaveFromGame(Controller controller) {
        this.controllers.remove(controller);
        this.model.removePlayer(controller.getID());// not notifying

        if (!this.started) {
            this.gameChangeListener.parametersChanged(this);
            notifyGameSessions(ProtocolConstants.UPDATE_GAME_INFO);
        } else {
            notifyGameSessions(ProtocolConstants.UPDATE_GAME_MAP);
        }

        if (controller == this.owner) {
            this.terminateGame();    // will call notify if need
        }
    }

    /**
     * Removes bot from game. If game was started then removes bot
     * from gameField.
     * @param bot bot to remove from the game.
     */
    public boolean tryRemoveBotFromGame(Bot bot) {//TODO CHECK THIS
        boolean result = this.model.removePlayer(bot.getID());
        this.gameStartedListeners.remove(bot);
        this.gameEndedListeners.remove(bot);

        if (!this.started) {
            this.gameChangeListener.parametersChanged(this);
            notifyGameSessions(ProtocolConstants.UPDATE_GAME_INFO);
        } else {
            notifyGameSessions(ProtocolConstants.UPDATE_GAME_MAP);
        }

        return result;
    }

    public boolean tryRemoveLastBot() { // TODO CHECK THIS
        List<Player> players = this.model.getPlayersList();
        for(int i = players.size()-1; i>0; --i){
            Player pl = players.get(i);
            if(pl instanceof Bot){
                return this.tryRemoveBotFromGame((Bot)pl);
            }
        }

        return false;
    }

    /**
     * Removing game end listener from game end listeners list.
     * @param listener listener to remove.
     */
    public void removeGameEndedListener(GameEndedListener listener) {
        this.gameEndedListeners.remove(listener);
    }

    /**
     * Removing game start listener from game start listeners list.
     * @param listener listener to remove.
     */
    public void removeGameStartedListener(GameStartedListener listener) {
        this.gameStartedListeners.remove(listener);
    }

    /**
     * Setting the owner of game.
     * @param owner controller that will be setted as owner of the game.
     */
    public void setOwner(Controller owner) {
        this.owner = owner;
    }

    /**
     * Tryes to place bomb. If game is not started returns false.
     * @param playerID ID of player that is trying to place bomb.
     * @return true if bomb was placed, false otherwise.
     */
    public boolean tryPlaceBomb(int playerID) {
        //all synchronization of such actions as Move, PlaceBomb etc must be provided by IModel
        if (!this.started) {
            return false;
        }

        boolean placed = false;
        placed = this.model.tryPlaceBomb(this.model.getPlayer(playerID));

        if (placed) {
            notifyGameSessions(ProtocolConstants.UPDATE_GAME_MAP);//TODO model must notify about this
        }

        return placed;
    }

    /**
     * Tryes to start game. Note that only owner of the game can start it.
     * @param controller client(controller) that is trying to start game.
     * @return true if game was started, false otherwise.
     */
    public boolean tryStartGame(Controller controller) {
        if (this.owner != controller) {
            return false;
        }

        // Here model must change gameMap to support current num of players
        // and then give coordinates to Players.
        this.model.startup();
        this.started = true;
        this.gameChangeListener.started(this);

        notifyGameSessions(ProtocolConstants.MESSAGE_GAME_START);

        for (GameStartedListener gameStartedListener : gameStartedListeners) {
            gameStartedListener.started(this);
        }

        return true;
    }

    /**
     * Notifying  only clients that are joined to this game about something
     * by sending message to them.
     * @param message message to send to clients.
     */
    public void notifyGameSessions(String message) {
        for (Controller controller : controllers) {
            controller.getSession().sendAnswer(message);
        }
    }

    /**
     * Notifying  only clients that are joined to this game about something
     * by sending messages to them.
     * @param message message to send to clients.
     */
    public void notifyGameSessions(List<String> messages) {
        for (Controller controller : controllers) {
            controller.getSession().sendAnswer(messages);
        }
    }

    /**
     * Terminates game. Removing all clients from it. Then remove the game from
     * server.
     */
    private void terminateGame() {
        for (GameEndedListener gameEndedListener : gameEndedListeners) {
            gameEndedListener.gameEnded(this);
        }

        this.gameChangeListener.gameEnded(this);

        notifyGameSessions(ProtocolConstants.MESSAGE_GAME_KICK);
    }
}
