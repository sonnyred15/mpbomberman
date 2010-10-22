package org.amse.bomberman.client.control.impl;

import org.amse.bomberman.client.net.Connector;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.util.Direction;
import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.protocol.requests.RequestCreator;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import org.amse.bomberman.client.control.protocolhandlers.ProtocolHandler;

/**
 * Implementation of Controller interface that uses ExecutorService to
 * free gui thread(EDT) as soon as it is possible and to free connector thread
 * as soon as it possible.
 *
 * @author Mikhail Korovkin
 * @author Kirilchuk V.E.
 */
public class ControllerImpl implements Controller {

    private final ExecutorService executors;

    private final ProtocolHandlersFactory commandsFactory
            = new ProtocolHandlersFactory();

    private final Connector       connector;
    private final ModelsContainer context;

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

    public void connect(final InetAddress serverIp, final int serverPort) {
        executors.submit(new Runnable() {

            public void run() {
                try {
                    connector.сonnect(serverIp, serverPort);
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
                    context.getConnectionStateModel().setConnected(false);
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

    public void requestAddBot() {
        sendRequest(protocol.requestJoinBotIntoGame());
    }

    public void requestKick(int id) {
        sendRequest(protocol.requestKickFromGame(id));
    }

    public void requestGameMapsList() {
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

    public void requestDownloadGameMap(String gameMapName) {
        sendRequest(protocol.requestDownloadGameMap(gameMapName));
    }

    public void requestSetClientName(String playerName) {
        sendRequest(protocol.requestSetClientName(playerName));
    }

    public void received(final ProtocolMessage<Integer, String> message) {
        int messageId = message.getMessageId();
        final ProtocolHandler handler = commandsFactory.getCommand(messageId);

        executors.submit(new Runnable() {

            public void run() {
                handler.process(ControllerImpl.this, message.getData());
            }
        });
    }

    private void sendRequest(final ProtocolMessage<Integer, String> message) {
        executors.execute(new Runnable() {

            public void run() {
                try {
                    connector.sendRequest(message);
                } catch (NetException ex) {
                    //TODO log
                    context.getConnectionStateModel().setConnected(false);
                }
            }
        });
    }
}
