package org.amse.bomberman.client.models.listeners;

/**
 *
 * @author Kirilchuk V.E.
 */
public interface GameModelListener {

    void gameModelChanged();

    void gameModelErrot(String error);
}
