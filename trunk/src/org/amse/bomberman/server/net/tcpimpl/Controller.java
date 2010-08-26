
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.GameMap;
import org.amse.bomberman.server.gameinit.imodel.Player;
import org.amse.bomberman.server.net.ISession;
import org.amse.bomberman.util.Constants.Direction;
import org.amse.bomberman.util.Creator;

//~--- JDK imports ------------------------------------------------------------

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.List;
import org.amse.bomberman.server.gameinit.control.GameEndedListener;
import org.amse.bomberman.protocol.ResponseCreator;
import org.amse.bomberman.protocol.RequestExecutor;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.protocol.ProtocolConstants;

/**
 * Class that represents net controller of ingame player.
 * @author Kirilchuk V.E.
 */
public class Controller implements RequestExecutor, GameEndedListener {
    private final ResponseCreator protocol = new ResponseCreator();
    private final MyTimer    timer = new MyTimer(System.currentTimeMillis());
    //    
    private final ISession  session;
    private Game            game;
    private int             playerID;
    private String          clientName = "Default_name";

    /**
     * Constructor of controller.
     * @param sessionServer server of session that owns this controller.
     * @param session owner of this controller.
     */
    public Controller(ISession session) {
        this.session = session;
    }

    public void sendGames() {
        List<Game> games = this.session.getGameStorage().getGamesList();

        if (games.size() == 0) {
            System.out.println("Session: sendGames info. No unstarted games finded.");
            this.session.sendAnswer(protocol.noUnstartedGames());
        } else {
            this.session.sendAnswer(protocol.unstartedGamesList(games));
        }
    }

    public void tryCreateGame(String[] args) {// "1 gameName mapName maxPlayers playerName"
        if (this.game != null) {//TODO maybe make private method from this?
            System.out.println("Session: createGame warning. Client tryed to create game, canceled. Already in other game.");
            this.session.sendAnswer(protocol.notOK(
                    ProtocolConstants.CAPTION_CREATE_GAME_RESULT,
                    "Leave another game first"));
            return;
        }

        if (args.length != 5) { // if we getted command with wrong number of args
            System.out.println("Session: createGame warning. Client tryed to create game, canceled. "
                    + "Wrong command parameters. Error on client side.");
            this.session.sendAnswer(protocol.wrongQuery (
                    ProtocolConstants.CAPTION_CREATE_GAME_RESULT,
                    "Wrong number of arguments."));

            return;
        }

        //here all is OK
        String gameName = args[1];
        String mapName = args[2];
        String playerName = args[4];

        if (playerName.length() > 10) { //TODO must be utilities function
            playerName = playerName.substring(0, 10);
        }

        int maxPlayers = -1;
        try {
            maxPlayers = Integer.parseInt(args[3]);
        } catch (NumberFormatException ex) {
            System.out.println("Session: createGame warning. Client tryed to create game, canceled. "
                    + "Wrong command parameters. Error on client side. "
                    + ex.getMessage());
            this.session.sendAnswer(protocol.wrongQuery (
                    ProtocolConstants.CAPTION_CREATE_GAME_RESULT,
                    "MaxPlayers argument must be integer."));
            return;
        }

        try {
            this.tryCreateGame(mapName, gameName, maxPlayers);//overloaded private function
            this.session.sendAnswer(protocol.ok (
                    ProtocolConstants.CAPTION_CREATE_GAME_RESULT,
                    "Game created."));

            return;
        } catch (FileNotFoundException ex) {
            System.out.println("Session: createGame warning. Client tryed to create game, canceled. "
                    + "Map wasn`t founded on server." + " Map=" + mapName);
            this.session.sendAnswer(protocol.notOK(
                    ProtocolConstants.CAPTION_CREATE_GAME_RESULT,
                    "No such map on server."));

            return;
        } catch (IOException ex) {
            System.err.println("Session: createGame error while loadimg map. "
                    + " Map=" + mapName + " " + ex.getMessage());
            this.session.sendAnswer(protocol.notOK(
                    ProtocolConstants.CAPTION_CREATE_GAME_RESULT,
                    "Error on server side, while loading map."));
            return;
        }
    }

    /**
     * Creates game. Add it to server. Setting session as owner of the game
     * and tryJoin owner into game.
     * <p>
     * If owner was in other game and tryes to create game
     * then this method will disconnect him from previous game, cause only one
     * game for client is supported.
     * <p>
     * Additionally setting this controller as GameEndedListener.
     * @see GameEndedListener
     * @see Game
     * @see GameMap
     * @param gameMapName name of gameMap to create.
     * @param gameName name of game to create.
     * @param maxPlayers maxPlayers parameter of game.
     * @throws FileNotFoundException if no gameMap with such name was finded.
     * @throws IOException if IO errors occurs while creating gameMap.
     */
    private void tryCreateGame(String gameMapName, String gameName,
                              int maxPlayers) throws FileNotFoundException,
                                                     IOException {
        this.game = Creator.createGame(this.session.getGameStorage(), this, 
                                       gameMapName, gameName, maxPlayers);
        this.game.addGameEndedListener(this);
    }

    public void tryJoinGame(String[] args) {// "2" "gameID" "playerName"
        if (this.game != null) {
            System.out.println("Session: tryJoinGame warning. Client tryed to join game, canceled. Already in other game.");
            this.session.sendAnswer(protocol.notOK(
                    ProtocolConstants.CAPTION_JOIN_GAME_RESULT,
                    "Leave another game first"));
            return;
        }

        if(args.length!=3){
            System.out.println("Session: joinGame error. Wrong command parameters. Error on client side.");
            this.session.sendAnswer(protocol.wrongQuery (
                    ProtocolConstants.CAPTION_JOIN_GAME_RESULT,
                    "Wrong number of arguments."));
            return;
        }

        //here all is OK
        int gameID = 0;
        String playerName = args[2];

        if (playerName.length() > 10) { // TODO must be util function
            playerName = playerName.substring(0, 10);
        }

        try {
                gameID = Integer.parseInt(args[1]);
           } catch (NumberFormatException ex) {
                System.out.println("Session: joinGame error. " +
                                   "Wrong command parameters. " +
                                   "Error on client side. gameID must be int. " +
                                   ex.getMessage());
            this.session.sendAnswer(protocol.wrongQuery (
                    ProtocolConstants.CAPTION_JOIN_GAME_RESULT,
                    "GameID argument must be integer."));
            return;
        }

        // all is ok
        CommandResult joinResult = this.tryJoinGame(gameID);

        switch (joinResult) {
            case NO_SUCH_UNSTARTED_GAME : {

                // if no unstarted gameParams with such gameID finded
                System.out.println("Session: client tryed to join gameID=" + gameID +
                                  " ,canceled." + " No such game on server.");
                this.session.sendAnswer(protocol.notOK(
                        ProtocolConstants.CAPTION_JOIN_GAME_RESULT,
                        "No such game."));

                return;
            }

            case GAME_IS_ALREADY_STARTED : {

                // if gameParams with such gameID already started
                System.out.println("Session: joinGame warning. Client tryed to join gameID=" +
                           gameID + " ,canceled." +
                           " Game is already started. ");
                this.session.sendAnswer(protocol.notOK(
                        ProtocolConstants.CAPTION_JOIN_GAME_RESULT,
                        "Game was already started."));

                return;
            }

            case GAME_IS_FULL : {

                System.out.println("Session: joinGame warning. Client tryed to join to full game, canceled.");
                this.session.sendAnswer(protocol.notOK(
                        ProtocolConstants.CAPTION_JOIN_GAME_RESULT,
                        "Game is full. Try to join later."));

                return;
            }

            case RESULT_SUCCESS : {

                System.out.println("Session: client joined to game." + " GameID=" +
                           gameID + " Player=" + playerName);
                this.session.sendAnswer(protocol.ok(
                        ProtocolConstants.CAPTION_JOIN_GAME_RESULT,
                        "Joined."));

                return;
            }

            default: {
                assert false;
            }
        }

    }

    /**
     * Tryes to join controller into game with specified nick name.
     * @param gameID ID of game to join in.
     * @param playerName nick name of player.
     * @return integer value that have next meanings
     * <p>
     * Controller.NO_SUCH_UNSTARTED_GAME - if there is no unstarted game
     * with such ID.
     * <p>
     * Controller.GAME_IS_ALREADY_STARTED - if game was already started
     * and you can not join.
     * <p>
     * Controller.GAME_IS_FULL - if game is full
     * and you can not join.
     * <p>
     * Controller.RESULT_SUCCESS - if you was joined.
     */
    private CommandResult tryJoinGame(int gameID) {
        if (this.game != null) {    // if not correct client can create multiple games
                                    // we just disconnect him from first game!
            game.leaveFromGame(this);
        }

        Game gameToJoin = this.session.getGameStorage().getGame(gameID);
        CommandResult  joinResult = CommandResult.NO_SUCH_UNSTARTED_GAME;

        if (gameToJoin != null) {
            joinResult = CommandResult.GAME_IS_FULL;

            if (!gameToJoin.isFull()) {
                joinResult = CommandResult.GAME_IS_ALREADY_STARTED;

                if (!gameToJoin.isStarted()) {
                    this.playerID = gameToJoin.tryJoin(this);
                    this.game = gameToJoin;

                    assert this.playerID != (-1);

                    joinResult = CommandResult.RESULT_SUCCESS;
                    this.game.addGameEndedListener(this);

                    if (this.playerID == -1) {
                        joinResult = CommandResult.GAME_IS_FULL;    // TODO must never happen if synchronization is ok.
                    } else {
                        joinResult = CommandResult.RESULT_SUCCESS;
                        this.game.addGameEndedListener(this);
                    }
                }
            }
        }

        return joinResult;
    }

    public void tryDoMove(String[] args) {
        if (this.game == null) {
            System.out.println("Session: doMove warning. " +
                       "Client tryed to move, canceled. " +
                       "Not joined to any game.");
            this.session.sendAnswer(protocol.notJoined(
                    ProtocolConstants.CAPTION_DO_MOVE_RESULT));

            return;
        }

        if (timer.getDiff() < Constants.GAME_STEP_TIME) {
                System.out.println("Session: doMove warning. " +
                           "Client tryed to move, canceled. " +
                           "Moves allowed only every " +
                           Constants.GAME_STEP_TIME + "ms.");
                this.session.sendAnswer(protocol.ok( //TODO it must not be ok =)
                        ProtocolConstants.CAPTION_DO_MOVE_RESULT,
                        "false"));

                return;
        }

        if (args.length != 2) {
            System.out.println("Session: createGame warning. Client tryed to create game, canceled. "
                    + "Wrong command parameters. Error on client side.");
            this.session.sendAnswer(protocol.wrongQuery(
                    ProtocolConstants.CAPTION_DO_MOVE_RESULT,
                    "Wrong number of arguments."));

            return;
        }

        int     dir   = 0;
        boolean moved = false;

        try {
            dir = Integer.parseInt(args[1]);    // throws NumberFormatException

            Direction direction = Direction.fromInt(dir);    // throws IllegalArgumentException

            moved = this.tryDoMove(direction);
        } catch (NumberFormatException ex) {
            System.out.println("Session: doMove error. "
                    + "Unsupported direction(not int). "
                    + "Error on client side." + " direction="
                    + args[1] + ex.getMessage());
            this.session.sendAnswer(protocol.wrongQuery(
                    ProtocolConstants.CAPTION_DO_MOVE_RESULT,
                    "Wrong move value."));

            return;
        } catch (IllegalArgumentException ex) {
            System.out.println("Session: doMove error. "
                    + "Unsupported direction. "
                    + "Error on client side." + " direction="
                    + args[1] + ex.getMessage());
            this.session.sendAnswer(protocol.wrongQuery(
                    ProtocolConstants.CAPTION_DO_MOVE_RESULT,
                    "Wrong move value."));

            return;
        }

        if (moved) {
            timer.setStartTime(System.currentTimeMillis());
        }

        this.session.sendAnswer(protocol.ok(
                ProtocolConstants.CAPTION_DO_MOVE_RESULT,
                ""+moved));
    }

    /**
     * Tryes to move controller`s player in defined direction
     * in controller`s game. If player was not joined to
     * any game move will be ignored.
     * @param dir move direction
     * @return true if player was moved, false otherwise.
     */
    private boolean tryDoMove(Direction dir) {
        if (game == null) { //TODO in what situations this can happen?
            System.err.println("Controller: tryDoMove warning. "
                    + "Client not joined to any game. DoMove ignored.");
            return false;
        }

        return this.game.tryDoMove(this.playerID, dir);
    }

    public void sendGameMapInfo() {
        if (game == null) {
            System.out.println("Session: sendMapArray warning. Canceled. Not joined to any game.");
            this.session.sendAnswer(protocol.notJoined(ProtocolConstants.CAPTION_GAME_MAP_INFO));

            return;
        }

        if (!game.isStarted()) {
            System.out.println("Session: sendMapArray warning. Canceled. Game is not started.");
            this.session.sendAnswer(protocol.notOK(
                    ProtocolConstants.CAPTION_GAME_MAP_INFO,
                    "Game is not started. You can`t get full game field info."));
        }

        System.out.println("Session: sended mapArray+explosions+playerInfo to client.");
        this.session.sendAnswer(protocol.gameMapInfo(this));
    }

    public void tryStartGame() {
        if (game == null) {
            System.out.println("Session: client tryed to start game, canceled. " +
                               "Not joined to any game.");
            this.session.sendAnswer(protocol.notJoined(
                    ProtocolConstants.CAPTION_START_GAME_RESULT));

            return;
        }

        if (game.isStarted()) {
            System.out.println("Session: startGame warning. " +
                               "Client tryed to start started game. " +
                               "Canceled.");
            this.session.sendAnswer(protocol.notOK(
                    ProtocolConstants.CAPTION_START_GAME_RESULT,
                    "Game is already started."));

            return;
        }

        boolean success = this.tryStartGame2();

        if (success) {
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
        if(game==null){
            System.err.println("Controller: tryStartGame warning. " +
                    "Client not joined to any game. StartGame ignored.");
            return false;
        }
        return this.game.tryStartGame(this);
    }

    public void tryLeave() {
        if (this.game == null) {
            System.out.println("Session: tryLeave warning. " +
                       "Client tryed to leave, canceled. " +
                       "Not joined to any game.");
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
        if (game == null) { //TODO in what situations this can happen?
            System.err.println("Controller: tryLeaveGame warning. " +
                    "Client not joined to any game. Leave ignored.");
        } else {
            this.game.removeGameEndedListener(this);
            this.game.leaveFromGame(this);
            this.game = null;
        }
    }

    public void tryPlaceBomb() {
        if (game == null) {
            System.out.println("Session: tryPlaceBomb warning. Canceled. Not joined to any game.");
            this.session.sendAnswer(protocol.notJoined(ProtocolConstants.CAPTION_PLACE_BOMB_RESULT));

            return;
        }

        if (!game.isStarted()) {
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
        if (game == null) { //TODO it is already checked in tryPlaceBomb! Is it really need?
            System.err.println("Controller: tryPlaceBomb warning. " +
                    "Client not joined to any game. PlaceBomb ignored.");
            return;
        }

        //if game not null then playerID is valid!
        this.game.tryPlaceBomb(this.playerID);
    }

    public void sendDownloadingGameMap(String[] args) {
        if (args.length != 2) {
            System.out.println("Session: sendDownloadingGameMap warning. " +
                               "Wrong number of args. Error on client side.");
            this.session.sendAnswer(protocol.wrongQuery(
                    ProtocolConstants.CAPTION_DOWNLOAD_GAME_MAP,
                    "Wrong number of arguments."));

            return;
        }

        String  gameMapName = args[1] + ".map";
        this.session.sendAnswer(protocol.downloadGameMap(gameMapName));
    }

    public void sendGameStatus() {
        if (game == null) {
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

    public void tryAddBot(String[] args) {
        if (args.length != 2) {
            System.out.println("Session: tryAddBot warning. Not enough arguments. Cancelled.");
            this.session.sendAnswer(protocol.wrongQuery(
                    ProtocolConstants.CAPTION_JOIN_BOT_RESULT,
                    "Not enough arguments."));

            return;
        }

        String botName = args[1];
        CommandResult joinBotResult = this.tryAddBotIntoMyGame(botName);

        switch (joinBotResult) {
            case NOT_JOINED : {
                System.out.println("Session: addBot warning. " +
                                   "Tryed to add bot to game, canceled. " +
                                   "Not joined to any game.");
                this.session.sendAnswer(protocol.notJoined(
                        ProtocolConstants.CAPTION_JOIN_BOT_RESULT));

                return;
            }

            case NOT_OWNER_OF_GAME : {

                // if not owner of game
                System.out.println("Session: addBot warning. " +
                                   "Tryed to add bot to game, canceled. " +
                                   "Not owner of the game.");
                this.session.sendAnswer(protocol.notOK(
                        ProtocolConstants.CAPTION_JOIN_BOT_RESULT,
                        "Not owner of game."));

                return;
            }

            case GAME_IS_FULL : {
                System.out.println("Session: addBot warning. " +
                                   "Tryed to add bot, canceled. Game is full.");
                this.session.sendAnswer(protocol.notOK(
                        ProtocolConstants.CAPTION_JOIN_BOT_RESULT,
                        "Game is full. Try to add bot later."));

                return;
            }

            case GAME_IS_ALREADY_STARTED : {

                // if game.isStarted() true
                System.out.println("Session: addbot warning. " +
                           "Tryed to add bot to game ,canceled." +
                           " Game is already started.");
                this.session.sendAnswer(protocol.notOK(
                        ProtocolConstants.CAPTION_JOIN_BOT_RESULT,
                        "Game was already started."));

                return;
            }

            case RESULT_SUCCESS : {
                System.out.println("Session: added bot to game." +
                                   this.game.getName());
                this.session.sendAnswer(protocol.ok(
                        ProtocolConstants.CAPTION_JOIN_BOT_RESULT,
                        "Bot added."));

                return;
            }

            default : {
                assert false : "Session: addbot error. Default block in switch " +
                               "statement. Error on server side.";
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

        if (this.game != null) {
            joinResult = CommandResult.GAME_IS_FULL;

            if (!this.game.isFull()) {
                joinResult = CommandResult.GAME_IS_ALREADY_STARTED;

                if (!this.game.isStarted()) {
                    Player bot = this.game.tryAddBot(botName, this);

                    if (bot == null) {
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
        if (game == null) {
            System.out.println("Session: sendGameInfo warning. Client tryed to get game info, canceled. Not joined to any game.");
            this.session.sendAnswer(protocol.notJoined(ProtocolConstants.CAPTION_GAME_INFO));

            return;
        }

        System.out.println("Session: sended gameInfo to client.");
        this.session.sendAnswer(protocol.sendGameInfo(this));
    }

    public void addMessageToChat(String[] args) { //TODO not sending any response!!!
        if (args.length < 2) {
            System.out.println("Session: addMessageToChat error. Client tryed to add message, canceled. Wrong query.");
            this.session.sendAnswer(protocol.wrongQuery(
                    ProtocolConstants.CAPTION_ADD_CHAT_MSG_RESULT,
                    "Not enough arguments"));

            return;
        }

        if (game == null) {
            System.out.println("Session: addMessageToChat warning. " +
                               "Client tryed to add message, canceled. " +
                               "Not joined to any game.");
            this.session.sendAnswer(protocol.notJoined(
                    ProtocolConstants.CAPTION_ADD_CHAT_MSG_RESULT));

            return;
        }

        StringBuilder chatMessage = new StringBuilder(); //TODO must be util method

        for (int i = 1; i < args.length; ++i) {
            chatMessage.append(args[i] + " ");
        }

        System.out.println("Session: client added message to game chat. message=" +
                           chatMessage.toString());
        this.addMessageToChat(chatMessage.toString());
    }

    /**
     * Method to add message to client`s game chat.
     * If client not joined to any game message will be ignored.
     * @param message message to add in chat.
     */
    private void addMessageToChat(String message) {
        if (game == null) {
            System.err.println("Controller: addMessageToChat warning. " +
                    "Client not joined to any game. Message ignored.");
            return;
        }

        this.game.addMessageToChat(this.game.getPlayer(playerID), message);
    }

    public void sendNewMessagesFromChat() {
        if (game == null) {
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

        switch (removeResult) {
            case NOT_JOINED : {
                System.out.println("Session: removeBot warning. " +
                           "Tryed to remove bot from game, canceled." +
                           " Not joined to any game.");
                this.session.sendAnswer(protocol.notJoined(
                        ProtocolConstants.CAPTION_REMOVE_BOT_RESULT));

                return;
            }

            case NOT_OWNER_OF_GAME : {
                System.out.println("Session: removeBot warning. " +
                           "Tryed to remove bot from game, canceled." +
                           " Not owner of the game.");
                this.session.sendAnswer(protocol.notOK(
                        ProtocolConstants.CAPTION_REMOVE_BOT_RESULT,
                        "Not owner of game."));

                return;
            }

            case GAME_IS_ALREADY_STARTED : {
                System.out.println("Session: removeBot warning. " +
                           "Tryed to remove bot from game ,canceled. " +
                           "Game is already started.");
                this.session.sendAnswer(protocol.notOK(
                        ProtocolConstants.CAPTION_REMOVE_BOT_RESULT,
                        "Game was already started."));

                return;
            }

            case NO_SUCH_BOT : {
                System.out.println("Session: removeBot warning. " +
                           "Tryed to remove bot from game, canceled." +
                           "No bot to remove.");
                this.session.sendAnswer(protocol.notOK(
                        ProtocolConstants.CAPTION_REMOVE_BOT_RESULT,
                        "No bot to remove."));

                return;
            }

            case RESULT_SUCCESS : {
                System.out.println("Session: removed bot from game." +
                                   game.getName());
                this.session.sendAnswer(protocol.ok(
                        ProtocolConstants.CAPTION_REMOVE_BOT_RESULT,
                        "Bot removed."));

                return;
            }

            default : {
                assert false : "Session: removeBot error. Default block in switch " +
                           "statement. Error on server side.";
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

        if (this.game != null) {
            removeResult = CommandResult.GAME_IS_ALREADY_STARTED;

            if (!this.game.isStarted()) {
                removeResult = CommandResult.NOT_OWNER_OF_GAME;

                if (this.game.getOwner() == this) {
                    boolean removed = this.game.tryRemoveLastBot();
                    if(removed){
                        removeResult = CommandResult.RESULT_SUCCESS;
                    }else{
                        removeResult = CommandResult.NO_SUCH_BOT;
                    }
                }
            }
        }

        return removeResult;
    }

    public void sendGamePlayersStats() {
        if (game == null) {
            System.out.println("Session: sendGamePlayersStats warning. Canceled. " +
                               "Not joined to any game.");
            this.session.sendAnswer(protocol.notJoined(
                    ProtocolConstants.CAPTION_GAME_PLAYERS_STATS));

            return;
        }

        if (!game.isStarted()) {
            System.out.println("Session: sendGamePlayersStats warning. Canceled. " +
                           "Game is not started.");
            this.session.sendAnswer(protocol.notOK(
                    ProtocolConstants.CAPTION_GAME_PLAYERS_STATS,
                    "Game is not started. You can`t get stats."));

            return;
        }

        System.out.println("Session: sended gamePlayersStats to client.");
        this.session.sendAnswer(protocol.sendPlayersStats(game));
    }

    public void setClientName(String[] args) {// "17 name"
        if (args.length != 2) {
            System.out.println("Session: setClientName warning. Canceled. Wrong query.");
            this.session.sendAnswer(protocol.wrongQuery(
                    ProtocolConstants.CAPTION_SET_CLIENT_NAME,
                    "Wrong number of arguments."));
        } else {
            this.clientName = args[2];
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
    public String getName() {
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

    public ISession getSession() {
        return session;
    }

    private class MyTimer {
        private long startTime;

        public MyTimer(long startTime) {
            this.startTime = startTime;
        }

        public long getDiff() {
            return System.currentTimeMillis() - this.startTime;
        }

        public void setStartTime(long time) {
            this.startTime = time;
        }
    }
}