package org.amse.bomberman.client.viewmanager.states.impl;

import java.util.List;
import org.amse.bomberman.client.models.impl.GameMapsModel;
import org.amse.bomberman.client.models.impl.GamesModel;
import org.amse.bomberman.client.models.listeners.GameMapsModelListener;
import org.amse.bomberman.client.models.listeners.GamesModelListener;
import org.amse.bomberman.client.view.wizard.panels.GamesPanel;
import org.amse.bomberman.client.viewmanager.State;
import org.amse.bomberman.client.viewmanager.ViewManager;

/**
 *
 * @author Kirilchuk V.E.
 * @author Mikhail Korovkin
 */
public class CreateJoinViewState extends AbstractState
                                 implements GameMapsModelListener,
                                            GamesModelListener {
    private static final String  BACK = "Disconnect";
    private static final String  NEXT = "Create/Join";

    private final GamesPanel panel = new GamesPanel();


    public CreateJoinViewState(ViewManager machine, State previous) {
        super(machine);
        setPrevious(previous);
        setNext(new CreateJoinWaitState(machine, this));
    }

    @Override
    public void init() {
        getController().getContext().getGameMapsModel().addListener(this);
        getController().getContext().getGamesModel().addListener(this);
        getController().requestGamesList();
        getController().requestGameMapsList();
        getWizard().setPanel(panel);
        getWizard().setBackText(BACK);
        getWizard().setNextText(NEXT);
    }

    @Override
    public void release() {
        getController().getContext().getGameMapsModel().removeListener(this);
        getController().getContext().getGamesModel().removeListener(this);
    }

    @Override
    public void previous() {
        getController().getContext().getConnectionStateModel().reset();
        getController().disconnect();
        machine.setState(previous);
    }

    @Override
    public void next() {
        String state = panel.getState();
        if (state.equals(GamesPanel.CREATE_NAME)) {//CREATE game option
            machine.setState(next);
            String mapName = panel.getMap();
            String gameName = panel.getGameName();
            int maxPlayers = panel.getMaxPlayers();
            getController().requestCreateGame(gameName, mapName, maxPlayers);
        } else {//JOIN game option
            List<String> selectedGame = panel.getSelectedGame();
            int gameNumber = Integer.parseInt(selectedGame.get(0));
            if (gameNumber == -1) {
                String errorMessage = "You did't select the game! "
                        + " Do this and then click join.";
                getWizard().showError(errorMessage);
            } else if (selectedGame.get(selectedGame.size() - 1)
                    .equals(selectedGame.get(selectedGame.size() - 2))) {
                String errorMessage = "Selected game is full!\n"
                        + "Please choose another one or create new.";
                getWizard().showError(errorMessage);
            } else {
                machine.setState(next);
                getController().requestJoinGame(gameNumber);                
            }
        }
    }

    @Override
    public void updateGameMaps() {
        GameMapsModel gameMaps = getController().getContext().getGameMapsModel();
        panel.setMaps(gameMaps.getGameMaps());
    }

    @Override
    public void gameMapsError(String error) {
        getWizard().showError(error);
    }

    @Override
    public void updateGamesList() {
        GamesModel games = getController().getContext().getGamesModel();
        panel.setGames(games.getGames());
    }
}
