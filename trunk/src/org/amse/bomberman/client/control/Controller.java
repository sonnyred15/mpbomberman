package org.amse.bomberman.client.control;

import java.net.InetAddress;
import org.amse.bomberman.client.net.ConnectorListener;
import org.amse.bomberman.util.Direction;
import org.amse.bomberman.client.control.impl.SimpleModelsContainer;
import org.amse.bomberman.client.models.impl.ConnectionStateModel;

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
     * Returns context which contains models.
     * 
     * @return context which contains models.
     */
    SimpleModelsContainer getContext();

    /**
     * Tryes to connect to  server with specified ip address and port.
     * This method must be asynchronous. If exception occur during connect 
     * it must be delivered to {@link ConnectionStateModel#connectException(java.lang.Exception)}
     *
     * @param serverIP ip adress of server to connect.
     * @param serverPort port of server to connect.
     */
    void connect(InetAddress serverIP, int serverPort);

    /**
     * Requests games list from server.
     * This method must be asynchronous.
     */
    void requestGamesList();

    /**
     * Requests server to create game with specified parameters.
     * This method must be asynchronous.
     *
     * @param gameName name of game to create.
     * @param gameMapName name of gameMap to create.
     * @param maxPlayers max players parameter.
     */
    void requestCreateGame(String gameName, String gameMapName, int maxPlayers);

    /**
     * Requests to leave from game.
     * This method must be asynchronous.
     */
    void requestLeaveGame();

    /**
     * Requests to join game with specified id.
     * This method must be asynchronous.
     *
     * @param gameId id of game to join.
     */
    void requestJoinGame(int gameId);

    /**
     * Requests to make move in some direction.
     * This method must be asynchronous.
     *
     * @param direction direction of move.
     */
    void requestDoMove(Direction direction);

    /**
     * Requests to start game.
     * This method must be asynchronous.
     */
    void requestStartGame();

    /**
     * Requests gameMap of current game.
     * This method must be asynchronous.
     */
    void requestGameMap();

    /**
     * Requests to plant bomb.
     * This method must be asynchronous.
     */
    void requestPlantBomb();

    /**
     * Requests to add bot into current game.
     * This method must be asynchronous.
     */
    void requestAddBot();

    /**
     * Requests to kick player with specified id from game.
     * This method must be asynchronous.
     *
     * @param id id of player to kick.
     */
    void requestKick(int id);

    /**
     * Requests availiable gameMaps list.
     * This method must be asynchronous.
     */
    void requestGameMapsList();

    /**
     * Requests answer on question: "Is current game started or not?"
     * This method must be asynchronous.
     */
    void requestIsGameStarted();

    /**
     * Requests current game info.(Player names, gameMap, gameName and other...)
     * This method must be asynchronous.
     */
    void requestGameInfo();

    /**
     * Requests to add message in some place.
     * This method must be asynchronous.
     *
     * @param message message to add.
     */
    void requestSendChatMessage(String message);

    /**
     * Requests to get new messages from chat.
     * This method must be asynchronous.
     *
     * @deprecated Always return "No new messages" because currently new messages
     * auto deliver to client.
     */
    void requestGetNewChatMessages();

    /**
     * Requests server to send GameMap to client as download.
     * This method must be asynchronous.
     *
     * @param gameMapName name of gameMap to download.
     * @deprecated currently not works.
     */
    void requestDownloadGameMap(String gameMapName);

    /**
     * Requests to set client name on server. Must be first command
     * after connection to server is established and must be called only once.
     * This method must be asynchronous.
     *
     * @param clientName name to set.
     */
    void requestSetClientName(String clientName);

    /**
     * Disconnects from server. It is OK to call this method even
     * if you are not connected.
     * This method must be asynchronous.
     */
    void disconnect();
}
