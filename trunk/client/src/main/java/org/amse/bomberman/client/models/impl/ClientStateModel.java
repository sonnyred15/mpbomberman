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
    private volatile ClientState state = ClientState.NOT_CONNECTED;

    public enum ClientState {

        NOT_CONNECTED, NOT_JOINED, LOBBY, GAME;
    }

    public void setState(ClientState state) {
        this.state = state;
        updateListeners();
    }

    public ClientState getState() {
        return state;
    }

    //TODO CLIENT do something with logic...
    public void stateChangeError(ClientState state, String error) {
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
    private void updateListeners(ClientState state, String string) {
        for (ClientStateModelListener listener : listeners) {
            listener.clientStateError(state, string);
        }
    }
}
