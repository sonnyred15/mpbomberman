package org.amse.bomberman.client.viewmanager.states;

import java.net.UnknownHostException;
import org.amse.bomberman.client.models.impl.ConnectionStateModel;
import org.amse.bomberman.client.models.listeners.ConnectionStateListener;
import org.amse.bomberman.client.view.WaitingDialog.DialogResult;
import org.amse.bomberman.client.view.wizard.panels.ConnectionPanel;
import org.amse.bomberman.client.viewmanager.ViewManager;

/**
 *
 * @author Kirilchuk V.E.
 */
public class NotConnectedState extends AbstractState implements ConnectionStateListener {

    private final ConnectionPanel panel = new ConnectionPanel();
    private static final String BACK = "Exit";
    private static final String NEXT = "Connect";

    private DialogResult wait;

    public NotConnectedState(ViewManager machine) {
        super(machine);
    }

    public void previous() {
        getWizard().dispose();
    }

    public void next() {
        try {
            getController().connect(panel.getIPAddress(), panel.getPort());
            wait = DialogResult.OPENED;
            wait = getWizard().showWaitingDialog();
            //other logic in connectionStateChanged() method.
        } catch (UnknownHostException ex) {
            getWizard().showError("Unknown host.\n" + ex.getMessage());
        }
    }

    public void init() {
        getController().getContext().getConnectionStateModel().addListener(this);
        getWizard().setPanel(panel);
        getWizard().setBackText(BACK);
        getWizard().setNextText(NEXT);
    }

    @Override
    public void release() {
        getController().getContext().getConnectionStateModel().removeListener(this);
    }

    public void connectionStateChanged() {//will be called from executors thread.
        ConnectionStateModel model = getController().getContext().getConnectionStateModel();
        if(model.isConnected()) {
            if(wait == DialogResult.OPENED) {
                getWizard().closeWaitingDialog();//this in EDT
                getController().requestSetPlayerName(panel.getPlayerName());
                machine.setState(next);
            } else {//canceled
                getController().disconnect();
            }
        }
    }

    public void connectionError(String error) {
        if(wait == DialogResult.OPENED) {
            getWizard().cancelWaitingDialog();//this in EDT
            getWizard().showError("Can not connect to the server.\n" + error);
        }
    }
}
