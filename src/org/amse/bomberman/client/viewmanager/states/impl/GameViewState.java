package org.amse.bomberman.client.viewmanager.states.impl;

import java.util.List;
import javax.swing.JOptionPane;
import org.amse.bomberman.client.models.gamemodel.Player;
import org.amse.bomberman.client.models.gamemodel.impl.GameMapModel;
import org.amse.bomberman.client.models.gamemodel.impl.GameStateModel;
import org.amse.bomberman.client.models.gamemodel.impl.PlayerModel;
import org.amse.bomberman.client.models.impl.ChatModel;
import org.amse.bomberman.client.models.impl.ClientStateModel;
import org.amse.bomberman.client.models.impl.ClientStateModel.State;
import org.amse.bomberman.client.models.impl.ResultsModel;
import org.amse.bomberman.client.models.listeners.ChatModelListener;
import org.amse.bomberman.client.models.listeners.ClientStateModelListener;
import org.amse.bomberman.client.models.listeners.GameMapModelListener;
import org.amse.bomberman.client.models.listeners.GameStateListener;
import org.amse.bomberman.client.models.listeners.PlayerModelListener;
import org.amse.bomberman.client.models.listeners.ResultModelListener;
import org.amse.bomberman.client.view.gamejframe.GameFrame;
import org.amse.bomberman.client.view.gamejframe.GameKeyListener;
import org.amse.bomberman.client.view.gamejframe.GameMenuBar;
import org.amse.bomberman.client.viewmanager.ViewManager;

/**
 *
 * @author Kirilchuk V.E.
 */
public class GameViewState extends AbstractState
                           implements GameMapModelListener,
                                      ChatModelListener,
                                      ResultModelListener,
                                      PlayerModelListener,
                                      GameStateListener,
                                      ClientStateModelListener {
    private GameFrame       gameFrame = new GameFrame();
    private GameKeyListener keyListener = new GameKeyListener(getController());
    private GameMenuBar     menu = new GameMenuBar(getController());

    private boolean dead = false;
    private boolean ended = false;

    public GameViewState(ViewManager machine) {
        super(machine);
        gameFrame.setJMenuBar(menu);        
    }

    public void init() {
        getController().getContext().getGameStateModel().reset();
        getController().getContext().getGameMapModel().reset();
        dead  = false;
        ended = false;
        gameFrame.addKeyListener(keyListener);
        getController().getContext().getGameMapModel().addListener(this);
        getController().getContext().getChatModel().addListener(this);
        getController().getContext().getResultsModel().addListener(this);
        getController().getContext().getPlayerModel().addListener(this);
        getController().getContext().getClientStateModel().addListener(this);
        getController().getContext().getGameStateModel().addListener(this);
        getController().requestGameMap();
        gameFrame.setLocationRelativeTo(getWizard());
        getWizard().setVisible(false);
        gameFrame.setVisible(true);
    }

    @Override
    public void release() {
        gameFrame.reset();
        gameFrame.removeKeyListener(keyListener);
        getController().getContext().getGameMapModel().removeListener(this);
        getController().getContext().getChatModel().removeListener(this);
        getController().getContext().getResultsModel().removeListener(this);
        getController().getContext().getPlayerModel().removeListener(this);
        getController().getContext().getClientStateModel().removeListener(this);
        getController().getContext().getGameStateModel().removeListener(this);
        gameFrame.dispose();//or JVM can`t auto shutdown when wizard disposed
        getWizard().setVisible(true);
    }

    public void previous() {
        machine.setState(previous);
    }

    public void next() {
        //TODO log
        //do nothing
    }

    public void gameMapChanged() {
        GameMapModel model = getController().getContext().getGameMapModel();
        gameFrame.setGameMap(model);               
    }

    public void updateResults() {
        ResultsModel model = getController().getContext().getResultsModel();
        gameFrame.setResults(model.getResults());
    }

    public void updateChat(List<String> newMessages) {
        ChatModel model = getController().getContext().getChatModel();
        gameFrame.setHistory(model.getHistory());
    }

    public void updatePlayer() {
        PlayerModel model = getController().getContext().getPlayerModel();
        Player player = model.getPlayer();
        gameFrame.setBonuses(player.getLifes(),
                             player.getBombAmount(),
                             player.getBombRadius());
        gameFrame.setPlayerInfo(model);

        int lives = player.getLifes();
        if (lives <= 0) {
            if (!dead) {//TODO CLIENT bad code
                dead = true;
                stopGame();
                JOptionPane.showMessageDialog(gameFrame, "You are dead!!!",
                        "Death", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void updateGameState() {
        GameStateModel model = getController().getContext().getGameStateModel();
        if (model.isEnded()) {
            if (!ended) { //TODO bad code
                ended = true;
                JOptionPane.showMessageDialog(gameFrame,
                        "Game ended. Now you can take a cup of tea. =)",
                        "End of game.",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void stopGame() {
        gameFrame.removeKeyListener(keyListener);
    }

    public void clientStateChanged() {
        ClientStateModel model = getController().getContext().getClientStateModel();
        State state = model.getState();
        switch (state) {
            case NOT_JOINED: {
                previous();
            }
        }
    }

    public void clientStateError(State state, String error) {
        switch (state) {
            case NOT_JOINED: {//NOT JOINED because error is about going to not joined state.
                JOptionPane.showMessageDialog(gameFrame, error,
                        "Leave error.", JOptionPane.ERROR_MESSAGE);
                break;
            }
            case GAME: {
                JOptionPane.showMessageDialog(gameFrame, error,
                        "Game error.", JOptionPane.ERROR_MESSAGE);
                previous();
                break;
            }
        }
    }
}
