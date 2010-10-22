package org.amse.bomberman.client.models.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.client.models.listeners.ClientStateModelListener;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ClientStateModel {

    private final List<ClientStateModelListener> listeners = new CopyOnWriteArrayList<ClientStateModelListener>();
    private volatile State state = State.NOT_CONNECTED;

    public enum State {

        NOT_CONNECTED, NOT_JOINED, LOBBY, GAME;
    }

    public void setState(State state) {
        this.state = state;
        updateListeners();
    }

    public State getState() {
        return state;
    }

    //TODO CLIENT do something with logic...
    public void stateChangeError(State state, String error) {
        updateListeners(state, error);
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
