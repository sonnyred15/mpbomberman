package org.amse.bomberman.client.control;

import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.net.RequestResultListener;
import org.amse.bomberman.util.Direction;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import org.amse.bomberman.protocol.ProtocolMessage;

/**
 *
 * @author Mikhail Korovkin
 */
public interface Controller {

    public void setReceiveInfoListener(RequestResultListener receiveResultListener);

    public void connect(InetAddress serverIP, int serverPort)
            throws NetException, IOException;

    public void requestGamesList() throws NetException;

    public void requestCreateGame(String gameName, String mapName, int maxPlayers)
            throws NetException;

    public void requestLeaveGame() throws NetException;

    public void requestJoinGame(int gameID) throws NetException;

    public void requestDoMove(Direction dir) throws NetException;

    public void requestStartGame() throws NetException;

    public void requestGameMap() throws NetException;

    public void requestPlantBomb() throws NetException;

    public void requestJoinBotIntoGame() throws NetException;

    public void requestRemoveBotFromGame() throws NetException;

    public void requestMapsList() throws NetException;

    public void requestIsGameStarted() throws NetException;

    public void requestGameInfo() throws NetException;

    public void requestSendChatMessage(String message) throws NetException;

    public void requestGetNewChatMessages() throws NetException;

    public void requestDownloadMap(String gameMapName) throws NetException;

    public void requestSetPlayerName(String playerName) throws NetException;

    public void startGame();

    public void leaveGame();

    public void receivedResponse(ProtocolMessage<Integer, String> requestResult);

    public void lostConnection(String message);

    public void disconnect();
}
