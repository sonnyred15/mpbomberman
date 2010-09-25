
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers;

//~--- non-JDK imports --------------------------------------------------------
import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.server.gameservice.Game;
import org.amse.bomberman.server.net.Session;
import org.amse.bomberman.util.Constants.Direction;

//~--- JDK imports ------------------------------------------------------------

import java.util.Iterator;

import java.util.List;
import org.amse.bomberman.protocol.InvalidDataException;
import org.amse.bomberman.protocol.ResponseCreator;
import org.amse.bomberman.protocol.RequestExecutor;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.server.gameservice.GamePlayer;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates.ClientState;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates.NotJoinedState;

/**
 * Class that represents net controller of ingame player.
 * @author Kirilchuk V.E.
 */
public class Controller implements RequestExecutor {

    private final ResponseCreator protocol = new ResponseCreator();
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
    public Controller(Session session) {
        this.session = session;
    }

    public void sendGames() {
        List<Game> games = this.session.getGameStorage().getGamesList();

        if(games.isEmpty()) {
            System.out.println("Session: sendGames info. No unstarted games finded.");
            sendToClient(protocol.noUnstartedGames());//TODO converter must do it
        } else {
            sendToClient(protocol.unstartedGamesList(games));//TODO converter must do it
        }
    }

    public void tryCreateGame(List<String> args) throws InvalidDataException {// "1 gameName mapName maxPlayers playerName"
        if(args.size() != 4) { // if we getted command with wrong number of args
            System.out.println("Session: createGame warning. Client tryed to create game, canceled. "
                    + "Wrong command parameters. Error on client side.");
            throw new InvalidDataException(ProtocolConstants.CREATE_GAME_MESSAGE_ID,
                                           "Wrong number of arguments.");
        }

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

        String playerName = iterator.next();//TODO whitespace name!?!?!?!
        playerName = cutLength(playerName, Constants.MAX_PLAYER_NAME_LENGTH);

        sendToClient(state.createGame(gameMapName, gameName, maxPlayers));
    }

    public void tryJoinGame(List<String> args) throws InvalidDataException {// "2" "gameID" "playerName"
        if(args.size() != 2) {
            System.out.println("Session: joinGame error. Wrong command parameters. Error on client side.");
            throw new InvalidDataException(ProtocolConstants.JOIN_GAME_MESSAGE_ID,
                                           "Wrong number of arguments.");
        }

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
        if(args.size() != 1) {
            System.out.println("Session: doMove warning. Client tryed to move, canceled. "
                    + "Wrong number of parameters. Error on client side.");
            throw new InvalidDataException(ProtocolConstants.DO_MOVE_MESSAGE_ID,
                                           "Wrong number of arguments.");
        }

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
        if(args.size() != 1) {
            System.out.println("Session: sendDownloadingGameMap warning. "
                    + "Wrong number of args. Error on client side.");
            throw new InvalidDataException(ProtocolConstants.DOWNLOAD_GAME_MAP_MESSAGE_ID,
                                           "Wrong number of arguments.");
        }

        Iterator<String> iterator = args.iterator();
        String gameMapName = iterator.next() + ".map"; //TODO change this on client and server

        sendToClient(protocol.downloadGameMap(gameMapName));
    }

    public void sendGameStatus() {
        System.out.println("Session: sended game status to client.");
        sendToClient(state.getGameStatus());
    }

    public void sendGameMapsList() {
        sendToClient(protocol.sendGameMapsList());//TODO converter must do it
    }

    public void tryAddBot(List<String> args) throws InvalidDataException {
        if(args.size() != 1) {
            System.out.println("Session: tryAddBot warning. Not enough arguments. Cancelled.");
            throw new InvalidDataException(ProtocolConstants.BOT_ADD_MESSAGE_ID,
                                           "Not enough arguments.");
        }

        Iterator<String> iterator = args.iterator();
        String botName = iterator.next(); //TODO what`s about incorrect name.

        sendToClient(state.addBot(botName));
    }

    public void sendGameInfo() {
        System.out.println("Session: sended gameInfo to client.");
        sendToClient(state.getGameInfo());
    }

    public void addMessageToChat(List<String> args) throws InvalidDataException {
        if(args.isEmpty()) {
            System.out.println("Session: addMessageToChat error. Client tryed to add message, canceled. Wrong query.");

            throw new InvalidDataException(ProtocolConstants.CHAT_ADD_RESULT_MESSAGE_ID,
                                           "Not enough arguments.");
        }

        sendToClient(protocol.chatMessage(args.get(0)));
    }

    public void sendNewMessagesFromChat() {
        sendToClient(state.getNewMessagesFromChat()); //BIG TODO NOT WORKS
    }

    public void tryRemoveBot() {
        sendToClient(state.removeBot()); //BIG TODO NOT WORK
    }

    public void sendGamePlayersStats() {        
        System.out.println("Session: sended gamePlayersStats to client.");
        sendToClient(state.getGamePlayersStats());
    }

    public void setClientNickName(List<String> args) throws InvalidDataException {// "17 name"
        if(args.size() != 1) {
            throw new InvalidDataException(ProtocolConstants.SET_NAME_MESSAGE_ID,
                    "Wrong number of arguments.");
        } else {
            Iterator<String> iterator = args.iterator();
            String name = iterator.next();
            this.player.setNickName(name);
            sendToClient(protocol.ok(
                    ProtocolConstants.SET_NAME_MESSAGE_ID,
                    "Name was set."));
        }
    }

    public Session getSession() {
        return session;
    }

    public void sendToClient(ProtocolMessage<Integer, String> message) {
        this.session.send(message);
    }

    private String cutLength(String string, int maxLength) {
        if(string.length() > maxLength) {
            return string.substring(0, maxLength);
        } else {
            return string;
        }
    }

    public ClientState getState() {
        return state;
    }

    public synchronized void setState(ClientState state) {
        this.state = state;
    }

    public NetGamePlayer getGamePlayer() {
        return player;
    }
}