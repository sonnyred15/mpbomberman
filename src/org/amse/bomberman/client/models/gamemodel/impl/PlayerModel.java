package org.amse.bomberman.client.models.gamemodel.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.client.models.gamemodel.Player;
import org.amse.bomberman.client.models.listeners.PlayerModelListener;

/**
 * Class that represents player model. Additionally contains name parameter.
 * In fact this is name of client, not player.
 *
 * @author Kirilchuk V.E.
 */
public class PlayerModel {

    private final List<PlayerModelListener> listeners
            = new CopyOnWriteArrayList<PlayerModelListener>();

    private volatile Player player  = new PlayerImpl();
    private volatile String name    = "Unnamed";

    /**
     * Sets player. This method notifies listeners.
     *
     * @param player player to set.
     */
    public void setPlayer(Player player) {
        this.player = player;
        updateListeners();
    }

    /**
     * @return player.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets client/player name. This method notifies listeners.
     *
     * @param player
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return client/player name.
     */
    public String getName() {
        return name;
    }

    private void updateListeners() {
        for(PlayerModelListener listener : listeners) {
            listener.updatePlayer();
        }
    }

    /**
     * Adds listener to this model.
     *
     * @param listener to add.
     */
    public void addListener(PlayerModelListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes listener from this model.
     *
     * @param listener listener to remove.
     */
    public void removeListener(PlayerModelListener listener) {
        listeners.remove(listener);
    }
}
