package org.amse.bomberman.client.viewmanager.states.impl;

import org.amse.bomberman.client.models.impl.ClientStateModel;
import org.amse.bomberman.client.models.impl.ClientStateModel.State;
import org.amse.bomberman.client.models.listeners.ClientStateModelListener;
import org.amse.bomberman.client.view.WaitingDialog.DialogState;
import org.amse.bomberman.client.viewmanager.ViewManager;

/**
 *
 * @author Kirilchuk V.E.
 */
public class CreateJoinWaitState extends AbstractState
                                 implements ClientStateModelListener {

    public CreateJoinWaitState(ViewManager machine) {
        super(machine);        
    }

    @Override
    public void init() {
        getController().getContext().getClientStateModel().addListener(this);
    }

    @Override
    public void release() {
        getController().getContext().getClientStateModel().removeListener(this);
    }

    @Override
    public void previous() {
        machine.setState(previous);
    }

    @Override
    public void next() {
        machine.setState(next);
    }

    @Override
    public void clientStateChanged() {
        ClientStateModel model = getController().getContext().getClientStateModel();
        if (model.getState() == ClientStateModel.State.LOBBY) {
            next();
        }
    }

    @Override
    public void clientStateError(State state, String error) {
        switch (state) {
            case NOT_JOINED: {//NOT_JOINED state that caused error
                getWizard().showError(error);
                previous();
                break;
            }
        }
    }
}

