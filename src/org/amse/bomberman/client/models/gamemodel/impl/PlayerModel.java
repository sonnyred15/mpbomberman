package org.amse.bomberman.client.models.gamemodel.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.client.models.gamemodel.Player;
import org.amse.bomberman.client.models.listeners.PlayerModelListener;

/**
 *
 * @author Kirilchuk V.E.
 */
public class PlayerModel {

    private final List<PlayerModelListener> listeners
            = new CopyOnWriteArrayList<PlayerModelListener>();

    private volatile Player player  = new PlayerImpl();
    private volatile String name = "unnamed";

    public void setPlayer(Player player) {
        this.player  = player;
        updateListeners();
    }

    public Player getPlayer() {
        return player;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private void updateListeners() {
        for(PlayerModelListener listener : listeners) {
            listener.updatePlayer();
        }
    }

    public void addListener(PlayerModelListener listener) {
        listeners.add(listener);
    }

    public void removeListener(PlayerModelListener listener) {
        listeners.remove(listener);
    }
}
