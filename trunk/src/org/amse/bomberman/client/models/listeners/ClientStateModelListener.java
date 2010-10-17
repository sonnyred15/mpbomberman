package org.amse.bomberman.client.models.listeners;

import org.amse.bomberman.client.models.impl.ClientStateModel;

/**
 *
 * @author Kirilchuk V.E.
 */
public interface ClientStateModelListener {

    void clientStateChanged();

    void clientStateError(ClientStateModel.State state, String error);
}
