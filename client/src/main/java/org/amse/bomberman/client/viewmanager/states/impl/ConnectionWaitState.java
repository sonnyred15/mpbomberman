package org.amse.bomberman.client.viewmanager.states.impl;

import org.amse.bomberman.client.models.impl.ConnectionStateModel;
import org.amse.bomberman.client.models.listeners.ConnectionStateListener;
import org.amse.bomberman.client.view.wizard.panels.ConnectionPanel;
import org.amse.bomberman.client.viewmanager.State;
import org.amse.bomberman.client.viewmanager.ViewManager;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ConnectionWaitState extends AbstractState implements ConnectionStateListener {

    //TODO CLIENT not need to have referense to this.
    //Previous state can just set name after changing state to this.
    private final ConnectionPanel panel;

    public ConnectionWaitState(ViewManager machine, ConnectionPanel panel, State previous) {
        super(machine);        
        this.panel = panel;
        setPrevious(previous);
        //previous state of createJoinViewState is notConnectedState!!! Not this!
        setNext(new CreateJoinViewState(machine, previous));
    }

    @Override
    public void init() {
        getWizard().showDialog();
        getController().getContext().getConnectionStateModel().addListener(this);
    }

    @Override
    public void release() {
       getController().getContext().getConnectionStateModel().removeListener(this);
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
    public void connectionStateChanged() {//will be called from executors thread.
        ConnectionStateModel model = getController().getContext().getConnectionStateModel();
        if (!model.isConnected()) {//TODO CLIENT does it really need?
            return;
        }

        if (model.isConnected()) {
            getWizard().hideDialog();
            getController().requestSetClientName(panel.getPlayerName());
            machine.setState(next);
        }
    }

    @Override
    public void connectionError(String error) {//will be called from executors thread.
        getWizard().hideDialog();
        getWizard().showError("Can not connect to the server.\n" + error);
        machine.setState(previous);
    }
}
