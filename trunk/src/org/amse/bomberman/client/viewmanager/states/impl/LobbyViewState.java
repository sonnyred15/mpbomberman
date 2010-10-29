package org.amse.bomberman.client.viewmanager.states.impl;

import java.util.List;
import org.amse.bomberman.client.models.impl.ClientStateModel;
import org.amse.bomberman.client.models.impl.ClientStateModel.ClientState;
import org.amse.bomberman.client.models.impl.GameInfoModel;
import org.amse.bomberman.client.models.listeners.ChatModelListener;
import org.amse.bomberman.client.models.listeners.ClientStateModelListener;
import org.amse.bomberman.client.models.listeners.GameInfoModelListener;
import org.amse.bomberman.client.view.wizard.panels.LobbyPanel;
import org.amse.bomberman.client.viewmanager.State;
import org.amse.bomberman.client.viewmanager.ViewManager;

/**
 *
 * @author Kirilchuk V.E.
 */
public class LobbyViewState extends AbstractState
                            implements ClientStateModelListener,
                                       GameInfoModelListener,
                                       ChatModelListener {
    private static final String  BACK = "Leave";
    private static final String  NEXT = "Start";
    
    private final LobbyPanel panel = new LobbyPanel(getController());

    public LobbyViewState(ViewManager machine, State previous) {
        super(machine);
        setPrevious(previous);
        //Previous state for game state is CreateJoinState!!! Not this!!!
        setNext(new GameViewState(machine, previous));
    }

    @Override
    public void init() {
        getController().getContext().getGameInfoModel().addListener(this);
        getController().getContext().getClientStateModel().addListener(this);
        getController().getContext().getChatModel().addListener(this);

        getController().requestGameInfo();

        getWizard().setPanel(panel);
        getWizard().setBackText(BACK);
        getWizard().setNextText(NEXT);
    }

    @Override
    public void release() {
        panel.clearGameInfo();
        panel.clearChatArea();
        getController().getContext().getGameInfoModel().removeListener(this);
        getController().getContext().getClientStateModel().removeListener(this);
        getController().getContext().getChatModel().removeListener(this);
    }

    @Override
    public void previous() {
        getController().requestLeaveGame();
        machine.setState(previous);
    }

    @Override
    public void next() {
        //if user pressed start button
        getController().requestStartGame();        
    }

    @Override
    public void clientStateChanged() {
        ClientStateModel model = getController().getContext().getClientStateModel();
        ClientState state = model.getState();
        switch(state) {
            case GAME: {
                machine.setState(next);
                break;
            }
            case NOT_JOINED: {
                machine.setState(previous);
                break;
            }
        }
    }

    @Override
    public void clientStateError(ClientState state, String error) {
        switch(state) {
            case LOBBY: {//LOBBY state that caused error
                getWizard().showError(error);//for can`t start
                break;
            }
            case GAME: {//GAME cause game terminated
                getWizard().showError(error);//for host leave
                previous();
                break;
            }
        }
    }

    @Override
    public void updateGameInfo() {
        GameInfoModel model = getController().getContext().getGameInfoModel();
        panel.setGameInfo(model.getGameInfo());
    }

    @Override
    public void gameInfoError(String error) {
        getWizard().showError(error);//for can`t add bot and can`t kick someone
    }

    @Override
    public void updateChat(List<String> newMessages) {//need only new, so history from model ignored.
        panel.setNewMessages(newMessages);
    }
}

