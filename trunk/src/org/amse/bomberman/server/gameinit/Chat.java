/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Chat {

    private final static int KEEP_OLD_MSGS_NUM = 10;
    private final int[] lastTakedMessageIndexes;
    private final List<String> messages = new CopyOnWriteArrayList<String>();

    public Chat(int maxPlayers) {
        this.lastTakedMessageIndexes = new int[maxPlayers];
    }

    public void addMessage(int chatID, String playerName, String message) {
        this.messages.add(playerName + ": " + message);
    }

    public List<String> getNewMessages(int chatID) {
        List<String> result = new ArrayList<String>();

        int from = this.lastTakedMessageIndexes[chatID-1];
        for (ListIterator<String> it = this.messages.listIterator(from); it.hasNext();) {
            result.add(it.next());
        }
        this.lastTakedMessageIndexes[chatID-1] = this.messages.size();

        tryRemoveOldMessages();

        if(result.size()==0){
            result.add("No new messages.");
        }

        return result;
    }

    public void clear() {
        this.messages.clear();
        for (int i = 0; i < this.lastTakedMessageIndexes.length; ++i) {
            this.lastTakedMessageIndexes[i] = 0;
        }
    }

    private void tryRemoveOldMessages() {
        for (int i = 0; i < this.lastTakedMessageIndexes.length; ++i) {
            if (this.lastTakedMessageIndexes[i] < Chat.KEEP_OLD_MSGS_NUM){
                return;
            }
        }

        for (int i = 0; i < Chat.KEEP_OLD_MSGS_NUM; ++i) {
            this.messages.remove(0);//removePlayer new first
        }

        for (int i = 0; i < lastTakedMessageIndexes.length; ++i) {
            if(this.lastTakedMessageIndexes[i]!= Integer.MAX_VALUE){
                this.lastTakedMessageIndexes[i] -= Chat.KEEP_OLD_MSGS_NUM;
            }
        }
    }

    void removePlayer(int chatID) {
        this.lastTakedMessageIndexes[chatID-1] = Integer.MAX_VALUE;
    }
}
