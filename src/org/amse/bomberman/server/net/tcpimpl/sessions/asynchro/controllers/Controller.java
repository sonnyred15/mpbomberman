package org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers;

import java.util.Iterator;
import java.util.List;

import org.amse.bomberman.server.gameservice.impl.NetGamePlayer;
import org.amse.bomberman.server.gameservice.impl.Game;
import org.amse.bomberman.server.net.Session;
import org.amse.bomberman.util.Direction;
import org.amse.bomberman.protocol.impl.responses.ResponseCreator;
import org.amse.bomberman.server.net.tcpimpl.sessions.control.RequestExecutor;
import org.amse.bomberman.protocol.impl.ProtocolConstants;
import org.amse.bomberman.protocol.InvalidDataException;
import org.amse.bomberman.protocol.impl.ProtocolMessage;
import org.amse.bomberman.server.gameservice.GameStorageListener;
import org.amse.bomberman.server.net.SessionEndListener;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates.ClientState;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates.NotJoinedState;

/**
 * Class that represents net controller of ingame player.
 * @author Kirilchuk V.E.
 */
public class Controller implements RequestExecutor, SessionEndListener, GameStorageListener {

    private final ResponseCreator protocol;
    private final NetGamePlayer player = new NetGamePlayer(this);
    //
    private volatile ClientState state = new NotJoinedState(this);
    //
    private final Session session;
    
    /**
     * Constructor of controller.
     * @param sessionServer server of session that owns this controller.
     * @param session owner of this controller.
     */
    public Controller(Session session, ResponseCreator protocol) {
        this.session = session;
        this.protocol = protocol;
    }

    @Override
    public void sendGames() {
        List<Game> games = this.session.getServiceContext().getGameStorage().getGamesList();
        sendToClient(protocol.unstartedGamesList(games));
    }

    @Override
    public void tryCreateGame(List<String> args) throws InvalidDataException {// gameName mapName maxPlayers
        checkArgsNum(args, 3, ProtocolConstants.CREATE_GAME_MESSAGE_ID,
                "Wrong number of arguments.");

        //here all is OK
        Iterator<String> iterator = args.iterator();
        String gameName = iterator.next();
        String gameMapName = iterator.next();

        int maxPlayers = -1;
        try {
            maxPlayers = Integer.parseInt(iterator.next());
        } catch (NumberFormatException ex) {
            System.out.println("Session: createGame warning. Client tryed to create game, canceled. "
                    + "Wrong command parameters. Error on client side. "
                    + ex.getMessage());
            throw new InvalidDataException(ProtocolConstants.CREATE_GAME_MESSAGE_ID,
                                           "Max players param must be int");
        }

        sendToClient(state.createGame(gameMapName, gameName, maxPlayers));
    }

    @Override
    public void tryJoinGame(List<String> args) throws InvalidDataException {//gameID
        checkArgsNum(args, 1, ProtocolConstants.JOIN_GAME_MESSAGE_ID,
                "Wrong number of arguments.");

        //here all is OK
        int gameID = 0;
        Iterator<String> iterator = args.iterator();
        try {
            gameID = Integer.parseInt(iterator.next());
        } catch (NumberFormatException ex) {
            System.out.println("Session: joinGame error. "
                    + "Wrong command parameters. "
                    + "Error on client side. gameID must be int. "
                    + ex.getMessage());
            throw new InvalidDataException(ProtocolConstants.JOIN_GAME_MESSAGE_ID,
                                           "Game id param must be int.");
        }

        sendToClient(state.joinGame(gameID));
    }

    @Override
    public void tryDoMove(List<String> args) throws InvalidDataException {
        checkArgsNum(args, 1, ProtocolConstants.DO_MOVE_MESSAGE_ID,
                "Wrong number of arguments.");

        Iterator<String> iterator = args.iterator();
        Direction direction = null;
        try {
            int dir = Integer.parseInt(iterator.next());    // throws NumberFormatException
            direction = Direction.fromInt(dir);    // throws IllegalArgumentException
        } catch (NumberFormatException ex) {
            System.out.println("Session: doMove error. "
                    + "Unsupported direction(not int). ");

            throw new InvalidDataException(ProtocolConstants.DO_MOVE_MESSAGE_ID,
                                           "Direction of move must be integer.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Session: doMove error. "
                    + "Unsupported direction. ");

            throw new InvalidDataException(ProtocolConstants.DO_MOVE_MESSAGE_ID,
                                           "Unsupported direction value.");
        }

        sendToClient(state.doMove(direction));
    }

    @Override
    public void sendGameMapInfo() {
        sendToClient(state.getGameMapInfo());
    }

    @Override
    public void tryStartGame() {
        sendToClient(state.startGame());
    }

    @Override
    public void tryLeave() {
        sendToClient(state.leave());
    }

    @Override
    public void tryPlaceBomb() {
        sendToClient(state.placeBomb());
    }

    @Override
    public void sendDownloadingGameMap(List<String> args) throws
            InvalidDataException {
        checkArgsNum(args, 1, ProtocolConstants.DOWNLOAD_GAME_MAP_MESSAGE_ID,
                "Wrong number of arguments.");

        Iterator<String> iterator = args.iterator();
        String gameMapName = iterator.next();

        sendToClient(protocol.downloadGameMap(gameMapName));
    }

    @Override
    public void sendGameStatus() {
        System.out.println("Session: sended game status to client.");
        sendToClient(state.getGameStatus());
    }

    @Override
    public void sendGameMapsList() {
        sendToClient(protocol.gameMapsList());//TODO converter must do it
    }

    @Override
    public void tryAddBot(List<String> args) throws InvalidDataException {
        checkArgsNum(args, 1, ProtocolConstants.BOT_ADD_MESSAGE_ID,
                "Wrong number of arguments.");

        Iterator<String> iterator = args.iterator();
        String botName = iterator.next(); //TODO what`s about incorrect name. Ex.: null or empty!?

        sendToClient(state.addBot(botName));
    }

    @Override
    public void sendGameInfo() {
        System.out.println("Session: sended gameInfo to client.");
        sendToClient(state.getGameInfo());
    }

    @Override
    public void addMessageToChat(List<String> args) throws InvalidDataException {
        checkArgsNum(args, 1, ProtocolConstants.CHAT_ADD_RESULT_MESSAGE_ID,
                "Wrong number of arguments.");

        sendToClient(state.addMessageToChat(args.get(0)));
    }

    @Override
    public void sendNewMessagesFromChat() {
        sendToClient(state.getNewMessagesFromChat());
    }

    @Override
    public void tryKickPlayer(List<String> args) throws InvalidDataException {
        checkArgsNum(args, 1, ProtocolConstants.KICK_PLAYER_MESSAGE_ID,
                "Wrong number of arguments.");
        
        int playerId;
        try {
            playerId = Integer.parseInt(args.get(0));  
        } catch (NumberFormatException ex) {
            System.out.println("Session: doMove error. "
                    + "Unsupported direction(not int). ");

            throw new InvalidDataException(ProtocolConstants.DO_MOVE_MESSAGE_ID,
                                           "Player to kick id must be integer.");
        }

        sendToClient(state.kickPlayer(playerId));
    }

    @Override
    public void sendGamePlayersStats() {        
        System.out.println("Session: sended gamePlayersStats to client.");
        sendToClient(state.getGamePlayersStats());
    }

    @Override
    public void setClientNickName(List<String> args) throws InvalidDataException {// "17 name"
        checkArgsNum(args, 1, ProtocolConstants.SET_NAME_MESSAGE_ID,
                "Wrong number of arguments.");

        Iterator<String> iterator = args.iterator();
        String name = iterator.next();
        this.player.setNickName(name);
        sendToClient(protocol.ok(ProtocolConstants.SET_NAME_MESSAGE_ID, name));
    }

    @Override
    public void gamesChanged() {
        sendToClient(protocol.ok(ProtocolConstants.GAMES_LIST_NOTIFY_ID,
                ProtocolConstants.UPDATE_GAMES_LIST));
    }

    public void sendToClient(ProtocolMessage message) {
        this.session.send(message);
    }

    public Session getSession() {
        return session;
    }

    public ClientState getState() {
        return state;
    }

    public void setState(ClientState state) {//don`t need synchronized cause not need atomicity and state is volatile
        this.state = state;
    }

    public NetGamePlayer getGamePlayer() {
        return player;
    }

    @Override
    public void sessionTerminated(Session endedSession) {
        if(!(this.state.getClass() == NotJoinedState.class)) {
            this.state.leave();
        }
    }

    private void checkArgsNum(List<String> args, int expected,
                              int errorMessageId, String errorMessage)
                                                  throws InvalidDataException {
        if(args.size() != expected) {
            throw new InvalidDataException(errorMessageId, errorMessage);
        }
    }

    private String cutLength(String string, int maxLength) {
        if(string.length() > maxLength) {
            return string.substring(0, maxLength);
        } else {
            return string;
        }
    }
}