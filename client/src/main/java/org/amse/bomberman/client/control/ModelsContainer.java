package org.amse.bomberman.client.control;

import org.amse.bomberman.client.models.impl.GameMapModel;
import org.amse.bomberman.client.models.impl.GameStateModel;
import org.amse.bomberman.client.models.impl.PlayerModel;
import org.amse.bomberman.client.models.impl.ChatModel;
import org.amse.bomberman.client.models.impl.ClientStateModel;
import org.amse.bomberman.client.models.impl.ConnectionStateModel;
import org.amse.bomberman.client.models.impl.GameInfoModel;
import org.amse.bomberman.client.models.impl.GameMapsModel;
import org.amse.bomberman.client.models.impl.GamesModel;
import org.amse.bomberman.client.models.impl.ResultsModel;

/**
 * Container of models(From MVC). Also can be called 'context'.
 *
 * @author Kirilchuk V.E.
 */
public interface ModelsContainer {

    /**
     * @return chat model.
     */
    ChatModel getChatModel();

    /**
     * @return client state model.
     */
    ClientStateModel getClientStateModel();

    /**
     * @return connection state model.
     */
    ConnectionStateModel getConnectionStateModel();

    /**
     * @return game info model.
     */
    GameInfoModel getGameInfoModel();

    /**
     * @return gameMap model.
     */
    GameMapModel getGameMapModel();

    /**
     * @return gameMaps model.
     */
    GameMapsModel getGameMapsModel();

    /**
     * @return game state model.
     */
    GameStateModel getGameStateModel();

    /**
     * @return games model.
     */
    GamesModel getGamesModel();

    /**
     * @return player model.
     */
    PlayerModel getPlayerModel();

    /**
     * @return results model.
     */
    ResultsModel getResultsModel();

}
