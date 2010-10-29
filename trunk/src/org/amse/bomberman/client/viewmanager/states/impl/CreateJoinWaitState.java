package org.amse.bomberman.client.viewmanager.states.impl;

import org.amse.bomberman.client.models.impl.ClientStateModel;
import org.amse.bomberman.client.models.impl.ClientStateModel.ClientState;
import org.amse.bomberman.client.models.listeners.ClientStateModelListener;
import org.amse.bomberman.client.viewmanager.State;
import org.amse.bomberman.client.viewmanager.ViewManager;

/**
 *
 * @author Kirilchuk V.E.
 */
public class CreateJoinWaitState extends AbstractState
                                 implements ClientStateModelListener {

    public CreateJoinWaitState(ViewManager machine, State previous) {
        super(machine);
        setPrevious(previous);
        //Previous state for Lobby is CreateJoinState!!! Not WaitState!!!
        setNext(new LobbyViewState(machine, previous));
    }

    @Override
    public void init() {
        getWizard().showDialog();
        getController().getContext().getClientStateModel().addListener(this);
    }

    @Override
    public void release() {
        getController().getContext().getClientStateModel().removeListener(this);
    }

    @Override
    public void previous() {
        throw new UnsupportedOperationException("This state does not support this.");
    }

    @Override
    public void next() {
        throw new UnsupportedOperationException("This state does not support this.");
    }

    @Override
    public void clientStateChanged() {
        ClientStateModel model = getController().getContext().getClientStateModel();
        if (model.getState() == ClientStateModel.ClientState.LOBBY) {
            getWizard().hideDialog();
            machine.setState(next);
        }
    }

    @Override
    public void clientStateError(ClientState state, String error) {
        switch (state) {
            case NOT_JOINED: {//NOT_JOINED state that caused error
                getWizard().hideDialog();
                getWizard().showError(error);
                machine.setState(previous);
                break;
            }
        }
    }
}

