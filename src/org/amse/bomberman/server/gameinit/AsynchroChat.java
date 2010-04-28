/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.server.gameinit;

import java.util.ArrayList;
import java.util.List;
import org.amse.bomberman.util.ProtocolConstants;  

/**
 * Class that represents lobby chat. And ingame kill info.
 * @author Kirilchuk V.E.
 */
public class AsynchroChat {

    private final Game game;

    public AsynchroChat(Game game) {
        this.game = game;
    }

    /**
     * Adding message to chat in next notation:
     * <p>
     * playerName: message.
     * @param playerName nickName of player that added message.
     * @param message message to add to chat.
     */
    public void addMessage(String playerName, String message) {
        List<String> forClients = new ArrayList<String>();
        forClients.add(ProtocolConstants.CAPTION_GET_CHAT_MSGS);
        forClients.add(playerName + ": " + message);
        this.game.notifyGameSessions(forClients);
    }

    public void addKillMessage(String message) {
        List<String> forClients = new ArrayList<String>();
        forClients.add(ProtocolConstants.CAPTION_GET_CHAT_MSGS);
        forClients.add(message);
        this.game.notifyGameSessions(forClients);
    }

    /**
     * Always returns "No new messages."
     * @param chatID id of player that wants to get new messages.
     * @return list of messages or list with only item - "No new messages."
     */
    public List<String> getNewMessages(int chatID) {
        List<String> result = new ArrayList<String>();

        result.add("No new messages.");

        return result;
    }
}