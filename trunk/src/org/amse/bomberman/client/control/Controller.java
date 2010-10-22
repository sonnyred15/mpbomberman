package org.amse.bomberman.client.control;

import org.amse.bomberman.util.Direction;
import java.net.InetAddress;
import org.amse.bomberman.client.control.impl.ModelsContainer;

/**
 *
 * @author Mikhail Korovkin
 * @author Kirilchuk V.E.
 */
public interface Controller extends ConnectorListener {

    ModelsContainer getContext();

    void connect(InetAddress serverIP, int serverPort);

    void requestGamesList();

    void requestCreateGame(String gameName,
            String mapName, int maxPlayers);

    void requestLeaveGame();

    void requestJoinGame(int gameID);

    void requestDoMove(Direction dir);

    void requestStartGame();

    void requestGameMap();

    void requestPlantBomb();

    void requestJoinBotIntoGame();

    void requestKickFromGame(int id);

    void requestMapsList();

    void requestIsGameStarted();

    void requestGameInfo();

    void requestSendChatMessage(String message);

    void requestGetNewChatMessages();

    void requestDownloadMap(String gameMapName);

    void requestSetPlayerName(String playerName);

    void disconnect();
}
