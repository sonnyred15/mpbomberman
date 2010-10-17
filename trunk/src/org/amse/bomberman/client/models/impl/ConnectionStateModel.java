package org.amse.bomberman.client.models.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.client.models.listeners.ConnectionStateListener;
import org.amse.bomberman.client.net.ServerListener;
import org.amse.bomberman.protocol.ProtocolMessage;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ConnectionStateModel implements ServerListener {
    private final List<ConnectionStateListener> listeners
            = new CopyOnWriteArrayList<ConnectionStateListener>();

    private volatile boolean connected = false;

    public boolean isConnected() {//not need synchronize - volatile enough
        return connected;
    }

    public void setConnected(boolean connected) {//not need synchronize - volatile enough
        if(this.connected != connected) {//to not spam listeners
            this.connected = connected;
            updateListeners();
        }
    }

    public void connectException(Exception ex) {
        updateListeners(ex.getMessage());
    }

    public void received(ProtocolMessage<Integer, String> message) {
        //TODO CLIENT when server send disconnect message
    }

    public void addListener(ConnectionStateListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ConnectionStateListener listener) {
        listeners.remove(listener);
    }

    private void updateListeners() {
        for (ConnectionStateListener listener : listeners) {
            listener.connectionStateChanged();
        }
    }

    private void updateListeners(String error) {
        for(ConnectionStateListener listener : listeners) {
            listener.connectionError(error);
        }
    }
}
