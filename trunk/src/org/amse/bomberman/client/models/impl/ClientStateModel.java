package org.amse.bomberman.client.models.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.client.models.listeners.ClientStateModelListener;
import org.amse.bomberman.client.net.ServerListener;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.protocol.ProtocolMessage;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ClientStateModel implements ServerListener {

    private final List<ClientStateModelListener> listeners
            = new CopyOnWriteArrayList<ClientStateModelListener>();

    private volatile State state = State.NOT_CONNECTED;

    public State getState() {
        return state;
    }

    public enum State {

        NOT_CONNECTED, NOT_JOINED, LOBBY, GAME;
    }

    //TODO CLIENT must be not here
    public void received(ProtocolMessage<Integer, String> message) {
        int messageId = message.getMessageId();
        List<String> data = message.getData();
        if (messageId == ProtocolConstants.CREATE_GAME_MESSAGE_ID) {
            if (data.get(0).equals("Game created.")) {
                setState(State.LOBBY);
            } else {
                updateListeners(State.NOT_JOINED, "Can not create game.\n" + data.get(0));
            }
        } else if (messageId == ProtocolConstants.JOIN_GAME_MESSAGE_ID) {
            if (data.get(0).equals("Joined.")) {
                setState(State.LOBBY);
            } else {
                updateListeners(State.NOT_JOINED, "Can not join to the game.\n" + data.get(0));
            }
        } else if (messageId == ProtocolConstants.START_GAME_MESSAGE_ID) {
            if (data.get(0).equals("Game started.")) {
                setState(State.GAME);
            } else {
                updateListeners(State.LOBBY, "Can not start game.\n" + data.get(0));
            }
        } else if (messageId == ProtocolConstants.GAME_STARTED_NOTIFY_ID) {
            setState(State.GAME);
        } else if (messageId == ProtocolConstants.LEAVE_MESSAGE_ID) {
            if (data.get(0).equals("Disconnected.")) {
                setState(State.NOT_JOINED);
            } else {
                updateListeners(State.NOT_JOINED, "Can not leave game.\n" + data.get(0));
            }
        } else if (messageId == ProtocolConstants.GAME_STATUS_MESSAGE_ID) {
            //ignore result
        } else if(messageId == ProtocolConstants.GAME_TERMINATED_NOTIFY_ID) {
            if(data.get(0).equals(ProtocolConstants.MESSAGE_GAME_KICK)) {
                updateListeners(State.GAME, "Host is escaped from game!\n Game terminated.");
            }
        }
    }

    public void setState(State state) {
        this.state = state;
        updateListeners();
    }
    
    public void addListener(ClientStateModelListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ClientStateModelListener listener) {
        listeners.remove(listener);
    }

    private void updateListeners() {
        for (ClientStateModelListener listener : listeners) {
            listener.clientStateChanged();
        }
    }

    /**
     * Tells listeners about error while going to next state.
     *
     * @param state next state that can`t be set because of error.
     * @param string description of error.
     */
    private void updateListeners(State state, String string) {
        for (ClientStateModelListener listener : listeners) {
            listener.clientStateError(state, string);
        }
    }
}
