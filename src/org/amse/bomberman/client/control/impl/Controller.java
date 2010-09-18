package org.amse.bomberman.client.control.impl;

import org.amse.bomberman.client.net.IConnector;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.net.impl.AsynchroConnector;
import org.amse.bomberman.client.control.IController;
import org.amse.bomberman.client.model.impl.Model;
import org.amse.bomberman.client.net.RequestResultListener;
import org.amse.bomberman.client.view.bomberwizard.BomberWizard;
import org.amse.bomberman.client.view.gamejframe.GameJFrame;
import org.amse.bomberman.client.view.wizard.Wizard;
import org.amse.bomberman.util.Constants.Direction;
import org.amse.bomberman.util.Creator;
import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.protocol.RequestCreator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Mikhail Korovkin
 */
public class Controller implements IController {

    private IConnector connector = null;
    private static IController controller = null;
    private RequestResultListener receiveResultListener;
    private final RequestCreator protocol = new RequestCreator();
    private JFrame gameJFrame = null;

    private Controller() {
        if (connector == null) {
            connector = AsynchroConnector.getInstance();
        }
    }

    public static IController getInstance() {
        if(controller == null) {
            controller = new Controller();
        }
        return controller;
    }

    public void lostConnection(String exception) {
        if(this.receiveResultListener instanceof Wizard) {
            Wizard wizard = (Wizard) this.receiveResultListener;
            System.out.println(exception);
            JOptionPane.showMessageDialog(wizard, exception, "Error",
                                          JOptionPane.ERROR_MESSAGE);

            wizard.setCurrentJPanel(BomberWizard.IDENTIFIER1);
            this.setReceiveInfoListener(receiveResultListener);
            gameJFrame = null;
        } else {
            System.out.println(exception);
            JOptionPane.showMessageDialog(gameJFrame,
                                          exception, "Error",
                                          JOptionPane.ERROR_MESSAGE);
            gameJFrame.dispose();
            gameJFrame = null;
            Model.getInstance().setStart(false);
            Model.getInstance().removeListeners();
            BomberWizard wizard = new BomberWizard();
            this.setReceiveInfoListener(wizard);
            wizard.setCurrentJPanel(BomberWizard.IDENTIFIER1);
        }
    }

    public void startGame() {
        if(receiveResultListener instanceof Wizard) {
            Wizard wizard = (Wizard) receiveResultListener;
            wizard.dispose();
            this.setReceiveInfoListener(
                    (RequestResultListener) Model.getInstance());
            GameJFrame jframe = new GameJFrame();
            Model.getInstance().addListener(jframe);
            gameJFrame = jframe;
            try {
                this.requestGameMap();
            } catch (NetException ex) {
                this.lostConnection(ex.getMessage());
            }
        } else {
            System.out.println("Game is already started or closed.");
        }
    }

    public void leaveGame() {
        if(!(receiveResultListener instanceof Wizard)) {
            gameJFrame.dispose();
            Model.getInstance().removeListeners();
            BomberWizard wizard = new BomberWizard();
            this.setReceiveInfoListener(wizard);
            wizard.setCurrentJPanel(BomberWizard.IDENTIFIER2);
        } else {
            System.out.println("Game is already leaved or closed.");
        }
    }

    public void setReceiveInfoListener(
            RequestResultListener receiveResultListener) {
        this.receiveResultListener = receiveResultListener;
    }

    public void connect(InetAddress serverIP, int serverPort)
            throws UnknownHostException, NumberFormatException, IOException {
        this.connector.сonnect(serverIP, serverPort);
    }

    public void disconnect() {
        this.connector.disconnect();
    }

    public void requestGamesList() throws NetException {
        sendRequest(protocol.requestGamesList());
    }

    public void requestCreateGame(String gameName, String mapName,
                                  int maxPlayers)
            throws NetException {
        sendRequest(protocol.requestCreateGame(gameName,
                                               mapName,
                                               maxPlayers));
    }

    public void requestLeaveGame() throws NetException {
        sendRequest(protocol.requestLeaveGame());
    }

    public void requestJoinGame(int gameID) throws NetException {
        sendRequest(protocol.requestJoinGame(gameID));
    }

    public void requestDoMove(Direction dir) throws NetException {
        sendRequest(protocol.requestDoMove(dir));
    }

    public void requestStartGame() throws NetException {
        sendRequest(protocol.requestStartGame());
    }

    public void requestGameMap() throws NetException {
        sendRequest(protocol.requestGameMap());
    }

    public void requestPlantBomb() throws NetException {
        sendRequest(protocol.requestPlantBomb());
    }

    public void requestJoinBotIntoGame() throws NetException {
        sendRequest(protocol.requestJoinBotIntoGame());
    }

    public void requestRemoveBotFromGame() throws NetException {
        sendRequest(protocol.requestRemoveBotFromGame());
    }

    public void requestMapsList() throws NetException {
        sendRequest(protocol.requestGameMapsList());
    }

    public void requestIsGameStarted() throws NetException {
        sendRequest(protocol.requestIsGameStarted());
    }

    public void requestGameInfo() throws NetException {
        sendRequest(protocol.requestGameInfo());
    }

    public void requestSendChatMessage(String message) throws NetException {
        sendRequest(protocol.requestAddChatMessage(message));
    }

    public void requestNewChatMessages() throws NetException {
        sendRequest(protocol.requestNewChatMessages());
    }

    public void requestDownloadMap(String gameMapName) throws NetException {
        sendRequest(protocol.requestDownloadGameMap(gameMapName));
    }

    public void requestSetPlayerName(String playerName) throws NetException {
        sendRequest(protocol.requestSetPlayerName(playerName));
    }

    public void receivedRequestResult(List<String> requestResult) {
        if(this.receiveResultListener != null) {
            this.receiveResultListener.received(requestResult);
        } else {
            Creator.createErrorDialog(null, "Error", "No listener for "
                    + "received info.");
        }
    }

    private void sendRequest(ProtocolMessage<Integer, String> message) throws
            NetException {
        this.connector.sendRequest(message);
    }

}
