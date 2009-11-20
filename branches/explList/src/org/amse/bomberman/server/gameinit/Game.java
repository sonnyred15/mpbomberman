/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit;

import java.util.ArrayList;
import java.util.List;
import org.amse.bomberman.server.gameinit.imodel.IModel;
import org.amse.bomberman.server.gameinit.imodel.impl.Model;

/**
 *
 * @author Kirilchuk V.E
 */
public class Game {

    private boolean started = false;
    private String gameName = "game";
    private int maxPlayers;
    private List<Player> players;
    private IModel model;

    public Game(GameMap map, String gameName) {
        this.started = false;
        this.gameName = gameName;
        this.maxPlayers = map.getMaxPlayers();
        this.players = new ArrayList<Player>();
        this.model = new Model(map, this);
    //CHECK ^ THIS!//
    }

    public Game(GameMap map, String gameName, int maxPlayers) {
        this(map, gameName);
        if (maxPlayers > 0 && maxPlayers <= this.maxPlayers) {
            this.maxPlayers = maxPlayers;
        }
    }

    public Player join(String name) {
        if (players.size() == this.maxPlayers) { //CHECK < THIS// what if size>max???
            return null;
        } else {
            Player player = new Player(name, players.size() + 1);
            //coordinates of players will be set when game would start!!!!
            players.add(player);
            return player;
        }
    }

    public void disconnectFromGame(Player player) {
        this.players.remove(player);
        this.model.removePlayer(player.getID());
    }

    public void placeBomb(Player player) {
        if (this.started != false) {
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

    public boolean doMove(Player player, int direction) {
        if (this.started != false) {
            if (player.isAlive()) {
                return model.doMove(player, direction);
            }
        }
        return false;
    }

    public int[][] getMapArray() {
        return this.model.getMapArray();
    }
    
    public List<Pair> getExplosionSquares(){
        return this.model.getExplosionSquares();
    }

    public boolean isStarted() {
        return this.started;
    }

    public String getName() {
        return this.gameName;
    }

    public void playerBombed(int id) {
        for (Player player : players) {
            if (player.getID() == id) {
                player.bombed();
                if (!player.isAlive()) {
                    model.removePlayer(id);
                }
            }
        }
    }
    
    public int getGameMaxPlayers(){
        return this.maxPlayers;
    }
    
    public int getCurrentPlayers(){
        return this.players.size();
    }
}
