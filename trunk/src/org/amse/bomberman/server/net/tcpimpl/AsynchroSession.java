
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.control.Controller;
import org.amse.bomberman.server.net.ISession;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Constants.Direction;
import org.amse.bomberman.util.Creator;
import org.amse.bomberman.util.ILog;
import org.amse.bomberman.util.ProtocolConstants;
import org.amse.bomberman.util.Stringalize;

//~--- JDK imports ------------------------------------------------------------

import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.Socket;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kirilchuk V.E.
 */
public class AsynchroSession extends AbstractSession {
    private final MyTimer      timer = new MyTimer(System.currentTimeMillis());
    protected final Controller controller;

    public AsynchroSession(Server server, Socket clientSocket, int sessionID,
                           ILog log) {
        super(server, clientSocket, sessionID, log);
        this.controller = new Controller(server, this);
    }

    public void interruptSession() throws SecurityException {
        this.interrupt();

        try {
            this.clientSocket.close();
        } catch (IOException ex) {
            writeToLog("Session: interruptSession error. " + ex.getMessage());
        }
    }

    @Override
    public void notifyClient(String message) {
        this.sendAnswer(message);
    }

    protected void addBot(String[] queryArgs) {

        // "11" "botName"
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_JOIN_BOT_INFO);

        String botName = "defaultBot";

        if (queryArgs.length == 2) {
            botName = queryArgs[1];
        } else {
            messages.add("Wrong command parameters. Error on client side.");
            sendAnswer(messages);

            return;
        }

        int joinBotResult = this.controller.tryAddBotIntoMyGame(botName);

        switch (joinBotResult) {
            case Controller.NOT_JOINED : {

                // if gameParams==null true
                messages.add("Not joined to any game.");
                sendAnswer(messages);
                writeToLog("Session: addBot warning. " +
                           "Tryed to add bot to game, canceled." +
                           " Not joined to any game.");

                return;
            }

            case Controller.NOT_OWNER_OF_GAME : {

                // if not owner of gameParams
                messages.add("Not owner of game.");
                sendAnswer(messages);
                writeToLog("Session: addBot warning. " +
                           "Tryed to add bot to game, canceled." +
                           " Not owner of the game.");

                return;
            }

            case Controller.GAME_IS_FULL : {
                messages.add("Game is full. Try to add bot later.");
                sendAnswer(messages);
                writeToLog("Session: addBot warning. " +
                           "Tryed to add bot, canceled. Game is full.");

                return;
            }

            case Controller.GAME_IS_ALREADY_STARTED : {

                // if gameParams.isStarted() true
                messages.add("Game was already started.");
                sendAnswer(messages);
                writeToLog("Session: addbot warning. " +
                           "Tryed to add bot to game ,canceled." +
                           " Game is already started.");

                return;
            }

            case Controller.RESULT_SUCCESS : {
                messages.add("Bot added.");
                sendAnswer(messages);
                writeToLog("Session: added bot to game." +
                           controller.getMyGame().getName());

                return;
            }

            default : {
                messages.add("Error on server.");
                sendAnswer(messages);
                writeToLog("Session: addbot error. Default block in switch " +
                           "statement. Error on server side.");

                return;
            }
        }
    }

    protected void addMessageToChat(String[] queryArgs) {
        List<String> message = new ArrayList<String>(2);

        message.add(0, ProtocolConstants.CAPTION_SEND_CHAT_MSG_INFO);

        if (queryArgs.length >= 2) {
            Game game = this.controller.getMyGame();

            if (game != null) {
                StringBuilder chatMessage = new StringBuilder();

                for (int i = 1; i < queryArgs.length; ++i) {
                    chatMessage.append(queryArgs[i] + " ");
                }

                this.controller.addMessageToChat(chatMessage.toString());
                writeToLog("Session: client added message to game chat. message=" +
                           queryArgs[1]);

                return;
            } else {
                message.add("Not joined to any game.");
                sendAnswer(message);
                writeToLog("Session: addMessageToChat warning. " +
                           "Client tryed to add message, canceled. " +
                           "Not joined to any game.");

                return;
            }
        } else {
            message.add("Wrong query. Not enough arguments");
            sendAnswer(message);
            writeToLog("Session: addMessageToChat error. Client tryed to add message, canceled. Wrong query.");

            return;
        }
    }

    protected void createGame(String[] queryArgs) {

        // Example queryArgs = "1" "gameName" "mapName" "maxpl" "playerName"
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_CREATE_GAME);

        if (this.controller.getMyGame() != null) {
            messages.add("Leave another game first.");
            sendAnswer(messages);
            writeToLog("Session: createGame warning. Client tryed to join game, canceled. Already in other game.");

            return;
        }

        String gameName = null;
        String mapName = null;
        int    maxPlayers = -1;    // -1 for: defines by GameMap.
        String playerName = "defaultPlayer";

        if (queryArgs.length == 5) {    // if we getted command in full syntax
            gameName = queryArgs[1];
            mapName = queryArgs[2];
            playerName = queryArgs[4];
            if(playerName.length()>10){
                playerName = playerName.substring(0, 10);
            }

            try {
                maxPlayers = Integer.parseInt(queryArgs[3]);
            } catch (NumberFormatException ex) {
                messages.add("Wrong query parameters. Error on client side.");
                writeToLog("Session: createGame error. Client tryed to create game, canceled. " +
                           "Wrong command parameters. Error on client side. " +
                           ex.getMessage());
                sendAnswer(messages);

                return;
            }
        } else {    // if command have more or less arguments than must have.
            messages.add("Wrong query parameters. Error on client side.");
            writeToLog("Session: createGame error. Client tryed to create game, canceled. " +
                       "Wrong command parameters. Error on client side.");
            sendAnswer(messages);

            return;
        }

        try {
            this.controller.tryCreateGame(mapName, gameName, maxPlayers,
                                          playerName);
            messages.add("Game created.");
            sendAnswer(messages);

            return;
        } catch (FileNotFoundException ex) {
            messages.add("No such map on server.");
            sendAnswer(messages);
            writeToLog("Session: createGame warning. Client tryed to create game, canceled. " +
                       "Map wasn`t founded on server." + " Map=" + mapName);

            return;
        } catch (IOException ex) {
            messages.add("Error on server side, while loading map.");
            sendAnswer(messages);
            writeToLog("Session: createGame error while loadimg map. " +
                       " Map=" + mapName + " " + ex.getMessage());

            return;
        }
    }

    protected void doMove(String[] queryArgs) {
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_DO_MOVE);

        if (this.controller.getMyGame() != null) {
            if (timer.getDiff() > Constants.GAME_STEP_TIME) {
                int     dir = 0;
                boolean moved = false;

                try {
                    dir = Integer.parseInt(queryArgs[1]);    // throws NumberFormatException

                    Direction direction = Direction.fromInt(dir);    // throws IllegalArgumentException

                    moved = this.controller.tryDoMove(direction);
                } catch (NumberFormatException ex) {
                    messages.add("Wrong move value.");
                    sendAnswer(messages);
                    writeToLog("Session: doMove error. " +
                               "Unsupported direction(not int). " +
                               "Error on client side." + " direction=" +
                               queryArgs[1] + ex.getMessage());

                    return;
                } catch (IllegalArgumentException ex) {
                    messages.add("Wrong move value.");
                    sendAnswer(messages);
                    writeToLog("Session: doMove error. " +
                               "Unsupported direction. Error on client side." +
                               " direction=" + dir + " " + ex.getMessage());

                    return;
                }

                if (moved) {
                    timer.setStartTime(System.currentTimeMillis());
                }

                messages.add("" + moved);
                sendAnswer(messages);

                if (moved) {
                    this.timer.setStartTime(System.currentTimeMillis());
                }

                return;
            } else {    // timer.getDiff < gameStep true
                messages.add("false");
                sendAnswer(messages);
                writeToLog("Session: doMove warning. " +
                           "Client tryed to move, canceled. " +
                           "Moves allowed only every " +
                           Constants.GAME_STEP_TIME + "ms.");

                return;
            }
        } else {    // gameParams == null true
            messages.add("Not joined to any game.");
            sendAnswer(messages);
            writeToLog("Session: doMove warning. " +
                       "Client tryed to move, canceled. " +
                       "Not joined to any game.");

            return;
        }
    }

    protected void freeResources() {
        if (this.controller != null) {
            this.controller.tryLeaveGame();
        }

        this.server.sessionTerminated(this);
    }

    protected void getNewMessagesFromChat() {
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_GET_CHAT_MSGS);

        if (this.controller.getMyGame() != null) {
            List<String> toSend = this.controller.getNewMessagesFromChat();

            messages.addAll(toSend);
            sendAnswer(messages);
            writeToLog("Session: client getted new messages from chat. count=" +
                       toSend.size());

            return;
        } else {
            messages.add("Not joined to any game.");
            sendAnswer(messages);
            writeToLog("Session: getNewMessagesFromChat warning. Client tryed to get messages, canceled. Not joined to any game.");

            return;
        }
    }

    protected void joinGame(String[] queryArgs) {

        // "2" "gameID" "playerName"
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_JOIN_GAME);

        if (this.controller.getMyGame() != null) {
            messages.add("Leave another game first.");
            sendAnswer(messages);
            writeToLog("Session: joinGame warning. " +
                       "Client tryed to join game, canceled. " +
                       "Already in other game.");

            return;
        }

        int    gameID = 0;
        String playerName = null;

        if (queryArgs.length == 3) {    // if we getted command in full syntax
            try {
                gameID = Integer.parseInt(queryArgs[1]);
            } catch (NumberFormatException ex) {
                messages.add("Wrong command parameters. Error on client side." +
                             " gameID must be int.");
                sendAnswer(messages);
                writeToLog("Session: joinGame error. " +
                           " Wrong command parameters. " +
                           "Error on client side. gameID must be int. " +
                           ex.getMessage());

                return;
            }

            playerName = queryArgs[2];
            if(playerName.length() > 10){
                playerName = playerName.substring(0, 10);
            }
        } else {    // wrong syntax
            messages.add("Wrong query. Error on client side.");
            sendAnswer(messages);
            writeToLog("Session: joinGame error. Wrong command parameters. Error on client side.");

            return;
        }

        // all is ok
        int joinResult = this.controller.tryJoinGame(gameID, playerName);

        switch (joinResult) {
            case Controller.NO_SUCH_UNSTARTED_GAME : {

                // if no unstarted gameParams with such gameID finded
                messages.add("No such game.");
                sendAnswer(messages);
                writeToLog("Session: client tryed to join gameID=" + gameID +
                           " ,canceled." + " No such game on server.");

                return;
            }

            case Controller.GAME_IS_ALREADY_STARTED : {

                // if gameParams with such gameID already started
                messages.add("Game was already started.");
                sendAnswer(messages);
                writeToLog("Session: joinGame warning. Client tryed to join gameID=" +
                           gameID + " ,canceled." +
                           " Game is already started. ");

                return;
            }

            case Controller.GAME_IS_FULL : {
                messages.add("Game is full. Try to join later.");
                sendAnswer(messages);
                writeToLog("Session: joinGame warning. Client tryed to join to full game, canceled.");

                return;
            }

            case Controller.RESULT_SUCCESS : {
                messages.add("Joined.");
                sendAnswer(messages);
                writeToLog("Session: client joined to game." + " GameID=" +
                           gameID + " Player=" + playerName);

                return;
            }
        }
    }

    protected void leaveGame() {
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_LEAVE_GAME_INFO);

        Game game = this.controller.getMyGame();

        if (game != null) {
            this.controller.tryLeaveGame();
            messages.add("Disconnected.");
            sendAnswer(messages);
            writeToLog("Session: player has been disconnected from the game." +
                       " gameName=" + game.getName());

            return;
        } else {
            messages.add("Not joined to any game.");
            sendAnswer(messages);
            writeToLog("Session: leaveGame warning. Disconnect from game canceled. Not joined to any game.");

            return;
        }
    }

    protected void placeBomb() {
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_PLACE_BOMB_INFO);

        Game game = this.controller.getMyGame();

        if (game != null) {    // Always if gameParams!=null player is not null too!
            if (game.isStarted()) {
                this.controller.tryPlaceBomb();
                messages.add("Ok.");
                sendAnswer(messages);
                writeToLog("Session: tryed to plant bomb. " + "playerID=" +
                        this.controller.getPlayer().getID() + " " +
                        this.controller.getPlayer().getPosition().toString());

                return;
            } else {
                messages.add("Game is not started. Can`t place bomb.");
                sendAnswer(messages);
                writeToLog("Session: placew bomb warning. Cancelled, Game is not started.");

                return;
            }
        } else {
            messages.add("Not joined to any game.");
            sendAnswer(messages);
            writeToLog("Session: place bomb warning. Canceled. Not joined to any game.");

            return;
        }
    }

    protected void sendDownloadingGameMap(String[] queryArgs) {
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_DOWNLOAD_GAME_MAP);

        int[][] ret = null;
        String  mapFileName = queryArgs[1] + ".map";

        if (queryArgs.length == 2) {
            try {
                ret = Creator.createMapAndGetField(mapFileName);
            } catch (FileNotFoundException ex) {
                messages.add("No such map on server.");
                sendAnswer(messages);
                writeToLog("Session: sendMap warning. " +
                           "Client tryed to download map, canceled. " +
                           "Map wasn`t founded on server." + " Map=" +
                           mapFileName + " " + ex.getMessage());

                return;
            } catch (IOException ex) {
                messages.add("Error on server side, while loading map.");
                sendAnswer(messages);
                writeToLog("Session: sendMap error. " +
                           "Client tryed to download map, canceled. " +
                           "Error on server side while loading map." +
                           " Map=" + mapFileName + " " + ex.getMessage());

                return;
            }
        } else {    // if arguments!=2
            messages.add("Wrong query. Not enough arguments");
            sendAnswer(messages);
            writeToLog("Session: sendMap error. " +
                       "Client tryed to download gameMap, canceled." +
                       " Wrong query.");

            return;
        }

        // if all is OK.
        messages.addAll(Stringalize.field(ret));
        sendAnswer(messages);
        writeToLog("Session: client downloaded gameMap." + " GameMap=" +
                   mapFileName);
    }

    protected void sendGameInfo() {
        List<String> info = new ArrayList<String>();

        info.add(0, ProtocolConstants.CAPTION_GAME_INFO);

        Game game = this.controller.getMyGame();

        if (game != null) {
            info.addAll(Stringalize.gameInfoForClient(this.controller));
            sendAnswer(info);
            writeToLog("Session: sended gameInfo to client.");

            return;
        } else {
            info.add("Not joined to any game.");
            sendAnswer(info);
            writeToLog("Session: sendGameInfo warning. Client tryed to get game info, canceled. Not joined to any game.");

            return;
        }
    }

    protected void sendGameMapArray() {
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_GAME_MAP_INFO);

        Game game = this.controller.getMyGame();

        if (game != null) {
            List<String> linesToSend =
                Stringalize.fieldExplPlayerInfo(game,
                                              this.controller.getPlayer());

            messages.addAll(linesToSend);
            sendAnswer(messages);
            writeToLog("Session: sended mapArray+explosions+playerInfo to client.");

            return;
        } else {
            messages.add("Not joined to any game.");
            sendAnswer(messages);
            writeToLog("Session: sendMapArray warning. Canceled. Not joined to any game.");

            return;
        }
    }

    protected void sendGameMapsList() {
        List<String> messages = Stringalize.gameMapsList();

        messages.add(0, ProtocolConstants.CAPTION_GAME_MAPS_LIST);

        if (messages.size() > 1) {
            sendAnswer(messages);
            writeToLog("Session: sended maps list to client. Maps count=" +
                       (messages.size() - 1));

            return;
        } else {
            messages.add("No maps on server was founded.");
            sendAnswer(messages);
            writeToLog("Session: sendMapsList error. No maps founded on server.");

            return;
        }
    }

    protected void sendGameStatus() {
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_GAME_STATUS_INFO);

        Game game = this.controller.getMyGame();

        if (game != null) {
            String ret = Stringalize.gameStartStatus(game);

            messages.add(ret);
            sendAnswer(messages);
            writeToLog("Session: sended game status to client.");

            return;
        } else {
            messages.add("Not joined to any game.");
            sendAnswer(messages);
            writeToLog("Session: sendGameStatus warning. Canceled. Not joined to any game.");

            return;
        }
    }

    protected void sendGames() {
        List<String> linesToSend =
            Stringalize.unstartedGames(this.server.getGamesList());

        linesToSend.add(0, ProtocolConstants.CAPTION_GAMES_LIST);

        if (linesToSend.size() == 1) {    // only ProtocolConstants.CAPTION_GAMES_LIST phraze
            linesToSend.add("No unstarted games finded.");
            sendAnswer(linesToSend);
            writeToLog("Session: client tryed to get games list. No unstarted games finded.");

            return;
        } else {
            sendAnswer(linesToSend);
            writeToLog("Session: sended games list to client.");

            return;
        }
    }

    protected void startGame() {
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_START_GAME_INFO);

        Game game = this.controller.getMyGame();

        if (game != null) {
            if (!game.isStarted()) {
                boolean success = this.controller.tryStartGame();

                if (success) {

                  messages.add("Game started.");
                  sendAnswer(messages);
                  writeToLog("Session: started game. " + "(gameName=" +
                             this.controller.getMyGame().getName() + ")");
                    return;
                } else {
                    messages.add("Not owner of game.");
                    sendAnswer(messages);
                    writeToLog("Session: startGame warning. " +
                               "Client tryed to start game, canceled. " +
                               "Not an owner.");

                    return;
                }
            } else {    // if gameParams.isStarted() true
                messages.add("Game is already started.");
                sendAnswer(messages);
                writeToLog("Session: startGame warning. " +
                           "Client tryed to start started game. " +
                           "Canceled.");

                return;
            }
        } else {    // gameParams == null true
            messages.add("Not joined to any game.");
            sendAnswer(messages);
            writeToLog("Session: client tryed to start game, canceled. " +
                       "Not joined to any game.");

            return;
        }
    }

    protected void sendGameMapArray2() {
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_GAME_MAP_INFO);

        Game game = this.controller.getMyGame();

        if (game != null) {
            List<String> linesToSend =
                Stringalize.fieldExplPlayerInfo2(game,
                                               this.controller.getPlayer());

            messages.addAll(linesToSend);
            sendAnswer(messages);
            writeToLog("Session: sended mapArray+explosions+playerInfo to client.");

            return;
        } else {
            messages.add("Not joined to any game.");
            sendAnswer(messages);
            writeToLog("Session: sendMapArray warning. Canceled. Not joined to any game.");

            return;
        }
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
