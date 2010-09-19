/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.amse.bomberman.server.gameinit.renameme.InGameState;
import org.amse.bomberman.server.net.tcpimpl.Controller;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Lobby {

    final AsynchroChat chat;
    final Set<Controller> controllers;
    Controller owner; // perhaps owner can change during game
    Game game;

    public Lobby(Game game) {
        this.chat = new AsynchroChat(null); //TODO not null!!!
        this.controllers = new CopyOnWriteArraySet<Controller>();
        this.game = game;
    }

    public void tryAddBot(Controller controller, String botName) {

    }

    public void tryRemoveBot() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void addMessageToChat(Controller controller, String message) {
        this.chat.addMessage(message);
    }

    /**
     * Return list of new messages from chat. See SynchroChat class to view
     * what will be sended if there is no new messages.
     * @see SynchroChat
     * @param playerID ID of player that requests his new messages.
     * @return list of new messages from chat.
     */
    public void getNewMessagesFromChat() {
        //TODO
    }

    /**
     * Return number of all players in game including bots.
     * @return number of all players in game including bots.
     */
    public int getCurrentPlayersNum() {
        return controllers.size();
    }

    public void getGameInfo() {//TODO info about players, names and so on..
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void tryStartGame(Controller controller) {//TODO only owner can start
        //TODO
        if(true) { //started
            for (Controller client : controllers) {
                client.setState(new InGameState(client, this.game));
            }
        }
    }

    public void leave(Controller controller) {//TODO if owner leave collapse =)
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
