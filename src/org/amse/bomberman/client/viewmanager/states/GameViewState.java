package org.amse.bomberman.client.viewmanager.states;

import javax.swing.JOptionPane;
import org.amse.bomberman.client.models.gamemodel.impl.GameModel;
import org.amse.bomberman.client.models.impl.ChatModel;
import org.amse.bomberman.client.models.impl.ResultsModel;
import org.amse.bomberman.client.models.listeners.ChatModelListener;
import org.amse.bomberman.client.models.listeners.GameModelListener;
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
                           implements GameModelListener,
                                      ChatModelListener,
                                      ResultModelListener {
    private GameFrame gameFrame = new GameFrame();
    private GameKeyListener keyListener = new GameKeyListener(getController());
    private GameMenuBar menu = new GameMenuBar(getController());

    private boolean dead = false;
    private boolean isFirstInit = true;

    public GameViewState(ViewManager machine) {
        super(machine);
        getController().getContext().getGameModel().addListener(this);
        gameFrame.setJMenuBar(menu);
        gameFrame.addKeyListener(keyListener);
    }

    public void init() {
        getWizard().setVisible(false);
        gameFrame.setVisible(true);
    }

    public void previous() {
        gameFrame.setVisible(false);
        getWizard().setVisible(true);
        machine.setState(previous);
    }

    public void next() {
        //TODO log
        //do nothing
    }

    public void gameModelChanged() {
        GameModel model = getController().getContext().getGameModel();

//        if (isFirstInit) {//TODO CLIENT bad code
//            int mapSize = model.getMap().getSize();
//            if (mapSize < GamePanel.DEFAULT_RANGE) {
//                width = mapSize * GamePanel.CELL_SIZE + 50 + infoTextWidth;
//                height = mapSize * GamePanel.CELL_SIZE + 160;
//            }
//            isFirstInit = false;//TODO CLIENT bad code
//        }
        gameFrame.setGameMap(model);
        gameFrame.setBonuses(model.getPlayerLives(),
                             model.getPlayerBombs(),
                             model.getPlayerRadius());

        updateResults();
        int lives = model.getPlayerLives();
        if (lives <= 0) {
            if (!dead) {//TODO CLIENT bad code
                dead = true;
                gameFrame.stopGame();
                JOptionPane.showMessageDialog(gameFrame, "You are dead!!!",
                        "Death", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        if (model.isEnded()) {
            if (!dead) { //TODO bad code
                dead = true;
                gameFrame.stopGame();
                JOptionPane.showMessageDialog(gameFrame, "You win!!!",
                        "Congratulations!", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void gameModelErrot(String error) {
        //TODO ignore?
    }

    public void updateChat() {
        ChatModel model = getController().getContext().getChatModel();
        gameFrame.setHistory(model.getHistory());
    }

    public void chatError(String message) {
        //TODO ignore?
    }

    public void updateResults() {
        ResultsModel model = getController().getContext().getResultsModel();
        gameFrame.setResults(model.getResults());
    }

    public void resultsError(String error) {
        //TODO ignore?
    }
}
