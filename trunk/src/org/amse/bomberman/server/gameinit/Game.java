
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.control.Controller;
import org.amse.bomberman.server.gameinit.control.GameEndedListener;
import org.amse.bomberman.server.gameinit.control.GameMapUpdateListener;
import org.amse.bomberman.server.gameinit.control.GameStartedListener;
import org.amse.bomberman.server.gameinit.imodel.IModel;
import org.amse.bomberman.server.gameinit.imodel.impl.Model;
import org.amse.bomberman.server.net.IServer;
import org.amse.bomberman.server.net.ISession;
import org.amse.bomberman.util.Constants.Direction;

//~--- JDK imports ------------------------------------------------------------

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Kirilchuk V.E
 */
public class Game {
    private Chat                            chat;
    private final List<GameEndedListener>   gameEndedListeners;
    private final String                    gameName;
    private final List<GameStartedListener> gameStartedListeners;
    private final int                       maxPlayers;
    private final IModel                    model;
    private Controller                      owner;
    private final IServer                   server;
    private final List<ISession>            sessions;
    private boolean                         started;

    public Game(IServer server, GameMap gameMap, String gameName,
                int maxPlayers) {
        this.server = server;
        this.gameName = gameName;

        int gameMapMaxPlayers = gameMap.getMaxPlayers();

        if ((maxPlayers > 0) && (maxPlayers <= gameMapMaxPlayers)) {
            this.maxPlayers = maxPlayers;
        } else {
            this.maxPlayers = gameMapMaxPlayers;
        }

        this.chat = new Chat(this.maxPlayers);
        this.model = new Model(gameMap, this);
        this.gameEndedListeners = new CopyOnWriteArrayList<GameEndedListener>();
        this.sessions = new CopyOnWriteArrayList<ISession>();
        this.gameStartedListeners =
            new CopyOnWriteArrayList<GameStartedListener>();
        this.started = false;
    }

    public synchronized Player addBot(String name, Controller controller) {
        if (controller != this.owner) {
            return null;
        }

        Player bot = null;

        if (this.model.getCurrentPlayersNum() < this.maxPlayers) {
            bot = this.model.addBot(name);
            this.gameStartedListeners.add((GameStartedListener) bot);
        }

        return bot;
    }

    public void addGameEndedListener(GameEndedListener gameEndedListener) {
        this.gameEndedListeners.add(gameEndedListener);
    }

    // method that delegates to model
    public void addGameMapUpdateListener(GameMapUpdateListener listener) {

        // TODO
    }

    public void addGameStartedListener(
            GameStartedListener gameStartedListener) {
        this.gameStartedListeners.add(gameStartedListener);
    }

    public void addMessageToChat(Player player, String message) {
        this.chat.addMessage(player.getID(), player.getNickName(), message);
    }

    public boolean doMove(Controller controller, Direction direction) {
        if (this.started) {
            return model.tryDoMove(controller.getPlayer(), direction);
        }

        return false;
    }

    private void endGame() {
        for (GameEndedListener gameEndedListener : gameEndedListeners) {
            gameEndedListener.gameEnded();
        }

        this.server.removeGame(this);
    }

    public List<Player> getCurrentPlayers() {
        return Collections.unmodifiableList(this.model.getPlayersList());
    }

    public int getCurrentPlayersNum() {
        return this.model.getCurrentPlayersNum();
    }

    public List<Pair> getExplosionSquares() {
        return this.model.getExplosionSquares();
    }

    public int[][] getGameMapArray() {
        return this.model.getGameMapArray();
    }

    public String getGameMapName() {
        return this.model.getGameMapName();
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public String getName() {
        return this.gameName;
    }

    public List<String> getNewMessagesFromChat(Player player) {
        return this.chat.getNewMessages(player.getID());
    }

    public Controller getOwner() {
        return owner;
    }

    public Player getPlayer(int playerID) {
        return this.model.getPlayer(playerID);
    }

    public List<ISession> getSessions() {
        return sessions;
    }

    public boolean isFull() {
        return ((this.model.getCurrentPlayersNum() == this.maxPlayers) ? true
                                                                       : false);
    }

    public boolean isStarted() {
        return this.started;
    }

    public synchronized Player join(String name, Controller controller) {
        Player player = null;

        if (this.model.getCurrentPlayersNum() < this.maxPlayers) {
            player = this.model.addPlayer(name);
            this.sessions.add(controller.getSession());
            this.chat.addPlayer(player.getID());
        }

        return player;
    }

    public void leaveFromGame(Controller controller) {
        this.sessions.remove(controller.getSession());
        this.model.removePlayer(controller.getPlayer().getID());
        this.chat.removePlayer(controller.getPlayer().getID());

        if (controller == this.owner) {
            this.endGame();
        }
    }

    public void removeBotFromGame(Player bot) {
        this.model.removePlayer(bot.getID());
        this.chat.removePlayer(bot.getID());
    }

    public void removeGameEndedListener(GameEndedListener listener) {
        this.gameEndedListeners.remove(listener);
    }

    // method that delegates to model
    public void removeGameMapUpdateListener(GameMapUpdateListener listener) {

        // TODO
    }

    public void setOwner(Controller owner) {
        this.owner = owner;
    }

    public boolean tryPlaceBomb(Controller controller) {
        if (this.started) {
            return this.model.tryPlaceBomb(controller.getPlayer());
        }

        return false;
    }

    public boolean tryStartGame(Controller controller) {
        if (this.owner == controller) {
            this.started = true;

            // Here model must change gameMap to support current num of players
            // and then give coordinates to Players.
            this.model.startup();
            //this.chat.clear();
            //this.chat = new Chat(this.model.getCurrentPlayersNum());

            // here we notifying all about start of game
            for (GameStartedListener gameStartedListener : gameStartedListeners) {
                gameStartedListener.started();
            }

            return true;
        }

        return false;
    }
}
