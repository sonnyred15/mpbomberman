package org.amse.bomberman.client.models.listeners;

/**
 *
 * @author Kirilchuk V.E.
 */
public interface ResultModelListener {

    void updateResults();

    void resultsError(String error);
}
