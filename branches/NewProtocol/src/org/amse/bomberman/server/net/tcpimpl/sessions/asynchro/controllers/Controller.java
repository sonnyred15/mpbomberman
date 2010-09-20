
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers;

//~--- non-JDK imports --------------------------------------------------------
import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.imodel.Player;
import org.amse.bomberman.server.net.Session;
import org.amse.bomberman.util.Constants.Direction;

//~--- JDK imports ------------------------------------------------------------

import java.util.Iterator;

import java.util.List;
import org.amse.bomberman.protocol.InvalidDataException;
import org.amse.bomberman.server.gameinit.control.GameEndedListener;
import org.amse.bomberman.protocol.ResponseCreator;
import org.amse.bomberman.protocol.RequestExecutor;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates.ClientState;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates.NotJoinedState;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates.CommandResult;

/**
 * Class that represents net controller of ingame player.
 * @author Kirilchuk V.E.
 */
public class Controller implements RequestExecutor, GameEndedListener {

    private final ResponseCreator protocol = new ResponseCreator();
    //
    private volatile ClientState state = new NotJoinedState(this);
    //
    private final Session session;
    private Game game;
    private int playerID;
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
            this.session.send(protocol.noUnstartedGames2());
        } else {
            this.session.send(protocol.unstartedGamesList2(games));
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
        if(game == null) {
            System.out.println("Session: sendMapArray warning. Canceled. Not joined to any game.");
            this.session.sendAnswer(protocol.notJoined(ProtocolConstants.CAPTION_GAME_MAP_INFO));

            return;
        }

        if(!game.isStarted()) {
            System.out.println("Session: sendMapArray warning. Canceled. Game is not started.");
            this.session.sendAnswer(protocol.notOK(
                    ProtocolConstants.CAPTION_GAME_MAP_INFO,
                    "Game is not started. You can`t get full game field info."));
        }

        System.out.println("Session: sended mapArray+explosions+playerInfo to client.");
        this.session.sendAnswer(protocol.gameMapInfo(this));
    }

    public void tryStartGame() {
        if(game == null) {
            System.out.println("Session: client tryed to start game, canceled. "
                    + "Not joined to any game.");
            this.session.sendAnswer(protocol.notJoined(
                    ProtocolConstants.CAPTION_START_GAME_RESULT));

            return;
        }

        if(game.isStarted()) {
            System.out.println("Session: startGame warning. "
                    + "Client tryed to start started game. "
                    + "Canceled.");
            this.session.sendAnswer(protocol.notOK(
                    ProtocolConstants.CAPTION_START_GAME_RESULT,
                    "Game is already started."));

            return;
        }

        boolean success = this.tryStartGame2();

        if(success) {
            System.out.println("Session: started game. " + "(gameName="
                    + this.game.getName() + ")");
            this.session.sendAnswer(protocol.ok(
                    ProtocolConstants.CAPTION_START_GAME_RESULT,
                    "Game started."));

            return;
        } else {
            System.out.println("Session: startGame warning. "
                    + "Client tryed to start game, canceled. "
                    + "Not an owner.");
            this.session.sendAnswer(protocol.notOK(
                    ProtocolConstants.CAPTION_START_GAME_RESULT,
                    "Not owner of game."));

            return;
        }
    }

    /**
     * Tryes to start game by this controller. Start of game possible
     * only if this controller is the owner of game.
     * If player was not joined to any game then false returns.
     * @return true if game was started, false otherwise.
     */
    private boolean tryStartGame2() {
        if(game == null) {
            System.err.println("Controller: tryStartGame warning. "
                    + "Client not joined to any game. StartGame ignored.");
            return false;
        }
        return this.game.tryStartGame(this);
    }

    public void tryLeave() {
        if(this.game == null) {
            System.out.println("Session: tryLeave warning. "
                    + "Client tryed to leave, canceled. "
                    + "Not joined to any game.");
            this.session.sendAnswer(protocol.notJoined(
                    ProtocolConstants.CAPTION_LEAVE_GAME_RESULT));

            return;
        }

        this.tryLeave2();
        System.out.println("Session: player has been disconnected from the game.");
        this.session.sendAnswer(protocol.ok(
                ProtocolConstants.CAPTION_LEAVE_GAME_RESULT,
                "Disconnected."));

        return;
    }

    /**
     * Removes all listeners correspond to this Controller.
     * Then removes client from game.
     * <p>
     * If client was not joined to any game, this method do nothing.
     */
    private void tryLeave2() {//TODO maybe make return type boolean and say to client about error?
        if(game == null) { //TODO in what situations this can happen?
            System.err.println("Controller: tryLeaveGame warning. "
                    + "Client not joined to any game. Leave ignored.");
        } else {
            this.game.removeGameEndedListener(this);
            this.game.leaveFromGame(this);
            this.game = null;
        }
    }

    public void tryPlaceBomb() {
        if(game == null) {
            System.out.println("Session: tryPlaceBomb warning. Canceled. Not joined to any game.");
            this.session.sendAnswer(protocol.notJoined(ProtocolConstants.CAPTION_PLACE_BOMB_RESULT));

            return;
        }

        if(!game.isStarted()) {
            System.out.println("Session: place bomb warning. Cancelled, Game is not started.");
            this.session.sendAnswer(protocol.wrongQuery(
                    ProtocolConstants.CAPTION_PLACE_BOMB_RESULT,
                    "Game is not started. Can`t place bomb."));

            return;
        }

        this.tryPlaceBomb2();
    }

    /**
     * Tryes to place bomb by controller`s player in controller`s game.
     * If client is not joined to any game then placing bomb will be ignored.
     */
    private void tryPlaceBomb2() {
        if(game == null) { //TODO it is already checked in tryPlaceBomb! Is it really need?
            System.err.println("Controller: tryPlaceBomb warning. "
                    + "Client not joined to any game. PlaceBomb ignored.");
            return;
        }

        //if game not null then playerID is valid!
        this.game.tryPlaceBomb(this.playerID);
    }

    public void sendDownloadingGameMap(List<String> args) {
        if(args.size() != 2) {
            System.out.println("Session: sendDownloadingGameMap warning. "
                    + "Wrong number of args. Error on client side.");
            this.session.sendAnswer(protocol.wrongQuery(
                    ProtocolConstants.CAPTION_DOWNLOAD_GAME_MAP,
                    "Wrong number of arguments."));

            return;
        }

        Iterator<String> iterator = args.iterator();
        String gameMapName = iterator.next() + ".map";
        this.session.sendAnswer(protocol.downloadGameMap(gameMapName));
    }

    public void sendGameStatus() {
        if(game == null) {
            System.out.println("Session: sendGameStatus warning. Canceled. Not joined to any game.");
            this.session.sendAnswer(protocol.notJoined(ProtocolConstants.CAPTION_GAME_STATUS));

            return;
        }

        System.out.println("Session: sended game status to client.");
        this.session.sendAnswer(protocol.sendGameStatus(game));
    }

    public void sendGameMapsList() {
        this.session.sendAnswer(protocol.sendGameMapsList());
    }

    public void tryAddBot(List<String> args) {
        if(args.size() != 1) {
            System.out.println("Session: tryAddBot warning. Not enough arguments. Cancelled.");
            this.session.sendAnswer(protocol.wrongQuery(
                    ProtocolConstants.CAPTION_JOIN_BOT_RESULT,
                    "Not enough arguments."));

            return;
        }

        Iterator<String> iterator = args.iterator();
        String botName = iterator.next();
        CommandResult joinBotResult = this.tryAddBotIntoMyGame(botName);

        switch(joinBotResult) {
            case NOT_JOINED: {
                System.out.println("Session: addBot warning. "
                        + "Tryed to add bot to game, canceled. "
                        + "Not joined to any game.");
                this.session.sendAnswer(protocol.notJoined(
                        ProtocolConstants.CAPTION_JOIN_BOT_RESULT));

                return;
            }

            case NOT_OWNER_OF_GAME: {

                // if not owner of game
                System.out.println("Session: addBot warning. "
                        + "Tryed to add bot to game, canceled. "
                        + "Not owner of the game.");
                this.session.sendAnswer(protocol.notOK(
                        ProtocolConstants.CAPTION_JOIN_BOT_RESULT,
                        "Not owner of game."));

                return;
            }

            case GAME_IS_FULL: {
                System.out.println("Session: addBot warning. "
                        + "Tryed to add bot, canceled. Game is full.");
                this.session.sendAnswer(protocol.notOK(
                        ProtocolConstants.CAPTION_JOIN_BOT_RESULT,
                        "Game is full. Try to add bot later."));

                return;
            }

            case GAME_IS_ALREADY_STARTED: {

                // if game.isStarted() true
                System.out.println("Session: addbot warning. "
                        + "Tryed to add bot to game ,canceled."
                        + " Game is already started.");
                this.session.sendAnswer(protocol.notOK(
                        ProtocolConstants.CAPTION_JOIN_BOT_RESULT,
                        "Game was already started."));

                return;
            }

            case RESULT_SUCCESS: {
                System.out.println("Session: added bot to game."
                        + this.game.getName());
                this.session.sendAnswer(protocol.ok(
                        ProtocolConstants.CAPTION_JOIN_BOT_RESULT,
                        "Bot added."));

                return;
            }

            default: {
                assert false : "Session: addbot error. Default block in switch "
                        + "statement. Error on server side.";
            }
        }
    }

    /**
     * Tryes to join bot into your game with specified nick name.
     * @param botName nick name of bot.
     * @return integer value that have next meanings
     * <p>
     * Controller.NOT_JOINED - if you are not joined to any game
     * and trying to add bot.
     * <p>
     * Controller.GAME_IS_ALREADY_STARTED - if game was already started
     * and you can not join bot.
     * <p>
     * Controller.NOT_OWNER_OF_GAME - if you are not owner of the game
     * <p>
     * Controller.GAME_IS_FULL - if game is full
     * and you can not join bot.
     * <p>
     * Controller.RESULT_SUCCESS - if bot was joined.
     */
    private CommandResult tryAddBotIntoMyGame(String botName) {
        CommandResult joinResult = CommandResult.NOT_JOINED;

        if(this.game != null) {
            joinResult = CommandResult.GAME_IS_FULL;

            if(!this.game.isFull()) {
                joinResult = CommandResult.GAME_IS_ALREADY_STARTED;

                if(!this.game.isStarted()) {
                    Player bot = this.game.tryAddBot(botName, this);

                    if(bot == null) {
                        joinResult = CommandResult.NOT_OWNER_OF_GAME;
                    } else {
                        joinResult = CommandResult.RESULT_SUCCESS;
                    }
                }
            }
        }

        return joinResult;
    }

    public void sendGameInfo() {
        if(game == null) {
            System.out.println("Session: sendGameInfo warning. Client tryed to get game info, canceled. Not joined to any game.");
            this.session.sendAnswer(protocol.notJoined(ProtocolConstants.CAPTION_GAME_INFO));

            return;
        }

        System.out.println("Session: sended gameInfo to client.");
        this.session.sendAnswer(protocol.sendGameInfo(this));
    }

    public void addMessageToChat(List<String> args) {
        if(args.isEmpty()) {
            System.out.println("Session: addMessageToChat error. Client tryed to add message, canceled. Wrong query.");
            this.session.sendAnswer(protocol.wrongQuery(
                    ProtocolConstants.CAPTION_ADD_CHAT_MSG_RESULT,
                    "Not enough arguments"));

            return;
        }

        if(game == null) {
            System.out.println("Session: addMessageToChat warning. "
                    + "Client tryed to add message, canceled. "
                    + "Not joined to any game.");
            this.session.sendAnswer(protocol.notJoined(
                    ProtocolConstants.CAPTION_ADD_CHAT_MSG_RESULT));

            return;
        }

        StringBuilder chatMessage = new StringBuilder(); //TODO must be util method
        for(String string : args) {
            chatMessage.append(string + " ");
        }

        System.out.println("Session: client added message to game chat. message="
                + chatMessage.toString());
        this.addMessageToChat(chatMessage.toString());
    }

    /**
     * Method to add message to client`s game chat.
     * If client not joined to any game message will be ignored.
     * @param message message to add in chat.
     */
    private void addMessageToChat(String message) {
        if(game == null) {
            System.err.println("Controller: addMessageToChat warning. "
                    + "Client not joined to any game. Message ignored.");
            return;
        }

        this.game.addMessageToChat(this.game.getPlayer(playerID), message);
    }

    public void sendNewMessagesFromChat() {
        if(game == null) {
            System.out.println("Session: getNewMessagesFromChat warning. Client tryed to get messages, canceled. Not joined to any game.");
            this.session.sendAnswer(protocol.notJoined(ProtocolConstants.CAPTION_NEW_CHAT_MSGS));

            return;
        }

        System.out.println("Session: sended new messages from chat to client.");
        this.session.sendAnswer(protocol.ok( //TODO this is deprecated command...so..
                ProtocolConstants.CAPTION_NEW_CHAT_MSGS,
                "No new messages."));
    }

    public void tryRemoveBot() {
        CommandResult removeResult = this.tryRemoveBot2();

        switch(removeResult) {
            case NOT_JOINED: {
                System.out.println("Session: removeBot warning. "
                        + "Tryed to remove bot from game, canceled."
                        + " Not joined to any game.");
                this.session.sendAnswer(protocol.notJoined(
                        ProtocolConstants.CAPTION_REMOVE_BOT_RESULT));

                return;
            }

            case NOT_OWNER_OF_GAME: {
                System.out.println("Session: removeBot warning. "
                        + "Tryed to remove bot from game, canceled."
                        + " Not owner of the game.");
                this.session.sendAnswer(protocol.notOK(
                        ProtocolConstants.CAPTION_REMOVE_BOT_RESULT,
                        "Not owner of game."));

                return;
            }

            case GAME_IS_ALREADY_STARTED: {
                System.out.println("Session: removeBot warning. "
                        + "Tryed to remove bot from game ,canceled. "
                        + "Game is already started.");
                this.session.sendAnswer(protocol.notOK(
                        ProtocolConstants.CAPTION_REMOVE_BOT_RESULT,
                        "Game was already started."));

                return;
            }

            case NO_SUCH_BOT: {
                System.out.println("Session: removeBot warning. "
                        + "Tryed to remove bot from game, canceled."
                        + "No bot to remove.");
                this.session.sendAnswer(protocol.notOK(
                        ProtocolConstants.CAPTION_REMOVE_BOT_RESULT,
                        "No bot to remove."));

                return;
            }

            case RESULT_SUCCESS: {
                System.out.println("Session: removed bot from game."
                        + game.getName());
                this.session.sendAnswer(protocol.ok(
                        ProtocolConstants.CAPTION_REMOVE_BOT_RESULT,
                        "Bot removed."));

                return;
            }

            default: {
                assert false : "Session: removeBot error. Default block in switch "
                        + "statement. Error on server side.";
            }
        }
    }

    /**
     * Tryes to remove some bot. It is not guarantied but
     * this method must remove last joined bot.
     * @return integer value that have next meanings
     * <p>
     * Controller.NOT_JOINED - if you are not joined to any game
     * and trying to remove bot.
     * <p>
     * Controller.GAME_IS_ALREADY_STARTED - if game was already started
     * and you can not remove bot.
     * <p>
     * Controller.NOT_OWNER_OF_GAME - if you are not owner of the game
     * and trying to remove bot.
     * <p>
     * Controller.NO_SUCH_BOT - if there is no bot to remove.
     * <p>
     * Controller.RESULT_SUCCESS - if bot was removed.
     */
    private CommandResult tryRemoveBot2() {
        CommandResult removeResult = CommandResult.NOT_JOINED;

        if(this.game != null) {
            removeResult = CommandResult.GAME_IS_ALREADY_STARTED;

            if(!this.game.isStarted()) {
                removeResult = CommandResult.NOT_OWNER_OF_GAME;

                if(this.game.getOwner() == this) {
                    boolean removed = this.game.tryRemoveLastBot();
                    if(removed) {
                        removeResult = CommandResult.RESULT_SUCCESS;
                    } else {
                        removeResult = CommandResult.NO_SUCH_BOT;
                    }
                }
            }
        }

        return removeResult;
    }

    public void sendGamePlayersStats() {
        if(game == null) {
            System.out.println("Session: sendGamePlayersStats warning. Canceled. "
                    + "Not joined to any game.");
            this.session.sendAnswer(protocol.notJoined(
                    ProtocolConstants.CAPTION_GAME_PLAYERS_STATS));

            return;
        }

        if(!game.isStarted()) {
            System.out.println("Session: sendGamePlayersStats warning. Canceled. "
                    + "Game is not started.");
            this.session.sendAnswer(protocol.notOK(
                    ProtocolConstants.CAPTION_GAME_PLAYERS_STATS,
                    "Game is not started. You can`t get stats."));

            return;
        }

        System.out.println("Session: sended gamePlayersStats to client.");
        this.session.sendAnswer(protocol.sendPlayersStats(game));
    }

    public void setClientName(List<String> args) {// "17 name"
        if(args.size() != 1) {
            System.out.println("Session: setClientName warning. Canceled. Wrong query.");
            this.session.sendAnswer(protocol.wrongQuery(
                    ProtocolConstants.CAPTION_SET_CLIENT_NAME,
                    "Wrong number of arguments."));
        } else {
            Iterator<String> iterator = args.iterator();
            this.clientName = iterator.next();
            this.session.sendAnswer(protocol.ok(
                    ProtocolConstants.CAPTION_SET_CLIENT_NAME,
                    "Name was set."));
        }
    }

    /**
     * Method from GameEndedListener interface. In current realization
     * removes GameEndedListener from game and leaving game,
     * setting game of controller to null and ID of player to -1.
     *
     * @see GameEndedListener
     */
    @Override
    public void gameEnded(Game gameThatEnded) {
        this.game.removeGameEndedListener(this);
        this.game.leaveFromGame(this);
        this.game = null;
        this.playerID = -1;
    }

    /**
     * Setting the ingame playerID.
     * @param playerID ID to set up.
     */
    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    /**
     * Returns name of client.
     * @return name of client.
     */
    public String getClientNickName() {
        return clientName;
    }

    /**
     * Returns reference to your game or null if you are not joined
     * to any game.
     * @return reference to your game or null if you are not joined
     * to any game.
     */
    public Game getMyGame() {
        return game;
    }

    /**
     * Returns reference to your Player
     * or null if you are not joined to any game.
     * @return reference to your Player
     * or null if you are not joined to any game.
     */
    public Player getPlayer() {
        return this.game.getPlayer(this.playerID);
    }

    public int getID() {
        return playerID;
    }

    public Session getSession() {
        return session;
    }

    public void notify(ProtocolMessage<Integer, String> message) {
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

    public void setState(ClientState state) {//TODO synchronization?
        this.state = state;
    }

}
