package org.amse.bomberman.client.control.impl;

import org.amse.bomberman.client.net.Connector;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.client.net.ServerListener;
import org.amse.bomberman.util.Direction;
import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.protocol.requests.RequestCreator;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import org.amse.bomberman.protocol.ProtocolConstants;

/**
 * Implementation of Controller interface that uses ExecutorService to
 * free gui Thread as soon as it is possible and to free connector thread
 * as soon as it possible.
 *
 * @author Mikhail Korovkin
 * @author Kirilchuk V.E.
 */
public class ControllerImpl implements Controller {

    private final ExecutorService executors;

    private final Connector       connector;
    private final ModelsContainer context;

    //TODO CLIENT must not have listeners. Must set info to models himself!!!
    private final List<ServerListener> listeners = new CopyOnWriteArrayList<ServerListener>();
    private final RequestCreator       protocol  = new RequestCreator();

    public ControllerImpl(ExecutorService executors,
            Connector connector, ModelsContainer context) {
        this.executors = executors;
        this.connector = connector;
        this.context   = context;
    }

    public ModelsContainer getContext() {
        return context;
    }

    public void connect(final InetAddress serverIP, final int serverPort) {
        executors.submit(new Runnable() {

            public void run() {
                try {
                    connector.сonnect(serverIP, serverPort);
                    context.getConnectionStateModel().setConnected(true);                    
                } catch (IOException ex) {
                    context.getConnectionStateModel().connectException(ex);
                } catch (IllegalArgumentException ex) { //wrong port
                    context.getConnectionStateModel().connectException(ex);
                }
            }
        });
    }

    public void disconnect() {
        executors.submit(new Runnable() {

            public void run() {
                try {//not using private sendRequest method cause need finally block
                    connector.sendRequest(protocol.requestServerDisconnect());
                } catch (NetException ex) {
                    //ignore
                } finally {
                    connector.closeConnection();
                }
            }
        });
    }

    public void requestGamesList() {
        sendRequest(protocol.requestGamesList());
    }

    public void requestCreateGame(String gameName, String mapName, int maxPlayers) {
        sendRequest(protocol.requestCreateGame(gameName, mapName, maxPlayers));
    }

    public void requestLeaveGame() {
        sendRequest(protocol.requestLeaveGame());
    }

    public void requestJoinGame(int gameID) {
        sendRequest(protocol.requestJoinGame(gameID));
    }

    public void requestDoMove(Direction dir) {
        sendRequest(protocol.requestDoMove(dir));
    }

    public void requestStartGame() {
        sendRequest(protocol.requestStartGame()); 
    }

    public void requestGameMap() {
        sendRequest(protocol.requestGameMap());
    }

    public void requestPlantBomb() {
        sendRequest(protocol.requestPlaceBomb());
    }

    public void requestJoinBotIntoGame() {
        sendRequest(protocol.requestJoinBotIntoGame());
    }

    public void requestRemoveBotFromGame() {
        sendRequest(protocol.requestRemoveBotFromGame());
    }

    public void requestMapsList() {
        sendRequest(protocol.requestGameMapsList());
    }

    public void requestIsGameStarted() {
        sendRequest(protocol.requestIsGameStarted());
    }

    public void requestGameInfo() {
        sendRequest(protocol.requestGameInfo());
    }

    public void requestSendChatMessage(String message) {
        sendRequest(protocol.requestAddChatMessage(message));
    }

    public void requestGetNewChatMessages() {
        sendRequest(protocol.requestGetNewChatMessages());
    }

    public void requestDownloadMap(String gameMapName) {
        sendRequest(protocol.requestDownloadGameMap(gameMapName));
    }

    public void requestSetPlayerName(String playerName) {
        sendRequest(protocol.requestSetClientName(playerName));
    }

    public void received(final ProtocolMessage<Integer, String> message) {
        int messageId = message.getMessageId();

        ///////THIS MUST NOT EXIST!!!///////
        if (messageId == ProtocolConstants.GAMES_LIST_NOTIFY_ID) {
            requestGamesList();
            return;
        } else if (messageId == ProtocolConstants.GAME_INFO_NOTIFY_ID) {
            requestGameInfo();
            return;
        } else if (messageId == ProtocolConstants.GAME_FIELD_CHANGED_NOTIFY_ID) {
            requestGameMap();
            return;
        }

        executors.submit(new Runnable() {

            public void run() {
                fireMessageReceived(message);
            }
        });
    }

    public void addServiceListener(ServerListener listener) {
        listeners.add(listener);
    }

    public void removeServiceListener(ServerListener listener) {
        listeners.remove(listener);
    }

    private void fireMessageReceived(ProtocolMessage<Integer, String> message) {
        for (ServerListener listener : listeners) {
            listener.received(message);
        }
    }

    private void sendRequest(final ProtocolMessage<Integer, String> message) {
        executors.execute(new Runnable() {

            public void run() {
                try {
                    connector.sendRequest(message);
                } catch (NetException ex) {
                    ex.printStackTrace();
                    context.getConnectionStateModel().setConnected(false);
                }
            }
        });
    }
}
