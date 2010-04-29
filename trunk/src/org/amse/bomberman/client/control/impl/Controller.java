package org.amse.bomberman.client.control.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import org.amse.bomberman.client.net.IConnector;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.net.impl.AsynchroConnector;
import org.amse.bomberman.client.net.impl.SynchroConnector;
import org.amse.bomberman.client.control.IController;
import org.amse.bomberman.client.net.RequestResultListener;
import org.amse.bomberman.util.Constants.Direction;
import org.amse.bomberman.util.Creator;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Controller implements IController{

    private IConnector connector = null;
    private static IController controller = null;
    private boolean isAsynchro;
    private RequestResultListener receiveResultListener;

    private Controller(boolean isAsynchro) {
        this.isAsynchro = isAsynchro;
        if (connector == null) {
            if (isAsynchro) {
                connector = AsynchroConnector.getInstance();
            } else {
                connector = SynchroConnector.getInstance();
            }
        }
    }
    public static IController getInstance() {
        if (controller == null) {
            controller = (IController)new Controller(true);
        }
        return controller;
    }

    public void setReceiveInfoListener(RequestResultListener receiveResultListener) {
        this.receiveResultListener = receiveResultListener;
    }

    public RequestResultListener getReceiveInfoListener() {
        return receiveResultListener;
    }
    @Deprecated
    public void showError(String message) {
        Creator.createErrorDialog(null, "Error", message);
    }

    public void connect(InetAddress serverIP, int serverPort)
            throws UnknownHostException,NumberFormatException,IOException {
        this.connector.сonnect(serverIP, serverPort);
    }

    public void disconnect() {
        this.connector.disconnect();
    }

    public void requestGamesList() throws NetException {
        this.connector.requestGamesList();
    }

    public void requestCreateGame(String gameName, String mapName, int maxPlayers)
            throws NetException {
        this.connector.requestCreateGame(gameName, mapName, maxPlayers);
    }

    public void requestLeaveGame() throws NetException {
        this.connector.requestLeaveGame();
    }

    public void requestJoinGame(int gameID) throws NetException {
        this.connector.requestJoinGame(gameID);
    }

    public void requestDoMove(Direction dir) throws NetException {
        this.connector.requestDoMove(dir);
    }

    public void requestStartGame() throws NetException {
        this.connector.requestStartGame();
        /*if (!isAsynchro) {
            ((SynchroConnector)connector).beginGameUpdating();
        }*/
    }

    public void requestGameMap() throws NetException {
        this.connector.requestGameMap();
    }

    public void requestPlantBomb() throws NetException {
        this.connector.requestPlantBomb();
    }

    public void requestJoinBotIntoGame() throws NetException {
        this.connector.requestJoinBotIntoGame();
    }
    public void requestRemoveBotFromGame() throws NetException {
        this.connector.requestRemoveBotFromGame();
    }

    public void requestMapsList() throws NetException {
        this.connector.requestGameMapsList();
    }

    public void requestIsGameStarted() throws NetException {
        this.connector.requestIsGameStarted();
    }

    public void requestGameInfo() throws NetException {
        this.connector.requestGameInfo();
    }

    public void requestSendChatMessage(String message) throws NetException {
        this.connector.sendChatMessage(message);
    }

    public void requestNewChatMessages() throws NetException {
        this.connector.requestNewChatMessages();
    }

    public void requestDownloadMap(String gameMapName) throws NetException {
        this.connector.requestDownloadGameMap(gameMapName);
    }

    public void receivedRequestResult(List<String> requestResult) {
        if (this.receiveResultListener != null) {
            this.receiveResultListener.received(requestResult);
        } else {
            showError("No listener for received info.");
        }
    }
}
