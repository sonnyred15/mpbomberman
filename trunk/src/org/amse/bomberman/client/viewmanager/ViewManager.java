package org.amse.bomberman.client.viewmanager;

import org.amse.bomberman.client.viewmanager.states.StartWaitState;
import org.amse.bomberman.client.viewmanager.states.NotConnectedState;
import org.amse.bomberman.client.viewmanager.states.CreateJoinWaitState;
import org.amse.bomberman.client.viewmanager.states.LobbyViewState;
import org.amse.bomberman.client.viewmanager.states.GameViewState;
import org.amse.bomberman.client.viewmanager.states.CreateJoinViewState;
import javax.swing.SwingUtilities;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.client.models.impl.ConnectionStateModel;
import org.amse.bomberman.client.models.listeners.ConnectionStateListener;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.view.wizard.Wizard;
import org.amse.bomberman.client.view.wizard.WizardEvent;
import org.amse.bomberman.client.view.wizard.WizardListener;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ViewManager implements WizardListener, ConnectionStateListener {

    private final Wizard     wizard;
    private final Controller controller;

    private final State notConnectedState;
    private final State createJoinViewState;
    private final State createJoinWaitState;
    private final State lobbyViewState;
    private final State startWaitState;
    private final State gameViewState;

    private volatile State state;

    public ViewManager(Controller controller) {
        this.controller = controller;
        wizard = new Wizard();

        notConnectedState   = new NotConnectedState(this);
        createJoinViewState = new CreateJoinViewState(this);
        createJoinWaitState = new CreateJoinWaitState(this);
        lobbyViewState      = new LobbyViewState(this);
        startWaitState      = new StartWaitState(this);
        gameViewState       = new GameViewState(this);

        initStatesConnections();
        setState(getNotConnectedState());
    }

    private void next() {
        state.next();
    }

    private void previous() {
        state.previous();
    }

    public Wizard getWizard() {
        return wizard;
    }

    public Controller getController() {
        return controller;
    }

    public void setState(State state) {
        if (this.state != null) {
            this.state.release();
        }
        this.state = state;
        state.init();
    }

    public void showView() {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                wizard.addListener(ViewManager.this);
                wizard.setVisible(true);
            }
        });
    }

    //Always called from EDT
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

    public State getCreateJoinViewState() {
        return createJoinViewState;
    }

    public State getCreateJoinWaitState() {
        return createJoinWaitState;
    }

    public State getGameViewState() {
        return gameViewState;
    }

    public State getLobbyViewState() {
        return lobbyViewState;
    }

    public State getNotConnectedState() {
        return notConnectedState;
    }

    public State getStartWaitState() {
        return startWaitState;
    }

    private void initStatesConnections() {
        notConnectedState
                .setPrevious(null)
                .setNext(createJoinViewState);
        createJoinViewState
                .setPrevious(notConnectedState)
                .setNext(createJoinWaitState);
        createJoinWaitState
                .setPrevious(createJoinViewState)
                .setNext(lobbyViewState);
        lobbyViewState
                .setPrevious(createJoinViewState)
                .setNext(gameViewState);
        gameViewState
                .setPrevious(createJoinViewState)
                .setNext(null);
    }

    public void connectionStateChanged() {
        ConnectionStateModel model = controller.getContext().getConnectionStateModel();
        if (!model.isConnected()) {//if we disconnected
            wizard.cancelWaitingDialog();
            wizard.showError(NetException.MESSAGE);
            setState(getNotConnectedState());
        }
    }

    public void connectionError(String error) {
        //TODO ignore or what?
    }
}
