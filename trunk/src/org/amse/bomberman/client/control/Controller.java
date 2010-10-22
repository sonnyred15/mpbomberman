package org.amse.bomberman.client.control;

import org.amse.bomberman.util.Direction;
import java.net.InetAddress;
import org.amse.bomberman.client.control.impl.ModelsContainer;

/**
 * Class that represents controller part of MVC pattern.
 * Controller is corresponds for doing operations that affects models 
 * and corresponds for storing context reference to all models.
 *
 * @author Mikhail Korovkin
 * @author Kirilchuk V.E.
 */
public interface Controller extends ConnectorListener {

    /**
     * @return context which contains models.
     */
    ModelsContainer getContext();

    /**
     * Tryes to connect to  server with specified ip address and port.
     *
     * @param serverIP ip adress of server to connect.
     * @param serverPort port of server to connect.
     */
    void connect(InetAddress serverIP, int serverPort);

    /**
     * Requests games list from server.
     */
    void requestGamesList();

    /**
     * Requests server to create game with specified parameters.
     *
     * @param gameName name of game to create.
     * @param gameMapName name of gameMap to create.
     * @param maxPlayers max players parameter.
     */
    void requestCreateGame(String gameName,
            String gameMapName, int maxPlayers);

    /**
     * Requests to leave from game.
     */
    void requestLeaveGame();

    /**
     * Requests to join game with specified id.
     *
     * @param gameId id of game to join.
     */
    void requestJoinGame(int gameId);

    /**
     * Requests to make move in some direction.
     *
     * @param direction direction of move.
     */
    void requestDoMove(Direction direction);

    /**
     * Requests to start game.
     */
    void requestStartGame();

    /**
     * Requests gameMap of current game.
     */
    void requestGameMap();

    /**
     * Requests to plant bomb.
     */
    void requestPlantBomb();

    /**
     * Requests to add bot into current game.
     */
    void requestAddBot();

    /**
     * Requests to kick player with specified id from game.
     *
     * @param id id of player to kick.
     */
    void requestKick(int id);

    /**
     * Requests availiable gameMaps list.
     */
    void requestGameMapsList();

    /**
     * Requests answer on question: "Is current game started or not?"
     */
    void requestIsGameStarted();

    /**
     * Requests current game info.(Player names, gameMap, gameName and other...)
     */
    void requestGameInfo();

    /**
     * Requests to add message in some place.
     *
     * @param message message to add.
     */
    void requestSendChatMessage(String message);

    /**
     * Requests to get new messages from chat.
     * @deprecated Always return "No new messages" because currently new messages
     * auto deliver to client.
     */
    void requestGetNewChatMessages();

    /**
     * Requests server to send GameMap to client as download.
     *
     * @param gameMapName name of gameMap to download.
     * @deprecated currently not works.
     */
    void requestDownloadGameMap(String gameMapName);

    /**
     * Requests to set client name on server. Must be first command
     * after connection to server is established and must be called only once.
     *
     * @param clientName name to set.
     */
    void requestSetClientName(String clientName);

    /**
     * Disconnects from server. It is OK to call this method even
     * if you are not connected.
     */
    void disconnect();
}
