package org.amse.bomberman.client.models.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.client.models.listeners.GameInfoModelListener;
import org.amse.bomberman.client.net.ServerListener;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.protocol.ProtocolMessage;

/**
 *
 * @author Kirilchuk V.E.
 */
public class GameInfoModel implements ServerListener {

    private final List<GameInfoModelListener> listeners 
            = new CopyOnWriteArrayList<GameInfoModelListener>();

    private volatile List<String> gameInfo = new ArrayList<String>(0);

    //TODO CLIENT must be not here
    public void received(ProtocolMessage<Integer, String> message) {
        int messageId = message.getMessageId();
        List<String> data = message.getData();
        if (messageId == ProtocolConstants.GAME_INFO_MESSAGE_ID) {
            setGameInfo(data);
        } else if (messageId == ProtocolConstants.BOT_ADD_MESSAGE_ID) {
            if (!data.get(0).equals("Bot added.")) {
                updateListeners("Can not join bot.\n" + data.get(0));
            }
        } else if (messageId == ProtocolConstants.KICK_PLAYER_MESSAGE_ID) {
            if (!data.get(0).equals("Kicked.")) {
                updateListeners("Kick error.\n" + data.get(0));
            }
        }
    }

    public List<String> getGameInfo() {//don`t need sychronized - volatile is enough here
        return gameInfo;
    }

    private void setGameInfo(List<String> data) {
        this.gameInfo = data;
        updateListeners();
    }

    public void addListener(GameInfoModelListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GameInfoModelListener listener) {
        listeners.remove(listener);
    }

    private void updateListeners() {
        for (GameInfoModelListener listener : listeners) {
            listener.updateGameInfo();
        }
    }

    private void updateListeners(String string) {
        for (GameInfoModelListener listener : listeners) {
            listener.gameInfoError(string);
        }
    }
}
