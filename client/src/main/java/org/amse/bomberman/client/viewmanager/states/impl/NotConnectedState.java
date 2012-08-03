package org.amse.bomberman.client.viewmanager.states.impl;

import java.net.UnknownHostException;
import org.amse.bomberman.client.view.wizard.panels.ConnectionPanel;
import org.amse.bomberman.client.viewmanager.ViewManager;

/**
 *
 * @author Kirilchuk V.E.
 */
public class NotConnectedState extends AbstractState {

    private static final String BACK = "Exit";
    private static final String NEXT = "Connect";

    private final ConnectionPanel panel = new ConnectionPanel();

    public NotConnectedState(ViewManager machine) {
        super(machine);
        setNext(new ConnectionWaitState(machine, panel, this));
    }

    @Override
    public void previous() {
        getWizard().dispose();
    }

    @Override
    public void next() {
        try {
            machine.setState(next);
            getController().connect(panel.getIPAddress(), panel.getPort());
        } catch (UnknownHostException ex) {
            getWizard().showError("Unknown host.\n" + ex.getMessage());
        }
    }

    @Override
    public void init() {
        getWizard().setPanel(panel);
        getWizard().setBackText(BACK);
        getWizard().setNextText(NEXT);
    }
}