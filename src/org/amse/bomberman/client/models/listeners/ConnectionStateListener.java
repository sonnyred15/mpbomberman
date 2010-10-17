package org.amse.bomberman.client.models.listeners;

/**
 *
 * @author Kirilchuk V.E.
 */
public interface ConnectionStateListener {
    void connectionStateChanged();

    void connectionError(String error);
}
