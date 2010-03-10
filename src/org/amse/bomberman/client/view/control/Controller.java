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
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.net.impl.AsynchroConnector;
import org.amse.bomberman.client.view.control.ifaces.ChatUpdatesListener;
import org.amse.bomberman.client.view.control.ifaces.CreateGameResultListener;
import org.amse.bomberman.client.view.control.ifaces.DoMoveResultListener;
import org.amse.bomberman.client.view.control.ifaces.GameInfoUpdatesListener;
import org.amse.bomberman.client.view.control.ifaces.GameMapUpdatesListener;
import org.amse.bomberman.client.view.control.ifaces.GameMapsListListener;
import org.amse.bomberman.client.view.control.ifaces.GamesListUpdatesListener;
import org.amse.bomberman.client.view.control.ifaces.IsGameStartedListener;
import org.amse.bomberman.client.view.control.ifaces.JoinBotResultListener;
import org.amse.bomberman.client.view.control.ifaces.JoinGameResultListener;
import org.amse.bomberman.client.view.control.ifaces.LeaveGameResultListener;
import org.amse.bomberman.client.view.control.ifaces.PlantBombResultListener;
import org.amse.bomberman.client.view.control.ifaces.TryStartGameResultListeners;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Constants.Direction;
import org.amse.bomberman.util.Creator;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Controller {

    private AsynchroConnector connector = null;
    private ChatUpdatesListener chatUpdatesListener = null;
    private CreateGameResultListener createGameResultListener = null;
    private DoMoveResultListener doMoveResultListener = null;
    private GameInfoUpdatesListener gameInfoUpdatesListener = null;
    private GameMapUpdatesListener gameMapUpdatesListener = null;
    private GameMapsListListener gameMapsListListener = null;
    private GamesListUpdatesListener gamesListUpdatesListener = null;
    private IsGameStartedListener gameStartedListener = null;
    private JoinBotResultListener joinBotResultListener = null;
    private JoinGameResultListener joinGameResultListener = null;
    private LeaveGameResultListener leaveGameResultListener = null;
    private PlantBombResultListener plantBombResultListener = null;
    private TryStartGameResultListeners tryStartGameResultListeners = null;

    public Controller() {
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
        //TODO
    }

    public void requestCreateGame(String gameName, String mapName, int maxPlayers) {
        try {
            this.connector.requestCreateGame(gameName, mapName, maxPlayers);
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateCreateGameResult(String result) {
        //TODO
    }

    public void requestLeaveGame() {
        try {
            this.connector.requestLeaveGame();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateLeaveGameResult() {
        //TODO
    }

    public void requestJoinGame(int gameID) {
        try {
            this.connector.requestJoinGame(gameID);
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateJoinGameResult(String result) {
        //TODO
    }

    public void requestDoMove(Direction dir) {
        try {
            this.connector.requestDoMove(dir);
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateDoMoveResult() {
        //TODO
    }

    public void requestStartGame() {
        try {
            this.connector.requestStartGame();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateStartGameResult() {
        //TODO
    }

    public void requestMap() {
        try {
            this.connector.requestMap();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateMap() {
        //TODO
    }

    public void requestPlantBomb() {
        try {
            this.connector.requestPlantBomb();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updatePlantBombStatus() {
        //TODO
    }

    public void requestJoinBotIntoGame(int gameNumber) {
        try {
            this.connector.requestJoinBotIntoGame(gameNumber);
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateJoinBotResult() {
        //TODO
    }

    public void requestMapsList() {
        try {
            this.connector.requestMapsList();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateMapsList() {
        //TODO
    }

    public void requestIsGameStarted() {
        try {
            this.connector.requestIsGameStarted();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateIsGameStarted() {
        //TODO
    }

    public void requestGameInfo() {
        try {
            this.connector.requestGameInfo();
        } catch (NetException ex) {
            //TODO
        }
    }

    public void updateGameInfo(List<String> gameInfo) {
        //TODO
    }

    public void sendChatMessage(String message) {
        //TODO
    }

    public void getNewChatMessages() {
        //TODO
    }

    public void setChatUpdatesListener(ChatUpdatesListener chatUpdatesListener) {
        this.chatUpdatesListener = chatUpdatesListener;
    }

    public void setCreateGameResultListener(CreateGameResultListener createGameResultListener) {
        this.createGameResultListener = createGameResultListener;
    }

    public void setDoMoveResultListener(DoMoveResultListener doMoveResultListener) {
        this.doMoveResultListener = doMoveResultListener;
    }

    public void setGameInfoUpdatesListener(GameInfoUpdatesListener gameInfoUpdatesListener) {
        this.gameInfoUpdatesListener = gameInfoUpdatesListener;
    }

    public void setGameMapUpdatesListener(GameMapUpdatesListener gameMapUpdatesListener) {
        this.gameMapUpdatesListener = gameMapUpdatesListener;
    }

    public void setGameMapsListListener(GameMapsListListener gameMapsListListener) {
        this.gameMapsListListener = gameMapsListListener;
    }

    public void setGameStartedListener(IsGameStartedListener gameStartedListener) {
        this.gameStartedListener = gameStartedListener;
    }

    public void setGamesListUpdatesListener(GamesListUpdatesListener gamesListUpdatesListener) {
        this.gamesListUpdatesListener = gamesListUpdatesListener;
    }

    public void setJoinBotResultListener(JoinBotResultListener joinBotResultListener) {
        this.joinBotResultListener = joinBotResultListener;
    }

    public void setJoinGameResultListener(JoinGameResultListener joinGameResultListener) {
        this.joinGameResultListener = joinGameResultListener;
    }

    public void setLeaveGameResultListener(LeaveGameResultListener leaveGameResultListener) {
        this.leaveGameResultListener = leaveGameResultListener;
    }

    public void setPlantBombResultListener(PlantBombResultListener plantBombResultListener) {
        this.plantBombResultListener = plantBombResultListener;
    }

    public void setTryStartGameResultListeners(TryStartGameResultListeners tryStartGameResultListeners) {
        this.tryStartGameResultListeners = tryStartGameResultListeners;
    }
}
