
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class that represents lobby chat and temporary(until better dicision)
 * represents the ingame log of some events like player deaths...
 * @author Kirilchuk V.E.
 */
public class Chat {

    // TODO this version of chat is compromise for syncronous and asynchronous
    // Session. Better way in asynchronous server is to send new messages to
    // clients and not to store them. BUt syncronous session can`t send in such way.
    // TODO make separate realization of ingame events log.
    private final static int   KEEP_OLD_MSGS_NUM = 10;
    private final List<String> messages = new CopyOnWriteArrayList<String>();
    private final int[]        lastTakedMessageIndexes;

    /**
     * Constructor of chat. Creates it for specified number of players.
     * @param maxPlayers maximum players num in this chat.
     */
    public Chat(int maxPlayers) {
        this.lastTakedMessageIndexes = new int[maxPlayers];

        for (int i = 0; i < lastTakedMessageIndexes.length; ++i) {
            lastTakedMessageIndexes[i] = Integer.MAX_VALUE;
        }
    }

    /**
     * Adding message to chat in next notation:
     * <p>
     * playerName: message.
     * @param playerName nickName of player that added message.
     * @param message message to add to chat.
     */
    public void addMessage(String playerName, String message) {
        this.messages.add(playerName + ": " + message);
    }

    /**
     * Returns new messages for specified id. chatId equals playerID.
     * @param chatID id of player that wants to get new messages.
     * @return list of messages or list with only item - "No new messages."
     */
    public List<String> getNewMessages(int chatID) {
        List<String> result = new ArrayList<String>();
        int          from = this.lastTakedMessageIndexes[chatID - 1];

        for (ListIterator<String> it = this.messages.listIterator(from);
                it.hasNext(); ) {
            result.add(it.next());
        }

        this.lastTakedMessageIndexes[chatID - 1] = this.messages.size();
        tryRemoveOldMessages();

        if (result.size() == 0) {
            result.add("No new messages.");
        }

        return result;
    }

    /**
     * Methd that clears chat.
     */
    public void clear() {    // TODO must not set lastTakedMessages to zero to leaved players and bots.
        this.messages.clear();

        for (int i = 0; i < this.lastTakedMessageIndexes.length; ++i) {
            this.lastTakedMessageIndexes[i] = 0;
        }
    }

    /**
     * If player leaved from game, chat must ignore number of taked by
     * him messages. It is needed to remove unneeded messages from messages list.
     * So, this method sets the lastTakedMessageIndexes for removed player to
     * <code>Integer.MAX_VALUE</code>.
     * @param chatID id of player to remove from chat.
     */
    public void removePlayer(int chatID) {
        this.lastTakedMessageIndexes[chatID - 1] = Integer.MAX_VALUE;
    }

    /**
     * Not actually adding player to chat. Maximum players of chat never changes
     * during it`s work. This method is for situations when one player leaved
     * from game and another player joined on his place. So chat must
     * unignore lastTakedMessageIndexex for this ID.
     * <p>ID must be smaller than maximum players parametr of chat or you
     * will get IndexOutOfBoundsException.
     * @param chatID id of player to add.
     */
    public void addPlayer(int chatID) {
        this.lastTakedMessageIndexes[chatID - 1] = 0;
    }

    private void tryRemoveOldMessages() {
        for (int i = 0; i < this.lastTakedMessageIndexes.length; ++i) {
            if (this.lastTakedMessageIndexes[i] < Chat.KEEP_OLD_MSGS_NUM) {
                return;
            }
        }

        for (int i = 0; i < Chat.KEEP_OLD_MSGS_NUM; ++i) {
            this.messages.remove(0);    // removePlayer new first
        }

        for (int i = 0; i < lastTakedMessageIndexes.length; ++i) {
            if (this.lastTakedMessageIndexes[i] != Integer.MAX_VALUE) {
                this.lastTakedMessageIndexes[i] -= Chat.KEEP_OLD_MSGS_NUM;
            }
        }
    }
}
