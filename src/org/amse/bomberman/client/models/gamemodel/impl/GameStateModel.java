package org.amse.bomberman.client.models.gamemodel.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.client.models.listeners.GameStateListener;
import org.amse.bomberman.client.net.ServerListener;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.protocol.ProtocolMessage;

/**
 *
 * @author Kirilchuk V.E.
 */
public class GameStateModel implements ServerListener {

    private final List<GameStateListener> listeners
            = new CopyOnWriteArrayList<GameStateListener>();

    private volatile boolean ended = false;//TODO CLIENT when it must be reset?

    public void received(ProtocolMessage<Integer, String> message) {
        int messageId = message.getMessageId();
        List<String> data = message.getData();
        if(messageId == ProtocolConstants.LEAVE_MESSAGE_ID) {
            setEnded(true);
        } else if(messageId == ProtocolConstants.GAME_TERMINATED_NOTIFY_ID) {
            if(data.get(0).equals(ProtocolConstants.MESSAGE_GAME_KICK)) {
                setEnded(true);
                updateListeners("Host is escaped from game!\n Game terminated.");
            }
        } else if(messageId == ProtocolConstants.END_RESULTS_MESSAGE_ID) {
            setEnded(true);
        }
    }

    private void setEnded(boolean ended) {
        if(this.ended != ended) { //to not spam listeners every time
            this.ended = ended;
            updateListeners();
        }
    }

    public boolean isEnded() {//don`t need synchronize - volatile is enough here
        return ended;
    }

    private void updateListeners() {
        for(GameStateListener listener : listeners) {
            listener.updateGameState();
        }
    }

    public void addListener(GameStateListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GameStateListener listener) {
        listeners.remove(listener);
    }

    private void updateListeners(String error) {
        for(GameStateListener listener : listeners) {
            listener.gameTerminated(error);
        }
    }
}
