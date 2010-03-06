/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.client.view.asynchronous;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import javax.swing.JFrame;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.net.impl.AsynchroConnector;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Constants.Direction;
import org.amse.bomberman.util.Creator;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Controller {

    private AsynchroConnector connector = null;
    private ServerInfoJFrame serverInfoJFrame = null;
    private StartJFrame connectionFrame = null;
    private CreatingGameJDialog createGameFrame = null;
    private GameInfoJFrame gameInfoFrame = null;

    public Controller() {
    }

    public void showConnectionFrame() {
        if (this.connectionFrame == null) {
            this.connectionFrame = new StartJFrame(this);
        }
        this.connectionFrame.setVisible(true);
    }

    public void showServerInfoFrame() {
        if (this.serverInfoJFrame == null) {
            this.serverInfoJFrame = new ServerInfoJFrame(this);
        }
        this.serverInfoJFrame.setVisible(true);
    }

    public void showCreateGameFrame() {
        if (this.createGameFrame == null) {
            this.createGameFrame = new CreatingGameJDialog(serverInfoJFrame, this);
        }
        this.createGameFrame.setVisible(true);
    }

    public void showGameInfoFrame() {
        if (this.gameInfoFrame == null) {
            this.gameInfoFrame = new GameInfoJFrame(this);
        }
        this.gameInfoFrame.setVisible(true);
    }

    public void connect(JFrame parent, InetAddress serverIP, int serverPort)
            throws UnknownHostException,
                   NumberFormatException,
                   IOException {

        this.connector = new AsynchroConnector(this);
        this.connector.сonnect(serverIP, serverPort);

        parent.setVisible(false);
        this.showServerInfoFrame();
        this.requestGamesList();
    }

    public void requestGamesList() {
        try {
            this.connector.requestGamesList();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateGamesList(List<String> gamesList) {
        if (this.serverInfoJFrame != null) {
            this.serverInfoJFrame.refreshTable(gamesList);
        }
    }

    public void requestCreateGame(String gameName, String mapName, int maxPlayers) {
        try {
            this.connector.requestCreateGame(gameName, mapName, maxPlayers);
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateCreateGameResult(String result) {
        if (result.startsWith("Created game")) {
            this.serverInfoJFrame.setVisible(false);
            this.createGameFrame.setVisible(false);
            this.showGameInfoFrame();
            this.requestGameInfo();
        } else if (result.startsWith("No such map on server") || result.startsWith("Error on server side")) {
            Creator.createErrorDialog(gameInfoFrame, result, result);
        } else if (result.startsWith("Wrong")) {
        }
    }

    public void requestLeaveGame() {
        try {
            this.connector.requestLeaveGame();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateLeaveGameResult() {
    }

    public void requestJoinGame(int gameID) {
        try {
            this.connector.requestJoinGame(gameID);
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateJoinGameResult(String result) {
        if (result.startsWith("Joined")) {
            this.serverInfoJFrame.setVisible(false);
            this.showGameInfoFrame();
            this.requestGameInfo();
        } else if (result.startsWith("Leave another game first") ||
                result.startsWith("Game is full") ||
                result.startsWith("Game was already started") ||
                result.startsWith("No such game")) {
            Creator.createErrorDialog(serverInfoJFrame, "Join game error.", result);
        } else {
            Creator.createErrorDialog(serverInfoJFrame, "Join query error.", result);
        }

    }

    public void requestDoMove(Direction dir) {
        try {
            this.connector.requestDoMove(dir);
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateDoMoveResult() {
    }

    public void requestStartGame() {
        try {
            this.connector.requestStartGame();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateStartGameResult() {
    }

    public void requestMap() {
        try {
            this.connector.requestMap();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateMap() {
    }

    public void requestPlantBomb() {
        try {
            this.connector.requestPlantBomb();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updatePlantBombStatus() {
    }

    public void requestJoinBotIntoGame(int gameNumber) {
        try {
            this.connector.requestJoinBotIntoGame(gameNumber);
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateJoinBotResult() {
    }

    public void requestMapsList() {
        try {
            this.connector.requestMapsList();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateMapsList() {
    }

    public void requestIsGameStarted() {
        try {
            this.connector.requestIsGameStarted();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateIsGameStarted() {
    }

    public void requestGameInfo() {
        try {
            this.connector.requestGameInfo();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateGameInfo(List<String> gameInfo) {
        if(this.gameInfoFrame != null){
            this.gameInfoFrame.updateInfo(gameInfo);
        }
    }

    public void sendChatMessage(String message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void getNewChatMessages() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
