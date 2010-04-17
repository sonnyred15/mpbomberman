
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit.control;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.Player;
import org.amse.bomberman.server.net.IServer;
import org.amse.bomberman.server.net.ISession;
import org.amse.bomberman.util.Constants.Direction;
import org.amse.bomberman.util.Creator;

//~--- JDK imports ------------------------------------------------------------

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.List;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Controller implements GameEndedListener{
    public static final int GAME_IS_ALREADY_STARTED = -1;
    public static final int GAME_IS_FULL = -3;
    public static final int NOT_JOINED = -2;
    public static final int NOT_OWNER_OF_GAME = 0;
    public static final int NO_SUCH_UNSTARTED_GAME = -10;
    public static final int RESULT_SUCCESS = 1;
    private Game            game;
    private int             playerID;
    private final ISession  session;
    private final IServer sessionServer;

    public Controller(IServer sessionServer, ISession session) {
        this.sessionServer = sessionServer;
        this.session = session;
    }

    public void addMessageToChat(String toString) {
        this.game.addMessageToChat(this.playerID, toString);
    }

    public void gameEnded() {
        this.game.removeGameEndedListener(this);
//        this.game.leaveFromGame(this); //TODO is it really need?
        this.game = null;
        this.playerID = -1;
    }

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
     * or game is full
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
     * Creates game. Add it to server. Setting session as owner of the game
     * and tryJoin owner into game.
     * <p>
     * If owner was in other game and tryes to create game
     * then this method will disconnect him from previous game, cause only one
     * game for client is supported.
     * <p>
     * Additionally setting this controller as GameEndedListener.
     * @see org.amse.bomberman.server.gameinit.control.GameEndedListener
     * <p>
     * @param mapName
     * @param gameName
     * @param maxPlayers
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void tryCreateGame(String mapName, String gameName, int maxPlayers,
                              String playerName)
                                    throws FileNotFoundException,
                                           IOException {
        if (this.game != null) {    // if not correct client can create multiple games

            // we just disconnect him from first game!
            game.leaveFromGame(this);
        }

        this.game = Creator.createGame(this.sessionServer, mapName, gameName,
                                       maxPlayers);
        this.game.setOwner(this);    // TODO Game constructor must have owner argument!!!
        this.playerID = this.game.tryJoin(playerName, this);

        if (this.playerID == -1) {
            throw new NullPointerException("Error while creating game. " +
                    "Owner tryed to create and join but join returned null.");
        }

        this.game.addGameEndedListener(this);
        
    }

    public boolean tryDoMove(Direction dir) {
        return this.game.tryDoMove(this.playerID, dir);
    }

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

    public void tryPlaceBomb() {
        this.game.tryPlaceBomb(this.playerID);
    }

    public boolean tryStartGame() {    // ONLY HOST(CREATER) CAN START GAME!!!
        return this.game.tryStartGame(this);
    }
}
