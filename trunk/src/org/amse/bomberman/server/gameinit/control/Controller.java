
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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Controller implements GameEndedListener, GameStartedListener {
    private Game           game;
    private Player         player;
    private final ISession session; //do not delete need to notify session about updates
    private final IServer  sessionServer;

    public Controller(IServer sessionServer, ISession session) {
        this.sessionServer = sessionServer;
        this.session = session;
    }

    public void addMessageToChat(String toString) {
        this.game.addMessageToChat(player, toString);
    }

    public void gameEnded(Game game) {
        this.game.removeGameEndedListener(this);
        this.game.leaveFromGame(this);
        this.game = null;
        this.player = null;
    }

    public Game getMyGame() {
        return game;
    }

    public List<String> getNewMessagesFromChat() {
        return this.game.getNewMessagesFromChat(this.player);
    }

    public Player getPlayer() {
        return this.player;
    }

    public void leaveGame() {
        if (this.game != null) {
            this.game.removeGameEndedListener(this);
            this.game.leaveFromGame(this);
            this.game = null;
        }
    }

    public void started() {

        // must notify client that game started in asynchroRealization
        // and do nothing in synchro..
        ;    // synchro and asynchro collision
    }

    public int tryAddBotIntoMyGame(String botName) {
        int joinResult = -2;

        if (this.game != null) {
            joinResult = -1;

            if (!this.game.isStarted()) {
                Player bot = this.game.addBot(botName, this);

                if (bot == null) {
                    joinResult = 0;
                } else {
                    joinResult = 1;
                }
            }
        }

        return joinResult;
    }

    /**
     * Creates game. Add it to server. Setting session as owner of the game
     * and join owner into game.
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
    public void tryCreateGame(String mapName, String gameName, int maxPlayers)
                                    throws FileNotFoundException,
                                           IOException {
        if (this.game != null) {    // if not correct client can create multiple games

            // we just disconnect him from first game!
            game.leaveFromGame(this);
        }

        this.game = Creator.createGame(this.sessionServer, mapName, gameName,
                                       maxPlayers);    // TODO maybe createGame must return your Player?
        this.game.setOwner(this);
        this.player = this.game.join("playerName", this);
        this.game.addGameEndedListener(this);

        // TODO addGameStartedListener
        this.sessionServer.addGame(this.game);
    }

    public boolean tryDoMove(Direction dir) {
        return this.game.doMove(this, dir);
    }

    public int tryJoinGame(int gameID, String playerName) {
        if (this.game != null) {    // if not correct client can create multiple games

            // we just disconnect him from first game!
            game.leaveFromGame(this);
        }

        Game gameToJoin = this.sessionServer.getGame(gameID);
        int  joinResult = -2;

        if (gameToJoin != null) {
            joinResult = -1;

            if (!gameToJoin.isStarted()) {                
                this.player = gameToJoin.join(playerName, this);
                this.game = gameToJoin;

                if (this.player == null) {
                    joinResult = 0;
                } else {
                    joinResult = 1;
                    this.game.addGameEndedListener(this);
                }
            }
        }

        return joinResult;
    }

    public void tryPlaceBomb() {
        this.game.tryPlaceBomb(this);
    }

    public boolean tryStartGame() {    // ONLY HOST(CREATER) CAN START GAME!!!
        return this.game.tryStartGame(this);
    }
}
