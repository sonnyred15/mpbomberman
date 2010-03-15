/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.client.view.control;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import javax.swing.JFrame;
import org.amse.bomberman.client.net.IConnector2;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.net.impl.AsynchroConnector;
import org.amse.bomberman.client.view.mywizard.RequestResultListener;
import org.amse.bomberman.util.Constants.Direction;
import org.amse.bomberman.util.Creator;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Controller {

    private IConnector2 connector = null;
    private RequestResultListener receiveResultListener;

    public Controller() {
    }

    public void setReceiveInfoListener(RequestResultListener receiveResultListener) {
        this.receiveResultListener = receiveResultListener;
    }

    @Deprecated
    public void showError(String message) {
        Creator.createErrorDialog(null, "Error", message);
    }

    public void connect(JFrame parent, InetAddress serverIP, int serverPort)
            throws UnknownHostException,
                   NumberFormatException,
                   IOException {

        this.connector = AsynchroConnector.getInstance();
        this.connector.сonnect(serverIP, serverPort);
    }

    public void requestGamesList() {
        try {
            this.connector.requestGamesList();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void requestCreateGame(String gameName, String mapName, int maxPlayers) {
        try {
            this.connector.requestCreateGame(gameName, mapName, maxPlayers);
        } catch (NetException ex) {
            //TODO
        }
    }

    public void requestLeaveGame() {
        try {
            this.connector.requestLeaveGame();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void requestJoinGame(int gameID) {
        try {
            this.connector.requestJoinGame(gameID);
        } catch (NetException ex) {
            //TODO
        }
    }

    public void requestDoMove(Direction dir) {
        try {
            this.connector.requestDoMove(dir);
        } catch (NetException ex) {
            //TODO
        }
    }

    public void requestStartGame() {
        try {
            this.connector.requestStartGame();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void requestGameMap() {
        try {
            this.connector.requestGameMap();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void requestPlantBomb() {
        try {
            this.connector.requestPlantBomb();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void requestJoinBotIntoGame(int gameNumber) {
        try {
            this.connector.requestJoinBotIntoGame(gameNumber);
        } catch (NetException ex) {
            //TODO
        }
    }

    public void requestMapsList() {
        try {
            this.connector.requestGameMapsList();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void requestIsGameStarted() {
        try {
            this.connector.requestIsGameStarted();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void requestGameInfo() {
        try {
            this.connector.requestGameInfo();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void requestSendChatMessage(String message) {
        try {
            this.connector.sendChatMessage(message);
        } catch (NetException ex) {
            //TODO
        }
    }

    public void requestNewChatMessages() {
        try {
            this.connector.requestNewChatMessages();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void requestDownloadMap(String gameMapName) {
        try {
            this.connector.requestDownloadGameMap(gameMapName);
        } catch (NetException ex) {
            //TODO
        }
    }

    public void receivedRequestResult(List<String> requestResult) {
        if (this.receiveResultListener != null) {
            this.receiveResultListener.received(requestResult);
        } else {
            showError("No listener for received info.");
        }
    }
}
