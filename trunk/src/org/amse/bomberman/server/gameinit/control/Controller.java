
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit.control;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.GameMap;
import org.amse.bomberman.server.gameinit.imodel.Player;
import org.amse.bomberman.server.net.IServer;
import org.amse.bomberman.server.net.ISession;
import org.amse.bomberman.util.Constants.Direction;
import org.amse.bomberman.util.Creator;

//~--- JDK imports ------------------------------------------------------------

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.List;

/**
 * Class that represents net controller of ingame player.
 * @author Kirilchuk V.E.
 */
public class Controller implements GameEndedListener {

    /**
     * Possible return value of next methods:
     * <p> 1)tryJoinGame
     * <p> 2)tryAddBotIntoMyGame
     * <p> 3)tryRemoveBot
     * <p> Tells that game was already started
     * and your operation is not success.
     */
    public static final int GAME_IS_ALREADY_STARTED = -1;

    /**
     * Possible return value of next methods:
     * <p> 1)tryJoinGame
     * <p> 2)tryAddBotIntoMyGame
     * <p> Tells that game is full
     * and your operation is not success.
     */
    public static final int GAME_IS_FULL = -3;

    /**
     * Possible return value of next methods:
     * <p> 1)tryAddBotIntoMyGame
     * <p> 2)tryRemoveBot
     * <p> Tells that you are not joined to any game so
     * you can not do this operation.
     */
    public static final int NOT_JOINED = -2;    // TODO there must be more methods that return this.

    /**
     * Possible return value of next methods:
     * <p> 1)tryAddBotIntoMyGame
     * <p> 2)tryRemoveBot
     * <p> Tells that you are not owner of game so
     * you can not do this operation.
     */
    public static final int NOT_OWNER_OF_GAME = 0;    // TODO there must be more methods that return this.

    /**
     * Possible return value of next methods:
     * <p> 1)tryJoinGame
     * <p> Tells that you operation failed cause no such unstarted game.
     */
    public static final int NO_SUCH_UNSTARTED_GAME = -10;

    /**
     * Possible return value of next methods:
     * <p> 1)tryJoinGame
     * <p> 2)tryAddBotIntoMyGame
     * <p> 3)tryRemoveBot
     * <p> Tells that you operation was sucessful.
     */
    public static final int RESULT_SUCCESS = 1;

    /**
     * Possible return value of next methods:
     * <p> tryRemoveBot
     * <p> Tells that there wasn`t bot to remove from game.
     */
    public static final int NO_SUCH_BOT = -5;
    private Game            game;
    private int             playerID;
    private final ISession  session;
    private final IServer   sessionServer;

    /**
     * Constructor of controller.
     * @param sessionServer server of session that owns this controller.
     * @param session owner of this controller.
     */
    public Controller(IServer sessionServer, ISession session) {
        this.sessionServer = sessionServer;
        this.session = session;
    }

    /**
     * Method to add message to game chat.
     * @param toString
     */
    public void addMessageToChat(String toString) {    // TODO must check if no game.
        this.game.addMessageToChat(this.game.getPlayer(playerID), toString);
    }

    /**
     * Method from GameEndedListener interface. In current realization
     * just setting game of controller to null and ID of player to -1.
     * @see GameEndedListener
     */
    public void gameEnded() {
        this.game.removeGameEndedListener(this);

//      this.game.leaveFromGame(this); //TODO is it really need?
        this.game = null;
        this.playerID = -1;
    }

    /**
     * Returns ingame ID of controller`s player.
     * @return ingame ID of controller`s player.
     */
    public int getID() {
        return this.playerID;
    }

    /**
     * Returns reference to your game or null if you are not joined
     * to any game.
     * @return reference to your game or null if you are not joined
     * to any game.
     */
    public Game getMyGame() {
        return game;
    }

    /**
     * Returns List of Strings - new messages from chat or List of only
     * one String - "No new messages." if there is no new messages available.
     * @return List of Strings - new messages, or
     * List of only String - "No new messages." if there is no new messages available.
     */
    public List<String> getNewMessagesFromChat() {
        return this.game.getNewMessagesFromChat(this.playerID);
    }

    /**
     * Returns reference to your Player
     * or null if you are not joined to any game.
     * @return reference to your Player
     * or null if you are not joined to any game.
     */
    public Player getPlayer() {
        return this.game.getPlayer(this.playerID);
    }

    /**
     * Returns reference to ISession that created this Controller.
     * @return reference to ISession that created this Controller.
     */
    public ISession getSession() {
        return session;
    }

    /**
     * Removes all listeners correspond to this Controller.
     * Then removes client from game.
     * <p>
     * If client was not joined to any game, this method do nothing.
     */
    public void tryLeaveGame() {
        if (this.game != null) {
            this.game.removeGameEndedListener(this);
            this.game.leaveFromGame(this);
            this.game = null;
        }
    }

    /**
     * Tryes to tryJoin bot into your game with specified nick name.
     * @param botName nick name of bot.
     * @return integer value that have next meanings
     * <p>
     * Controller.NOT_JOINED - if you are not joined to any game
     * and trying to add bot.
     * <p>
     * Controller.GAME_IS_ALREADY_STARTED - if game was already started
     * and you can not tryJoin bot.
     * <p>
     * Controller.NOT_OWNER_OF_GAME - if you are not owner of the game
     * <p>
     * Controller.GAME_IS_FULL - if game is full
     * and you can not join bot.
     * <p>
     * Controller.RESULT_SUCCESS - if bot was joined.
     */
    public int tryAddBotIntoMyGame(String botName) {
        int joinResult = Controller.NOT_JOINED;

        if (this.game != null) {
            joinResult = Controller.GAME_IS_FULL;

            if (!this.game.isFull()) {
                joinResult = Controller.GAME_IS_ALREADY_STARTED;

                if (!this.game.isStarted()) {
                    Player bot = this.game.tryAddBot(botName, this);

                    if (bot == null) {
                        joinResult = Controller.NOT_OWNER_OF_GAME;
                    } else {
                        joinResult = Controller.RESULT_SUCCESS;
                    }
                }
            }
        }

        return joinResult;
    }

    /**
     *
     * @return integer value that have next meanings
     * <p>
     * Controller.NOT_JOINED - if you are not joined to any game
     * and trying to remove bot.
     * <p>
     * Controller.GAME_IS_ALREADY_STARTED - if game was already started
     * and you can not remove bot.
     * <p>
     * Controller.NOT_OWNER_OF_GAME - if you are not owner of the game
     * and trying to remove bot.
     * <p>
     * Controller.NO_SUCH_BOT - if there is no bot to remove.
     * <p>
     * Controller.RESULT_SUCCESS - if bot was removed.
     */
    public int tryRemoveBot() {
        int removeResult = Controller.NOT_JOINED;

        if (this.game != null) {
            removeResult = Controller.GAME_IS_ALREADY_STARTED;

            if (!this.game.isStarted()) {
                removeResult = Controller.NOT_OWNER_OF_GAME;

                if (this.game.getOwner() == this) {
                    boolean removed = this.game.tryRemoveLastBot();
                    if(removed){
                        removeResult = Controller.RESULT_SUCCESS;
                    }else{
                        removeResult = Controller.NO_SUCH_BOT;
                    }
                }
            }
        }

        return removeResult;
    }

    /**
     * Creates game. Add it to server. Setting session as owner of the game
     * and tryJoin owner into game.
     * <p>
     * If owner was in other game and tryes to create game
     * then this method will disconnect him from previous game, cause only one
     * game for client is supported.
     * <p>
     * Additionally setting this controller as GameEndedListener.
     * @see GameEndedListener
     * @see Game
     * @see GameMap
     * @param gameMapName name of gameMap to create.
     * @param gameName name of game to create.
     * @param maxPlayers maxPlayers parameter of game.
     * @throws FileNotFoundException if no gameMap with such name was finded.
     * @throws IOException if IO errors occurs while creating gameMap.
     */
    public void tryCreateGame(String gameMapName, String gameName,
                              int maxPlayers, String playerName)
                                    throws FileNotFoundException,
                                           IOException {
        if (this.game != null) {    // if not correct client can create multiple games

            // we just disconnect him from first game!
            game.leaveFromGame(this);
        }

        this.game = Creator.createGame(this.sessionServer, gameMapName,
                                       gameName, maxPlayers);
        this.game.setOwner(this);    // TODO Game constructor must have owner argument!!!
        this.playerID = this.game.tryJoin(playerName, this);

        if (this.playerID == -1) {
            throw new NullPointerException("Error while creating game. " +
                    "Owner tryed to create and join but join returned null.");
        }

        this.game.addGameEndedListener(this);
    }

    /**
     * Tryes to move controller`s player in defined direction
     * in controller`s game.
     * @param dir move direction
     * @return true if player was moved, false otherwise.
     */
    public boolean tryDoMove(Direction dir) {    // TODO what if game==null
        return this.game.tryDoMove(this.playerID, dir);
    }

    /**
     * Tryes to join controller into game with specified nick name.
     * @param gameID ID of game to join in.
     * @param playerName nick name of player.
     * @return integer value that have next meanings
     * <p>
     * Controller.NO_SUCH_UNSTARTED_GAME - if there is no unstarted game
     * with such ID.
     * <p>
     * Controller.GAME_IS_ALREADY_STARTED - if game was already started
     * and you can not join.
     * <p>
     * Controller.GAME_IS_FULL - if game is full
     * and you can not join.
     * <p>
     * Controller.RESULT_SUCCESS - if you was joined.
     */
    public int tryJoinGame(int gameID, String playerName) {
        if (this.game != null) {    // if not correct client can create multiple games

            // we just disconnect him from first game!
            game.leaveFromGame(this);
        }

        Game gameToJoin = this.sessionServer.getGame(gameID);
        int  joinResult = Controller.NO_SUCH_UNSTARTED_GAME;

        if (gameToJoin != null) {
            joinResult = Controller.GAME_IS_FULL;

            if (!gameToJoin.isFull()) {
                joinResult = Controller.GAME_IS_ALREADY_STARTED;

                if (!gameToJoin.isStarted()) {
                    this.playerID = gameToJoin.tryJoin(playerName, this);
                    this.game = gameToJoin;

                    if (this.playerID == -1) {
                        joinResult = Controller.GAME_IS_FULL;    // TODO must never happen if synchronization is ok.
                    } else {
                        joinResult = Controller.RESULT_SUCCESS;
                        this.game.addGameEndedListener(this);
                    }
                }
            }
        }

        return joinResult;
    }

    /**
     * Tryes to place bomb by controller`s player in controller`s game.
     */
    public void tryPlaceBomb() {    // TODO what if game == null
        this.game.tryPlaceBomb(this.playerID);
    }

    /**
     * Tryes to start game by this controller. Start of game possible
     * only if this controller is the owner of game.
     * @return true if game was started, false otherwise.
     */
    public boolean tryStartGame() {    // TODO what if game ==null // ONLY HOST(CREATER) CAN START GAME!!!
        return this.game.tryStartGame(this);
    }
}