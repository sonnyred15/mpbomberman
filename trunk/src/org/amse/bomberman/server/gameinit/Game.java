
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.bot.Bot;
import org.amse.bomberman.server.gameinit.control.Controller;
import org.amse.bomberman.server.gameinit.control.GameEndedListener;
import org.amse.bomberman.server.gameinit.control.GameMapUpdateListener;
import org.amse.bomberman.server.gameinit.control.GameStartedListener;
import org.amse.bomberman.server.gameinit.imodel.IModel;
import org.amse.bomberman.server.gameinit.imodel.impl.Model;
import org.amse.bomberman.server.net.IServer;
import org.amse.bomberman.server.net.ISession;
import org.amse.bomberman.util.Constants.Direction;
import org.amse.bomberman.util.Pair;
import org.amse.bomberman.util.ProtocolConstants;

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
    private final List<Controller>          controllers;
    private final List<GameEndedListener>   gameEndedListeners;
    private final String                    gameName;
    private final List<GameStartedListener> gameStartedListeners;
    private final int                       maxPlayers;
    private final IModel                    model;
    private Controller                      owner;
    private final IServer                   server;
    private boolean                         started;

    public Game(IServer server, GameMap gameMap, String gameName,
                int maxPlayers) {

        // initialization
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
        this.controllers = new CopyOnWriteArrayList<Controller>();
        this.gameStartedListeners =
            new CopyOnWriteArrayList<GameStartedListener>();
        this.started = false;

        // additional stuff
        this.server.addGame(this);
        this.notifyAllSessions(ProtocolConstants.UPDATE_GAMES_LIST);
    }

    public synchronized Player addBot(String name, Controller controller) {
        if (controller != this.owner) {
            return null;
        }

        Player bot = null;

        if (this.model.getCurrentPlayersNum() < this.maxPlayers) {
            bot = this.model.addBot(name);
            this.gameStartedListeners.add((GameStartedListener) bot);
            this.gameEndedListeners.add((GameEndedListener) bot);
            this.notifyAllSessions(ProtocolConstants.UPDATE_GAMES_LIST);
            this.notifyGameSessions(ProtocolConstants.UPDATE_GAME_INFO);
        }

        return bot;
    }

    public void addGameEndedListener(GameEndedListener gameEndedListener) {
        this.gameEndedListeners.add(gameEndedListener);
    }

    public void addGameStartedListener(
            GameStartedListener gameStartedListener) {
        this.gameStartedListeners.add(gameStartedListener);
    }

    public void addMessageToChat(int playerID, String message) {
        this.chat.addMessage(playerID,
                             this.model.getPlayer(playerID).getNickName(),
                             message);
        notifyGameSessions(ProtocolConstants.UPDATE_CHAT_MSGS);
    }

    public boolean doMove(int playerID, Direction direction) {
        boolean moved = false;

        if (this.started) {
            moved = model.tryDoMove(this.model.getPlayer(playerID), direction);

            if (moved) {
                notifyGameSessions(ProtocolConstants.UPDATE_GAME_MAP);
            }
        }

        return moved;
    }

    public List<Controller> getControllers() {
        return controllers;
    }

    public List<Player> getCurrentPlayers() {
        return Collections.unmodifiableList(this.model.getPlayersList());
    }

    // maybe use instead of this getCurrentPlayers.size()?
    public int getCurrentPlayersNum() {
        return this.model.getCurrentPlayersNum();
    }

    public List<Pair> getExplosionSquares() {
        return this.model.getExplosionSquares();
    }

    public int[][] getGameMapArray() {
        return this.model.getGameMap().getField();
    }

    public String getGameMapName() {
        return this.model.getGameMap().getName();
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public String getName() {
        return this.gameName;
    }

    public List<String> getNewMessagesFromChat(int playerID) {
        return this.chat.getNewMessages(playerID);
    }

    public Controller getOwner() {
        return owner;
    }

    public Player getPlayer(int playerID) {
        return this.model.getPlayer(playerID);
    }

    public boolean isFull() {
        return ((this.model.getCurrentPlayersNum() == this.maxPlayers) ? true
                                                                       : false);
    }

    public boolean isStarted() {
        return this.started;
    }

    public synchronized int join(String name, Controller controller) {
        int playerID = -1;

        if (this.model.getCurrentPlayersNum() < this.maxPlayers) {
            playerID = this.model.addPlayer(name);
            this.controllers.add(controller);
            this.chat.addPlayer(playerID);

            // notifying clients
            this.notifyAllSessions(ProtocolConstants.UPDATE_GAMES_LIST);
            this.notifyGameSessions(ProtocolConstants.UPDATE_GAME_INFO);
        }

        return playerID;
    }

    public void leaveFromGame(Controller controller) {
        this.controllers.remove(controller);
        this.model.removePlayer(controller.getID());    // this will call notify about gameMap change
        this.chat.removePlayer(controller.getID());

        if (!this.started) {
            notifyGameSessions(ProtocolConstants.UPDATE_GAMES_LIST);
            notifyGameSessions(ProtocolConstants.UPDATE_GAME_INFO);
        } else {
            notifyGameSessions(ProtocolConstants.UPDATE_GAME_MAP);
        }

        if (controller == this.owner) {
            this.terminateGame();    // will call notify if need
        }
    }

    public void removeBotFromGame(Bot bot) {
        if (!this.started) {
            notifyGameSessions(ProtocolConstants.UPDATE_GAMES_LIST);
            notifyGameSessions(ProtocolConstants.UPDATE_GAME_INFO);
        } else {
            notifyGameSessions(ProtocolConstants.UPDATE_GAME_MAP);
        }

        this.model.removePlayer(bot.getID());
        this.chat.removePlayer(bot.getID());
    }

    public void removeGameEndedListener(GameEndedListener listener) {
        this.gameEndedListeners.remove(listener);
    }

//  public void removeGameMapUpdateListener(
//          GameMapUpdateListener gameMapUpdateListener) {
//      this.model.removeGameMapUpdateListener(gameMapUpdateListener);
//  }
    public void removeGameStartedListener(GameStartedListener listener) {
        this.gameStartedListeners.remove(listener);
    }

    public void setOwner(Controller owner) {
        this.owner = owner;
    }

    public boolean tryPlaceBomb(int playerID) {
        boolean placed = false;

        if (this.started) {
            placed = this.model.tryPlaceBomb(this.model.getPlayer(playerID));

            if (placed) {
                notifyGameSessions(ProtocolConstants.UPDATE_GAME_MAP);
            }
        }

        return placed;
    }

    public boolean tryStartGame(Controller controller) {
        if (this.owner == controller) {
            this.started = true;

            // Here model must change gameMap to support current num of players
            // and then give coordinates to Players.
            this.model.startup();

            // this.chat.clear();
            // this.chat = new Chat(this.model.getCurrentPlayersNum());
            notifyGameSessions(ProtocolConstants.MESSAGE_GAME_START);
            for (GameStartedListener gameStartedListener : gameStartedListeners) {
                gameStartedListener.started();
            }

            return true;
        }

        return false;
    }

    public void notifyAllSessions(String message) {
        List<ISession> sessions = this.server.getSessions();

        for (ISession iSession : sessions) {
            iSession.notifyClient(message);
        }
    }

    public void notifyGameSessions(String message) {
        for (Controller controller : controllers) {
            controller.getSession().notifyClient(message);
        }
    }

    private void terminateGame() {
        for (GameEndedListener gameEndedListener : gameEndedListeners) {
            gameEndedListener.gameEnded();
        }

        this.server.removeGame(this);

        if (!this.started) {
            notifyAllSessions(ProtocolConstants.UPDATE_GAMES_LIST);
        }

        notifyGameSessions(ProtocolConstants.MESSAGE_GAME_KICK);
    }
}
