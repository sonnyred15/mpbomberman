/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.amse.bomberman.server.gameinit.imodel.IModel;
import org.amse.bomberman.server.gameinit.imodel.impl.Model;
import org.amse.bomberman.server.net.IServer;
import org.amse.bomberman.util.Constants.Direction;

/**
 *
 * @author Kirilchuk V.E
 */
public class Game {

    private final IServer server;
    private final String gameName;
    private final int maxPlayers;
    private final IModel model;
    private final List<Player> players;
    private boolean started;

    public Game(IServer server, GameMap map, String gameName, int maxPlayers) {
        this.server = server;
        this.gameName = gameName;

        int mapMaxPlayers = map.getMaxPlayers();
        if (maxPlayers > 0 && maxPlayers <= mapMaxPlayers) {
            this.maxPlayers = maxPlayers;
        } else {
            this.maxPlayers = mapMaxPlayers;
        }

        this.model = new Model(map, this);
        this.players = Collections.synchronizedList(new ArrayList<Player>());

        this.started = false;
    }

    public String getMapName() {
        return this.model.getMapName();
    }

    public synchronized Player join(String name) {
        Player player = null;

        if (this.players.size() < this.maxPlayers) { 
            player = new Player(name, this.players.size() + 1);
            //coordinates of players will be set when game would start!!!!
            this.players.add(player);
        }

        return player;
    }

    public void disconnectFromGame(Player player) {
        this.players.remove(player);
        this.model.removePlayer(player.getID());
        if (this.players.size() == 0) {
            this.endGame();
        }
    }

    public void placeBomb(Player player) {
        if (this.started) {
            this.model.placeBomb(player);
        }
    }

    public void startGame() {
        this.started = true;
        //Here model must change map to support currrent num of players
        //and then give coordinates.
        this.model.changeMapForCurMaxPlayers(this.players.size());
        for (Player player : players) {
            player.setX(model.xCoordOf(player.getID()));
            player.setY(model.yCoordOf(player.getID()));
        }
    }

    private void endGame() {
        this.server.removeGame(this);
    }

    public boolean doMove(Player player, Direction direction) {
        if (this.started) {
            if (player.isAlive()) {
                return model.doMove(player, direction.getValue());
            }
        }
        return false;
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

    public String getName() {
        return this.gameName;
    }

    public void playerBombed(int id) {
        synchronized (players) { //otherwise iterator could broke
            for (Player player : players) {
                if (player.getID() == id) {
                    player.bombed();
                    if (!player.isAlive()) {
                        model.removePlayer(id);
                    }
                    break;
                }
            }
        }
    }

    public int getGameMaxPlayers() {
        return this.maxPlayers;
    }

    public int getCurrentPlayers() {
        return this.players.size();
    }
}
