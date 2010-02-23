/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Chat {

    private final int[] lastTakedMessageIndexes;
    private final List<String> messages = new ArrayList<String>();

    public Chat(int maxPlayers) {
        this.lastTakedMessageIndexes = new int[maxPlayers];
    }

    public void addMessage(int chatID, String playerName, String message) {
        this.messages.add(playerName + ": " + message);
    }

    public List<String> getNewMessages(int chatID) {
        List<String> result = new ArrayList<String>();

        int from = this.lastTakedMessageIndexes[chatID];
        for (ListIterator<String> it = this.messages.listIterator(from); it.hasNext();) {
            result.add(it.next());
        }
        this.lastTakedMessageIndexes[chatID] = this.messages.size();

        return result;
    }

    public void clear() {
        this.messages.clear();
        for (int i = 0; i < this.lastTakedMessageIndexes.length; ++i) {
            this.lastTakedMessageIndexes[i] = 0;
        }
    }
}
