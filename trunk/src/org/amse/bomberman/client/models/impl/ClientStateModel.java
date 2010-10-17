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
                state = State.LOBBY;
                updateListeners();
            } else {
                updateListeners(State.LOBBY, "Can not create game.\n" + data.get(0));
            }
        } else if (messageId == ProtocolConstants.JOIN_GAME_MESSAGE_ID) {
            if (data.get(0).equals("Joined.")) {
                state = State.LOBBY;
                updateListeners();
            } else {
                updateListeners(State.LOBBY, "Can not join to the game.\n" + data.get(0));
            }
        } else if (messageId == ProtocolConstants.START_GAME_MESSAGE_ID) {
            if (data.get(0).equals("Game started.")) {
                state = State.GAME;
                updateListeners();
            } else {
                updateListeners(State.LOBBY, "Can not start game.\n" + data.get(0));
            }
        } else if (messageId == ProtocolConstants.GAME_STARTED_NOTIFY_ID) {
            state = State.GAME;
            updateListeners();
        } else if (messageId == ProtocolConstants.LEAVE_MESSAGE_ID) {
            if (data.get(0).equals("Disconnected.")) {
                state = State.NOT_JOINED;
                updateListeners();
            } else {
                updateListeners(State.NOT_JOINED, "Can not leave game.\n" + data.get(0));
            }
        } else if (messageId == ProtocolConstants.GAME_STATUS_MESSAGE_ID) {
            if (data.get(0).equals("true")) {
                //state = State.GAME;
                //updateListeners(); //TODO CLIENT
            }
        }
    }

    public void setState(State state) {
        this.state = state;

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

    private void updateListeners(State state, String string) {
        for (ClientStateModelListener listener : listeners) {
            listener.clientStateError(state, string);
        }
    }
}
