package org.amse.bomberman.client.viewmanager;

import org.amse.bomberman.client.viewmanager.states.impl.NotConnectedState;
import javax.swing.SwingUtilities;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.client.models.impl.ConnectionStateModel;
import org.amse.bomberman.client.models.listeners.ConnectionStateListener;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.view.wizard.Wizard;
import org.amse.bomberman.client.view.wizard.WizardEvent;
import org.amse.bomberman.client.view.wizard.WizardListener;

/**
 * Class that manages View(part of MVC).
 *
 * @author Kirilchuk V.E.
 */
public class ViewManager implements WizardListener, ConnectionStateListener {

    private final Wizard     wizard;
    private final Controller controller;

    private volatile State state;
    private final State notConnected;

    /**
     * Creates ViewManager with specified Controller.
     *
     * @param controller controller part of MVC.
     */
    public ViewManager(Controller controller) {
        this.controller = controller;
        wizard = new Wizard();
        notConnected = new NotConnectedState(this);
        state = notConnected;
        state.init();
    }

    private void next() {
        state.next();
    }

    private void previous() {
        state.previous();
    }

    /**
     * Returns wizard frame.
     *
     * @return wizard frame.
     */
    public Wizard getWizard() {
        return wizard;
    }

    /**
     * @return controller.
     */
    public Controller getController() {
        return controller;
    }

    /**
     * Sets new state for view. Firstly,
     * {@link State#release()} then setting state, and then
     * {@link State#init()}
     *
     * @param state state to set.
     */
    public void setState(State state) {
        this.state.release();
        this.state = state;
        this.state.init();
    }

    /**
     * Shows Wizard.
     */
    public void showWizard() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                wizard.addListener(ViewManager.this);
                wizard.setVisible(true);
            }
        });
    }

    /**
     * Executes action that corresponds for specified
     * wizard event.
     * <p>Assumes that this method is always called from
     * Event Dispatch Thread.
     *
     * @param event event from wizard.
     */
    @Override
    public void wizardEvent(WizardEvent event) {
        switch (event) {
            case BACK_PRESSED: {
                previous();
                break;
            }
            case NEXT_PRESSED: {
                next();
                break;
            }
            case CANCEL_PRESSED: {
                wizard.dispose();
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown wizard event.");
            }
        }
    }

    /**
     * Implementation of ConnectionStateListener interface method.
     * Reacts on connection lost by showing error in View.
     */
    @Override
    public void connectionStateChanged() {
        ConnectionStateModel model = controller.getContext().getConnectionStateModel();
        if (!model.isConnected()) {//if we disconnected
            wizard.showError(NetException.MESSAGE);
            wizard.hideDialog();
            setState(notConnected);
        }
    }

    /**
     * Implementation of ConnectionStateListener interface method.
     * Do nothing.
     */
    @Override
    public void connectionError(String error) {
        //this is for situations when we try to connect, but have error.
        //We don`t need to do something here.
    }
}
