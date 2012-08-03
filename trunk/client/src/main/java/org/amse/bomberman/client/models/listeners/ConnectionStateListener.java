package org.amse.bomberman.client.models.listeners;

/**
 *
 * @author Kirilchuk V.E.
 */
public interface ConnectionStateListener {

    void connectionStateChanged();

    /**
     * Method of listener to process connection error.
     *
     * @param error description of connection fail.
     */
    void connectionError(String error);
}
