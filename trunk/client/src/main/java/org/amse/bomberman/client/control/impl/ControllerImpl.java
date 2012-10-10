package org.amse.bomberman.client.control.impl;

import java.net.ConnectException;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;

import org.amse.bomberman.client.net.GenericConnector;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.protocol.RequestCreator;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.util.Direction;
import org.amse.bomberman.client.control.protocolhandlers.ProtocolHandler;
import org.amse.bomberman.protocol.ProtocolMessage;

/**
 * Implementation of {@link Controller} interface that uses {@link ExecutorService} to
 * free gui thread(EDT) as soon as it is possible and to free connector thread
 * as soon as it is possible.
 *
 * @author Mikhail Korovkin
 * @author Kirilchuk V.E.
 */
public class ControllerImpl implements Controller {

    private final ExecutorService executors;

    private final RequestCreator          protocol  = new RequestCreator();
    private final ProtocolHandlersFactory handlersFactory
            = new ProtocolHandlersFactory();

    private final GenericConnector<ProtocolMessage> connector;

    private final SimpleModelsContainer context;
    
    /**
     * Constructs asynchronous controller.
     *
     * @param executors workers to process requests and responses.
     * @param connector underlaying connector.
     * @param context models container.
     */
    public ControllerImpl(ExecutorService executors,
            GenericConnector<ProtocolMessage> connector, SimpleModelsContainer context) {
        if (executors == null || connector == null || context == null) {
            throw new IllegalArgumentException("Arguments can`t be null.");
        }

        this.executors = executors;
        this.connector = connector;
        this.context   = context;
    }

    @Override
    public SimpleModelsContainer getContext() {
        return context;
    }

    @Override
    public void connect(final InetAddress serverIp, final int serverPort) {
        executors.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    connector.сonnect(serverIp, serverPort);
                    context.getConnectionStateModel().setConnected(true);                    
                } catch (ConnectException ex) {
                    context.getConnectionStateModel().connectException(ex);
                } catch (IllegalArgumentException ex) { //wrong port
                    context.getConnectionStateModel().connectException(ex);
                }
            }
        });
    }

    @Override
    public void disconnect() {
        executors.submit(new Runnable() {

            @Override
            public void run() {
                try {//not using private sendRequest method cause need finally block
                    connector.send(protocol.requestServerDisconnect());
                } catch (NetException ex) {
                    //ignore
                } finally {
                    context.getConnectionStateModel().setConnected(false);
                    connector.closeConnection();
                }
            }
        });
    }

    @Override
    public void requestGamesList() {
        sendRequest(protocol.requestGamesList());
    }

    @Override
    public void requestCreateGame(String gameName, String mapName, int maxPlayers) {
        sendRequest(protocol.requestCreateGame(gameName, mapName, maxPlayers));
    }

    @Override
    public void requestLeaveGame() {
        sendRequest(protocol.requestLeaveGame());
    }

    @Override
    public void requestJoinGame(int gameID) {
        sendRequest(protocol.requestJoinGame(gameID));
    }

    @Override
    public void requestDoMove(Direction dir) {
        sendRequest(protocol.requestDoMove(dir));
    }

    @Override
    public void requestStartGame() {
        sendRequest(protocol.requestStartGame()); 
    }

    @Override
    public void requestGameMap() {
        sendRequest(protocol.requestGameMap());
    }

    @Override
    public void requestPlantBomb() {
        sendRequest(protocol.requestPlaceBomb());
    }

    @Override
    public void requestAddBot() {
        sendRequest(protocol.requestJoinBotIntoGame());
    }

    @Override
    public void requestKick(int id) {
        sendRequest(protocol.requestKickFromGame(id));
    }

    @Override
    public void requestGameMapsList() {
        sendRequest(protocol.requestGameMapsList());
    }

    @Override
    public void requestIsGameStarted() {
        sendRequest(protocol.requestIsGameStarted());
    }

    @Override
    public void requestGameInfo() {
        sendRequest(protocol.requestGameInfo());
    }

    @Override
    public void requestSendChatMessage(String message) {
        sendRequest(protocol.requestAddChatMessage(message));
    }

    @Override
    public void requestGetNewChatMessages() {
        sendRequest(protocol.requestGetNewChatMessages());
    }

    @Override
    public void requestDownloadGameMap(String gameMapName) {
        sendRequest(protocol.requestDownloadGameMap(gameMapName));
    }

    @Override
    public void requestSetClientName(String playerName) {
        sendRequest(protocol.requestSetClientName(playerName));
    }

    @Override
    public void received(final ProtocolMessage message) {
        int messageId = message.getMessageId();
        final ProtocolHandler handler = handlersFactory.getCommand(messageId);
        
        if(handler == null) {
            //TODO CLIENT log this
            return;
        }

        executors.submit(new Runnable() {

            @Override
            public void run() {
                handler.process(ControllerImpl.this, message.getData());
            }
        });
    }

    private void sendRequest(final ProtocolMessage message) {
        executors.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    connector.send(message);
                } catch (NetException ex) {
                    //TODO CLIENT log this
                    context.getConnectionStateModel().setConnected(false);
                }
            }
        });
    }
}
