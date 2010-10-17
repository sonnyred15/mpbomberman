package org.amse.bomberman.client.viewmanager.states;

import org.amse.bomberman.client.models.impl.ClientStateModel;
import org.amse.bomberman.client.models.impl.ClientStateModel.State;
import org.amse.bomberman.client.models.impl.GameInfoModel;
import org.amse.bomberman.client.models.listeners.ClientStateModelListener;
import org.amse.bomberman.client.models.listeners.GameInfoModelListener;
import org.amse.bomberman.client.view.wizard.panels.LobbyPanel;
import org.amse.bomberman.client.viewmanager.ViewManager;

/**
 *
 * @author Kirilchuk V.E.
 */
public class LobbyViewState extends AbstractState
                            implements ClientStateModelListener,
                                       GameInfoModelListener {
    private final LobbyPanel panel = new LobbyPanel(getController());
    private static final String  BACK = "Leave";
    private static final String  NEXT = "Start";

    public LobbyViewState(ViewManager machine) {
        super(machine);
        getController().getContext().getGameInfoModel().addListener(this);
        getController().getContext().getClientStateModel().addListener(this);
    }

    public void init() {
        panel.cleanChatArea();
        getController().requestGameInfo();
        getWizard().setPanel(panel);
        getWizard().setBackText(BACK);
        getWizard().setNextText(NEXT);
    }

    public void previous() {
        getController().requestLeaveGame();
        machine.setState(previous);
    }

    public void next() {
        getController().requestStartGame();
        machine.setState(next);
    }

    public void clientStateChanged() {
        ClientStateModel model = getController().getContext().getClientStateModel();
        State state = model.getState();
        switch(state) {
            case GAME: {
                next();
                break;
            }
            case NOT_JOINED: {
                previous();
                break;
            }
        }
    }

    public void clientStateError(State state, String error) {
        //TODO CLIENT
    }

    public void updateGameInfo() {
        GameInfoModel model = getController().getContext().getGameInfoModel();
        panel.setGameInfo(model.getGameInfo());
    }

    public void gameInfoError(String error) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

