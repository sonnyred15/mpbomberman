package org.amse.bomberman.client.control.impl;

import org.amse.bomberman.client.net.Connector;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.net.impl.AsynchroConnector;
import org.amse.bomberman.client.control.Controller;
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
 * @author Kirilchuk V.E.
 */
public class ControllerImpl implements Controller {

    private Connector connector = null;
    private static Controller controller = null;
    private RequestResultListener responseListener;
    private final RequestCreator protocol = new RequestCreator();
    private JFrame gameJFrame = null;

    private ControllerImpl() {
        if (connector == null) {
            connector = AsynchroConnector.getInstance();
        }
    }

    public static Controller getInstance() {
        if(controller == null) {
            controller = new ControllerImpl();
        }
        return controller;
    }

    public void lostConnection(String exception) {
        if(this.responseListener instanceof Wizard) {
            Wizard wizard = (Wizard) this.responseListener;
            System.out.println(exception);
            JOptionPane.showMessageDialog(wizard, exception, "Error",
                                          JOptionPane.ERROR_MESSAGE);

            wizard.setCurrentJPanel(BomberWizard.IDENTIFIER1);
            this.setReceiveInfoListener(responseListener);
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
        if(responseListener instanceof Wizard) {
            Wizard wizard = (Wizard) responseListener;
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
        if(!(responseListener instanceof Wizard)) {
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
        this.responseListener = receiveResultListener;
    }

    public void connect(InetAddress serverIP, int serverPort)
            throws UnknownHostException, NumberFormatException, IOException {
        this.connector.сonnect(serverIP, serverPort);
    }

    public void disconnect() {
        try {
            this.connector.sendRequest(protocol.requestServerDisconnect());
            this.connector.closeConnection();
        } catch (NetException ex) {
            //ignore
        }
    }

    public void requestGamesList() throws NetException {
        sendRequest(protocol.requestGamesList());
    }

    public void requestCreateGame(String gameName, String mapName,
                                  int maxPlayers) throws NetException {
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
        sendRequest(protocol.requestPlaceBomb());
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
        sendRequest(protocol.requestSetClientName(playerName));
    }

    //TODO REDESIGN PLEASE
    //Actually, controller must know about models that need some result.
    //After receive controller must parse result, and set data to proper model.
    //And concrete model must notify View listener about changes,
    //after that, View must self take info from model. That is the MVC.
    //...Difference between current realization is that listener must not parse
    //response themselfs. They don`t need to know about ProtocolMessage at all!!
    public void receivedRequestResult(ProtocolMessage<Integer, String> response) {
        if(this.responseListener != null) {
            this.responseListener.received(response);
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
