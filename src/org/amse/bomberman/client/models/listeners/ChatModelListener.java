package org.amse.bomberman.client.models.listeners;

/**
 *
 * @author Kirilchuk V.E.
 */
public interface ChatModelListener {

    void updateChat();

    void chatError(String message);
}
