package org.amse.bomberman.client.control.impl;

import org.amse.bomberman.client.models.impl.ChatModel;
import org.amse.bomberman.client.models.impl.ClientStateModel;
import org.amse.bomberman.client.models.impl.ConnectionStateModel;
import org.amse.bomberman.client.models.impl.GameMapsModel;
import org.amse.bomberman.client.models.impl.GamesModel;
import org.amse.bomberman.client.models.impl.ResultsModel;
import org.amse.bomberman.client.models.gamemodel.impl.GameModel;
import org.amse.bomberman.client.models.impl.GameInfoModel;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ModelsContainer {
    private final ConnectionStateModel  connectionStateModel
            = new ConnectionStateModel();
    
    private final GameModel     gameModel     = new GameModel();
    private final GameInfoModel gameInfoModel = new GameInfoModel();
    private final ChatModel     chatModel     = new ChatModel();
    private final ResultsModel  resultsModel  = new ResultsModel();
    private final GameMapsModel gameMapsModel = new GameMapsModel();
    private final GamesModel    gamesModel    = new GamesModel();

    private final ClientStateModel stateModel = new ClientStateModel();

    public GameMapsModel getGameMapsModel() {
        return gameMapsModel;
    }

    public GamesModel getGamesModel() {
        return gamesModel;
    }

    public ClientStateModel getClientStateModel() {
        return stateModel;
    }

    public ChatModel getChatModel() {
        return chatModel;
    }

    public ConnectionStateModel getConnectionStateModel() {
        return connectionStateModel;
    }

    public GameModel getGameModel() {
        return gameModel;
    }

    public ResultsModel getResultsModel() {
        return resultsModel;
    }

    public GameInfoModel getGameInfoModel() {
        return gameInfoModel;
    }
}
