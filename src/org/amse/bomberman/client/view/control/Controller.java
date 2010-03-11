/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.client.view.control;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import org.amse.bomberman.client.model.impl.Model;
import org.amse.bomberman.client.net.IConnector2;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.net.impl.AsynchroConnector;
//import org.amse.bomberman.client.view.control.ifaces.ChatUpdatesListener;
//import org.amse.bomberman.client.view.control.ifaces.CreateGameResultListener;
//import org.amse.bomberman.client.view.control.ifaces.DoMoveResultListener;
//import org.amse.bomberman.client.view.control.ifaces.GameInfoUpdatesListener;
//import org.amse.bomberman.client.view.control.ifaces.GameMapUpdatesListener;
//import org.amse.bomberman.client.view.control.ifaces.GameMapsListListener;
//import org.amse.bomberman.client.view.control.ifaces.GamesListUpdatesListener;
//import org.amse.bomberman.client.view.control.ifaces.IsGameStartedListener;
//import org.amse.bomberman.client.view.control.ifaces.JoinBotResultListener;
//import org.amse.bomberman.client.view.control.ifaces.JoinGameResultListener;
//import org.amse.bomberman.client.view.control.ifaces.LeaveGameResultListener;
//import org.amse.bomberman.client.view.control.ifaces.PlantBombResultListener;
//import org.amse.bomberman.client.view.control.ifaces.TryStartGameResultListeners;
import org.amse.bomberman.util.Constants.Direction;
import org.amse.bomberman.util.Creator;
import org.amse.bomberman.util.impl.Parser;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Controller {

    private IConnector2 connector = null;
//    private ChatUpdatesListener chatUpdatesListener = null;
//    private CreateGameResultListener createGameResultListener = null;
//    private DoMoveResultListener doMoveResultListener = null;
//    private GameInfoUpdatesListener gameInfoUpdatesListener = null;
//    private GameMapUpdatesListener gameMapUpdatesListener = null;
//    private GameMapsListListener gameMapsListListener = null;
//    private GamesListUpdatesListener gamesListUpdatesListener = null;
//    private IsGameStartedListener gameStartedListener = null;
//    private JoinBotResultListener joinBotResultListener = null;
//    private JoinGameResultListener joinGameResultListener = null;
//    private LeaveGameResultListener leaveGameResultListener = null;
//    private PlantBombResultListener plantBombResultListener = null;
//    private TryStartGameResultListeners tryStartGameResultListeners = null;

    public Controller() {
    }

    @Deprecated
    public void showError(String message) {
        Creator.createErrorDialog(null, "Error", message);
    }

    public void connect(JFrame parent, InetAddress serverIP, int serverPort)
            throws UnknownHostException,
                   NumberFormatException,
                   IOException {

        this.connector = new AsynchroConnector(this);
        this.connector.сonnect(serverIP, serverPort);
    }

    public void requestGamesList() {
        try {
            this.connector.requestGamesList();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateGamesList(List<String> gamesList) {
        String line = gamesList.get(0);
        if (line.startsWith("No unstarted")) {
            Model.getInstance().setGamesList(new ArrayList<String>(0));
        } else {
            Model.getInstance().setGameMapsList(gamesList);
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
        if (!result.startsWith("Game created")) {
            showError(result);
        } else {
            Model.getInstance().setCreated(true);
        }
    }

    public void requestLeaveGame() {
        try {
            this.connector.requestLeaveGame();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateLeaveGameResult(String result) {
        if (!result.startsWith("Disconnected")) {
            showError(result);
        } else {
            Model.getInstance().setLeavedGame(true);
        }
    }

    public void requestJoinGame(int gameID) {
        try {
            this.connector.requestJoinGame(gameID);
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateJoinGameResult(String result) {
        if (!result.startsWith("Joined")) {
            showError(result);
        } else {
            Model.getInstance().setJoined(true);
        }
    }

    public void requestDoMove(Direction dir) {
        try {
            this.connector.requestDoMove(dir);
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateDoMoveResult(String result) {
        if (!(result.startsWith("true") || result.startsWith("false"))) {
            showError(result);
        } else {
            ;//do nothing
        }
    }

    public void requestStartGame() {
        try {
            this.connector.requestStartGame();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateStartGameResult(String result) {
        if (!result.startsWith("Game started")) {
            showError(result);
        } else {
            Model.getInstance().setStarted(true);
        }
    }

    public void requestGameMap() {
        try {
            this.connector.requestGameMap();
        } catch (NetException ex) {
            //TODO
        }
    }

    /**
     *
     * @param gameMap Game map array + explosions + player info
     */
    public void updateGameMap(List<String> gameMap) {
        String line = gameMap.get(0);
        if (!line.startsWith("Not joined")) {
            Parser parser = new Parser();
            Model.getInstance().setMap(parser.parse(gameMap));
        } else {
            showError(line);
        }
    }

    public void requestPlantBomb() {
        try {
            this.connector.requestPlantBomb();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updatePlantBombResult(String result) {
        if (!result.startsWith("Ok")) {
            showError(result);
        } else {
            ;//do nothing
        }
    }

    public void requestJoinBotIntoGame(int gameNumber) {
        try {
            this.connector.requestJoinBotIntoGame(gameNumber);
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateJoinBotResult(String result) {
        //TODO
    }

    public void requestMapsList() {
        try {
            this.connector.requestGameMapsList();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateMapsList(List<String> gameMapsList) {
        String line = gameMapsList.get(0);
        if (!line.startsWith("No maps")) {
            Model.getInstance().setGameMapsList(gameMapsList);
        } else {
            showError(line);
        }
    }

    public void requestIsGameStarted() {
        try {
            this.connector.requestIsGameStarted();
        } catch (NetException ex) {
            //TODO
        }
    }

    /**
     *
     * @param result is game started or not. "true" if started, "false" - not.
     */
    public void updateIsGameStarted(String result) {
        if (!result.startsWith("Not joined")) {
            if (result.equals("true")) {
                Model.getInstance().setStarted(true);
            } else {
                Model.getInstance().setStarted(false);
            }
        }
    }

    public void requestGameInfo() {
        try {
            this.connector.requestGameInfo();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateGameInfo(List<String> gameInfo) {
        String line = gameInfo.get(0);
        if (!line.startsWith("Not joined")) {
            Model.getInstance().setGameInfo(gameInfo);
        } else {
            showError(line);
        }
    }

    public void requestSendChatMessage(String message) {
        try {
            this.connector.sendChatMessage(message);
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateSendMessageResult(String result) {
        //TODO
    }

    public void requestNewChatMessages() {
        try {
            this.connector.requestNewChatMessages();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateNewChatMessages(List<String> messages) {
        //TODO
    }

    public void requestDownloadMap(String gameMapName) {
        try {
            this.connector.requestDownloadGameMap(gameMapName);
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateDownloadGameMap(List<String> message) {
        String line = message.get(0);
        if (!(line.startsWith("No") || line.startsWith("Error") || line.startsWith("Wrong"))) {
            Model.getInstance().downloadedGameMap(message);
        } else {
            showError(line);
        }
    }

//    public void setChatUpdatesListener(ChatUpdatesListener chatUpdatesListener) {
//        this.chatUpdatesListener = chatUpdatesListener;
//    }
//
//    public void setCreateGameResultListener(CreateGameResultListener createGameResultListener) {
//        this.createGameResultListener = createGameResultListener;
//    }
//
//    public void setDoMoveResultListener(DoMoveResultListener doMoveResultListener) {
//        this.doMoveResultListener = doMoveResultListener;
//    }
//
//    public void setGameInfoUpdatesListener(GameInfoUpdatesListener gameInfoUpdatesListener) {
//        this.gameInfoUpdatesListener = gameInfoUpdatesListener;
//    }
//
//    public void setGameMapUpdatesListener(GameMapUpdatesListener gameMapUpdatesListener) {
//        this.gameMapUpdatesListener = gameMapUpdatesListener;
//    }
//
//    public void setGameMapsListListener(GameMapsListListener gameMapsListListener) {
//        this.gameMapsListListener = gameMapsListListener;
//    }
//
//    public void setGameStartedListener(IsGameStartedListener gameStartedListener) {
//        this.gameStartedListener = gameStartedListener;
//    }
//
//    public void setGamesListUpdatesListener(GamesListUpdatesListener gamesListUpdatesListener) {
//        this.gamesListUpdatesListener = gamesListUpdatesListener;
//    }
//
//    public void setJoinBotResultListener(JoinBotResultListener joinBotResultListener) {
//        this.joinBotResultListener = joinBotResultListener;
//    }
//
//    public void setJoinGameResultListener(JoinGameResultListener joinGameResultListener) {
//        this.joinGameResultListener = joinGameResultListener;
//    }
//
//    public void setLeaveGameResultListener(LeaveGameResultListener leaveGameResultListener) {
//        this.leaveGameResultListener = leaveGameResultListener;
//    }
//
//    public void setPlantBombResultListener(PlantBombResultListener plantBombResultListener) {
//        this.plantBombResultListener = plantBombResultListener;
//    }
//
//    public void setTryStartGameResultListeners(TryStartGameResultListeners tryStartGameResultListeners) {
//        this.tryStartGameResultListeners = tryStartGameResultListeners;
//    }
}
