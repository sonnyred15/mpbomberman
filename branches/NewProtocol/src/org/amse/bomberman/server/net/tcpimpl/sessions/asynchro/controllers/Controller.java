
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers;

//~--- non-JDK imports --------------------------------------------------------
import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.server.gameinit.Game;
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
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates.ClientState;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates.NotJoinedState;

/**
 * Class that represents net controller of ingame player.
 * @author Kirilchuk V.E.
 */
public class Controller implements RequestExecutor {

    private final ResponseCreator protocol = new ResponseCreator();
    //
    private volatile ClientState state = new NotJoinedState(this);
    //
    private final Session session;
    private String clientName = "Default_name";

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
            this.session.send(protocol.noUnstartedGames2());//TODO converter must do it
        } else {
            this.session.send(protocol.unstartedGamesList2(games));//TODO converter must do it
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

        this.session.send(state.createGame(gameMapName, gameName, maxPlayers));
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

        this.session.send(state.joinGame(gameID));
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

        this.session.send(state.doMove(direction));
    }

    public void sendGameMapInfo() {
        this.session.send(state.getGameMapInfo());
    }

    public void tryStartGame() {
        this.session.send(state.startGame());
    }

    public void tryLeave() {
        this.session.send(state.leave());
    }

    public void tryPlaceBomb() {
        this.session.send(state.placeBomb());
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

        this.session.send(protocol.downloadGameMap2(gameMapName));
    }

    public void sendGameStatus() {
        System.out.println("Session: sended game status to client.");
        this.session.send(state.getGameStatus());
    }

    public void sendGameMapsList() {
        this.session.send(protocol.sendGameMapsList2());//TODO converter must do it
    }

    public void tryAddBot(List<String> args) throws InvalidDataException {
        if(args.size() != 1) {
            System.out.println("Session: tryAddBot warning. Not enough arguments. Cancelled.");
            throw new InvalidDataException(ProtocolConstants.ADD_BOT_MESSAGE_ID,
                                           "Not enough arguments.");
        }

        Iterator<String> iterator = args.iterator();
        String botName = iterator.next(); //TODO what`s about incorrect name.

        this.session.send(state.addBot(botName));
    }

    public void sendGameInfo() {
        System.out.println("Session: sended gameInfo to client.");
        this.session.send(state.getGameInfo());
    }

    public void addMessageToChat(List<String> args) throws InvalidDataException {
        if(args.isEmpty()) {
            System.out.println("Session: addMessageToChat error. Client tryed to add message, canceled. Wrong query.");

            throw new InvalidDataException(ProtocolConstants.CHAT_ADD_RESULT_MESSAGE_ID,
                                           "Not enough arguments.");
        }

        this.session.send(protocol.chatMessage(args.get(0)));
    }

    public void sendNewMessagesFromChat() {
        this.session.send(state.getNewMessagesFromChat()); //BIG TODO NOT WORKS
    }

    public void tryRemoveBot() {
        this.session.send(state.removeBot()); //BIG TODO NOT WORK
    }

    public void sendGamePlayersStats() {        
        System.out.println("Session: sended gamePlayersStats to client.");
        this.session.send(state.getGamePlayersStats());
    }

    public void setClientNickName(List<String> args) throws InvalidDataException {// "17 name"
        if(args.size() != 1) {
            throw new InvalidDataException(ProtocolConstants.CLIENT_NAME_MESSAGE_ID,
                    "Wrong number of arguments.");
        } else {
            Iterator<String> iterator = args.iterator();
            this.clientName = iterator.next();
            this.session.send(protocol.ok2(
                    ProtocolConstants.CLIENT_NAME_MESSAGE_ID,
                    "Name was set."));
        }
    }

    /**
     * Returns name of client.
     * @return name of client.
     */
    public String getClientNickName() {
        return clientName;
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

    public synchronized void setState(ClientState state) {//TODO synchronization?
        this.state = state;
    }
}