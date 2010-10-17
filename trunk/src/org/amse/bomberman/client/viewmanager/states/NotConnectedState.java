package org.amse.bomberman.client.viewmanager.states;

import java.net.UnknownHostException;
import org.amse.bomberman.client.control.AsynchroCaller;
import org.amse.bomberman.client.models.gamemodel.impl.GameModel;
import org.amse.bomberman.client.view.WaitingDialog.DialogResult;
import org.amse.bomberman.client.view.wizard.panels.ConnectionPanel;
import org.amse.bomberman.client.viewmanager.ViewManager;

/**
 *
 * @author Kirilchuk V.E.
 */
public class NotConnectedState extends AbstractState implements AsynchroCaller {
    private final ConnectionPanel panel = new ConnectionPanel();
    private static final String  BACK = "Exit";
    private static final String  NEXT = "Connect";
 
    private DialogResult wait;

    public NotConnectedState(ViewManager machine) {
        super(machine);
    } 

    public void previous() {
        getWizard().dispose();
    } 

    public void next() {//TODO CLIENT rewrite ugly logic
        try {//TODO CLIENT remove asynhro controller! connect must be taked from connection model
            getController().connect(this, panel.getIPAddress(), panel.getPort());
            wait = DialogResult.OPENED;
            wait = getWizard().showWaitingDialog();
        } catch (UnknownHostException ex) {
            getWizard().showError("Unknown host.\n" + ex.getMessage());
        }
    }

    public void init() {
        getWizard().setPanel(panel);
        getWizard().setBackText(BACK);
        getWizard().setNextText(NEXT);
    } 

    private GameModel getGameModel() {
        return getController().getContext().getGameModel();
    }

    //will be called from another thread
    public void returnOk() {
        if(wait == DialogResult.OPENED) {
            getWizard().closeWaitingDialog();//this in EDT
            getGameModel().setPlayerName(panel.getPlayerName());//TODO CLIENT this must be done after server response on setName
            getController().requestSetPlayerName(getGameModel().getPlayerName());
            machine.setState(next);
        } else {//canceled
            getController().disconnect();
        }
    }

    //will be called from another thread
    public void returnException(Exception ex) {
        if(wait == DialogResult.OPENED) {
            getWizard().cancelWaitingDialog();//this in EDT
            getWizard().showError("Can not connect to the server.\n" + ex.getMessage());
        }
    }
}


