package org.amse.bomberman.client.viewmanager.states;

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

    public void init() {
        getController().getContext().getClientStateModel().addListener(this);
        //blocking here
        DialogState result = getWizard().showWaitingDialog();
        //unblocked
        if(result == DialogState.CANCELED) {
            getController().requestLeaveGame();
            previous();
        } else {
            next();
        }
    }

    @Override
    public void release() {
        getController().getContext().getClientStateModel().removeListener(this);
    }

    public void previous() {
        machine.setState(previous);
    }

    public void next() {
        machine.setState(next);
    }

    public void clientStateChanged() {
        ClientStateModel model = getController().getContext().getClientStateModel();
        if(model.getState() == ClientStateModel.State.LOBBY) {
            getWizard().closeWaitingDialog();
        }
    }

    public void clientStateError(State state, String error) {
       switch(state) {
           case LOBBY: {//LOBBY cause error is about going to lobby state
               getWizard().cancelWaitingDialog();
               getWizard().showError(error);
           }
       }
    }
}

