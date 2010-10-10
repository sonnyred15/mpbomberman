package org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers;

//~--- non-JDK imports --------------------------------------------------------
import org.amse.bomberman.server.gameservice.impl.NetGamePlayer;
import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.server.gameservice.impl.Game;
import org.amse.bomberman.server.net.Session;
import org.amse.bomberman.util.Direction;

//~--- JDK imports ------------------------------------------------------------

import java.util.Iterator;

import java.util.List;
import org.amse.bomberman.protocol.responses.ResponseCreator;
import org.amse.bomberman.protocol.requests.RequestExecutor;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.protocol.requests.InvalidDataException;
import org.amse.bomberman.server.net.SessionEndListener;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates.ClientState;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates.NotJoinedState;

/**
 * Class that represents net controller of ingame player.
 * @author Kirilchuk V.E.
 */
public class Controller implements RequestExecutor, SessionEndListener {

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

    public void sendGames() {
        List<Game> games = this.session.getGameStorage().getGamesList();
        sendToClient(protocol.unstartedGamesList(games));
    }

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

    public void tryJoinGame(List<String> args) throws InvalidDataException {//gameID playerName
        checkArgsNum(args, 2, ProtocolConstants.JOIN_GAME_MESSAGE_ID,
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

        String playerName = iterator.next();
        playerName = cutLength(playerName, Constants.MAX_PLAYER_NAME_LENGTH);

        sendToClient(state.joinGame(gameID));
    }

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

    public void sendGameMapInfo() {
        sendToClient(state.getGameMapInfo());
    }

    public void tryStartGame() {
        sendToClient(state.startGame());
    }

    public void tryLeave() {
        sendToClient(state.leave());
    }

    public void tryPlaceBomb() {
        sendToClient(state.placeBomb());
    }

    public void sendDownloadingGameMap(List<String> args) throws
            InvalidDataException {
        checkArgsNum(args, 1, ProtocolConstants.DOWNLOAD_GAME_MAP_MESSAGE_ID,
                "Wrong number of arguments.");

        Iterator<String> iterator = args.iterator();
        String gameMapName = iterator.next();

        sendToClient(protocol.downloadGameMap(gameMapName));
    }

    public void sendGameStatus() {
        System.out.println("Session: sended game status to client.");
        sendToClient(state.getGameStatus());
    }

    public void sendGameMapsList() {
        sendToClient(protocol.gameMapsList());//TODO converter must do it
    }

    public void tryAddBot(List<String> args) throws InvalidDataException {
        checkArgsNum(args, 1, ProtocolConstants.BOT_ADD_MESSAGE_ID,
                "Wrong number of arguments.");

        Iterator<String> iterator = args.iterator();
        String botName = iterator.next(); //TODO what`s about incorrect name. Ex.: null or empty!?

        sendToClient(state.addBot(botName));
    }

    public void sendGameInfo() {
        System.out.println("Session: sended gameInfo to client.");
        sendToClient(state.getGameInfo());
    }

    public void addMessageToChat(List<String> args) throws InvalidDataException {
        checkArgsNum(args, 1, ProtocolConstants.CHAT_ADD_RESULT_MESSAGE_ID,
                "Wrong number of arguments.");

        sendToClient(state.addMessageToChat(args.get(0)));
    }

    public void sendNewMessagesFromChat() {
        sendToClient(state.getNewMessagesFromChat());
    }

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

    public void sendGamePlayersStats() {        
        System.out.println("Session: sended gamePlayersStats to client.");
        sendToClient(state.getGamePlayersStats());
    }

    public void setClientNickName(List<String> args) throws InvalidDataException {// "17 name"
        checkArgsNum(args, 1, ProtocolConstants.SET_NAME_MESSAGE_ID,
                "Wrong number of arguments.");

        Iterator<String> iterator = args.iterator();
        String name = iterator.next();
        this.player.setNickName(name);
        sendToClient(protocol.ok(ProtocolConstants.SET_NAME_MESSAGE_ID,
                "Name was set."));
    }

    public void sendToClient(ProtocolMessage<Integer, String> message) {
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