package org.amse.bomberman.client.control.impl;

import org.amse.bomberman.client.models.impl.ChatModel;
import org.amse.bomberman.client.models.impl.ClientStateModel;
import org.amse.bomberman.client.models.impl.ConnectionStateModel;
import org.amse.bomberman.client.models.impl.GameMapsModel;
import org.amse.bomberman.client.models.impl.GamesModel;
import org.amse.bomberman.client.models.impl.ResultsModel;
import org.amse.bomberman.client.models.gamemodel.impl.GameMapModel;
import org.amse.bomberman.client.models.gamemodel.impl.GameStateModel;
import org.amse.bomberman.client.models.gamemodel.impl.PlayerModel;
import org.amse.bomberman.client.models.impl.GameInfoModel;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ModelsContainer {
    private final ConnectionStateModel  connectionStateModel
            = new ConnectionStateModel();

    private final GameMapModel  gameMapModel  = new GameMapModel();
    private final PlayerModel   playerModel   = new PlayerModel();
    private final GameInfoModel gameInfoModel = new GameInfoModel();
    private final ChatModel     chatModel     = new ChatModel();
    private final ResultsModel  resultsModel  = new ResultsModel();
    private final GameMapsModel gameMapsModel = new GameMapsModel();
    private final GamesModel    gamesModel    = new GamesModel();

    private final ClientStateModel clientStateModel = new ClientStateModel();
    private final GameStateModel   gameStateModel   = new GameStateModel();

    public GameMapsModel getGameMapsModel() {
        return gameMapsModel;
    }

    public GamesModel getGamesModel() {
        return gamesModel;
    }

    public ClientStateModel getClientStateModel() {
        return clientStateModel;
    }

    public ChatModel getChatModel() {
        return chatModel;
    }

    public ConnectionStateModel getConnectionStateModel() {
        return connectionStateModel;
    }

    public GameMapModel getGameMapModel() {
        return gameMapModel;
    }

    public ResultsModel getResultsModel() {
        return resultsModel;
    }

    public GameInfoModel getGameInfoModel() {
        return gameInfoModel;
    }

    public PlayerModel getPlayerModel() {
        return playerModel;
    }

    public GameStateModel getGameStateModel() {
        return gameStateModel;
    }
}
