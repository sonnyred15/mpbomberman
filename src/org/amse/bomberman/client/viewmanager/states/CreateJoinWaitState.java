package org.amse.bomberman.client.viewmanager.states;

import org.amse.bomberman.client.models.impl.ClientStateModel;
import org.amse.bomberman.client.models.impl.ClientStateModel.State;
import org.amse.bomberman.client.models.listeners.ClientStateModelListener;
import org.amse.bomberman.client.view.WaitingDialog.DialogResult;
import org.amse.bomberman.client.viewmanager.ViewManager;

/**
 *
 * @author Kirilchuk V.E.
 */
public class CreateJoinWaitState extends AbstractState
                                 implements ClientStateModelListener {

    public CreateJoinWaitState(ViewManager machine) {
        super(machine);
        getController().getContext().getClientStateModel().addListener(this);
    }

    public void init() {
        //blocking here
        DialogResult result = getWizard().showWaitingDialog(); 
        if(result == DialogResult.CANCELED) {
            getController().requestLeaveGame();
            previous();
        } else {
            next();
        }
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
           case LOBBY: {
               getWizard().cancelWaitingDialog();
               getWizard().showError(error);
           }
       }
    }
}

