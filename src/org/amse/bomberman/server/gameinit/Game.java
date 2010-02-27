/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit;

import org.amse.bomberman.server.gameinit.bot.Bot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.amse.bomberman.server.gameinit.imodel.IModel;
import org.amse.bomberman.server.gameinit.imodel.impl.Model;
import org.amse.bomberman.server.net.IServer;
import org.amse.bomberman.server.net.ISession;
import org.amse.bomberman.util.Constants.Direction;

/**
 *
 * @author Kirilchuk V.E
 */
public class Game {

    private final IServer server;
    private Player owner;
    private final String gameName;
    private final int maxPlayers;
    private final IModel model;
    private final List<Player> players;
    private final List<ISession> sessions;
    private boolean started;
    private final DieListener dieListener;
    private final Chat chat;

    public Game(IServer server, GameMap map, String gameName, int maxPlayers) {
        this.server = server;
        this.gameName = gameName;

        int mapMaxPlayers = map.getMaxPlayers();
        if (maxPlayers > 0 && maxPlayers <= mapMaxPlayers) {
            this.maxPlayers = maxPlayers;
        } else {
            this.maxPlayers = mapMaxPlayers;
        }
        this.chat = new Chat(this.maxPlayers);

        this.model = new Model(map, this);
        this.sessions = Collections.synchronizedList(new ArrayList<ISession>());
        this.players = Collections.synchronizedList(new ArrayList<Player>());
        this.dieListener = new DieListener(this);

        this.started = false;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public String getMapName() {
        return this.model.getMapName();
    }

    public synchronized Player join(String name, ISession session) {
        Player player = null;

        if (this.players.size() < this.maxPlayers) {
            player = new Player(name);
            //coordinates of players will be set when game would start!!!!
            this.players.add(player);

            player.setDieListener(dieListener);
        }

        return player;
    }

    public synchronized Bot joinBot(String name) {
        Bot bot = null;
        if (this.players.size() < this.maxPlayers) {
            bot = this.model.addBot(name);
            if (bot != null) {
                this.players.add(bot);
                bot.setDieListener(dieListener);
            }
        }
        return bot;
    }

    public Player getPlayer(int id) {//MUST BE USED ONLY AFTER GAME IS STARTED!!
        synchronized (players) {
            for (Player player : players) {
                if (player.getID() == id) {
                    return player;
                }
            }
        }
        return null;
    }

    public void disconnectFromGame(Player player) {
        this.players.remove(player);

        synchronized(this.sessions){
            for (ISession session : sessions) {
                if(session.correspondTo(player)){
                    this.sessions.remove(session);
                    break;
                }
            }
        }

        if (this.started) {//removing player from GameMap
            this.model.removePlayer(player.getID());
        }
        if (player == this.owner) {
            this.endGame();
        }
    }

    public void placeBomb(Player player) {
        if (this.started) {
            this.model.placeBomb(player);
        }
    }

    public void startGame() {
        //Here model must change map to support currrent num of players
        //and then give coordinates.
        this.model.changeMapForCurMaxPlayers(this.players.size());
        int i = 1; //giving id`s to players
        for (Player player : players) {
            player.setID(i);
            Pair playerPosition = new Pair(model.xCoordOf(i), model.yCoordOf(i));
            player.setPosition(playerPosition);
            ++i;
        }
        this.started = true;
        this.chat.clear();
        this.model.startBots();
    }

    private void endGame() {
        this.server.removeGame(this);
    }

    public boolean doMove(Player player, Direction direction) {
        if (this.started) {
            if (player.isAlive()) {
                return model.doMove(player, direction);
            }
        }
        return false;
    }

    public void addMessageToChat(Player player, String message) {
        synchronized (this.chat) {
            int chatID = this.players.indexOf(player);
            if (chatID != -1) {
                this.chat.addMessage(maxPlayers, player.getNickName(), message);
            }
        }
    }

    public List<String> getNewMessagesFromChat(Player player){
        synchronized(this.chat){
            int chatID = this.players.indexOf(player);
            if (chatID != -1) {
                return this.chat.getNewMessages(chatID);
            }
        }
        return null;
    }

    public int[][] getMapArray() {
        return this.model.getMapArray();
    }

    public List<Pair> getExplosionSquares() {
        return this.model.getExplosionSquares();
    }

    public boolean isStarted() {
        return this.started;
    }

    public boolean isFull() {
        return (this.players.size() == this.maxPlayers ? true : false);
    }

    public String getName() {
        return this.gameName;
    }

    public void playerDied(Player player) {
        model.removePlayer(player.getID());
    }

    public int getGameMaxPlayers() {
        return this.maxPlayers;
    }

    public List<Player> getCurrentPlayers() {
        return Collections.unmodifiableList(players);
    }

    public int getCurrentPlayersNum() {
        return this.players.size();
    }

    public Player getOwner() {
        return owner;
    }

    public List<ISession> getSessions(){//mb unmodifiable
        return this.sessions;
    }
}
