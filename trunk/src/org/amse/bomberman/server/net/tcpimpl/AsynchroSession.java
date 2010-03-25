
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.bot.Bot;
import org.amse.bomberman.server.gameinit.control.Controller;
import org.amse.bomberman.server.net.IServer;
import org.amse.bomberman.server.net.ISession;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Constants.Command;
import org.amse.bomberman.util.Constants.Direction;
import org.amse.bomberman.util.Creator;
import org.amse.bomberman.util.ILog;
import org.amse.bomberman.util.ProtocolConstants;
import org.amse.bomberman.util.Stringalize;

//~--- JDK imports ------------------------------------------------------------

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.net.Socket;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kirilchuk V.E.
 */
public class AsynchroSession extends Thread implements ISession {
    protected ILog log = null;    // it can be null. So use writeToLog() instead of log.println()
    protected final MyTimer    timer = new MyTimer(System.currentTimeMillis());
    protected final Socket     clientSocket;
    protected final Controller controller;
    protected final IServer    server;
    protected final int        sessionID;

    public AsynchroSession(Server server, Socket clientSocket, int sessionID,
                           ILog log) {
        this.setDaemon(true);
        this.server = server;
        this.clientSocket = clientSocket;
        this.sessionID = sessionID;
        this.log = log;
        this.controller = new Controller(server, this);
    }

    @Deprecated
    protected void addBot(String[] queryArgs) {    // TODO write for asynchro

//
//      // "11" "gameID" "botName"
//      int    gameID = 0;
//      String botName = "defaultBot";
//
//      switch (queryArgs.length) {
//          case 3 : {    // if we getted command in full syntax
//              try {
//                  gameID = Integer.parseInt(queryArgs[1]);
//              } catch (NumberFormatException ex) {
//                  sendAnswer("Wrong command parameters. Error on client side." +
//                             " gameID must be int.");
////                  writeToLog("Session: addBot error. " +
////                             " Wrong command parameters. " +
////                             "Error on client side. gameID must be int. " +
////                             ex.getMessage());
//              }
//
//              botName = queryArgs[2];
//
//              break;
//          }
//
//          default : {    // wrong syntax
//              sendAnswer("Wrong query. Error on client side.");
////              writeToLog("Session: addBot error. Wrong command parameters. Error on client side.");
//
//              break;
//          }
//      }
//
//      Game gameToJoin = server.getGame(gameID);
//
//      if (gameToJoin != null) {
//          if (!gameToJoin.isStarted()) {
////              Bot bot = gameToJoin.joinBot(botName);
//
////              if (bot == null) {    // if game is full
//                  sendAnswer("Game is full. Try to add bot later.");
////                  writeToLog("Session: addBot warning. Tryed to add bot, canceled. Game is full.");
//
//                  return;
//              } else {
//
//                  // this.game = gameToJoin;
//                  sendAnswer("Bot added.");
//
//                  List<ISession> sessionsToNotify = this.game.getSessions();
//                  List<String>   messages = new ArrayList<String>(1);
//
//                  messages.add(0, "Update game info.");
//                  this.server.notifySomeClients(sessionsToNotify, messages);
//                  messages.clear();
//                  messages.add("Update games list.");
//                  this.server.notifyAllClients(messages);
//                  writeToLog("Session: added bot to game." + " GameID=" +
//                             gameID + " Player=" + botName);
//
//                  return;
//              }
//          } else {    // if game.isStarted() true
//              sendAnswer("Game was already started.");
//              writeToLog("Session: addbot warning. Tryed to add bot to game(gameID=" +
//                         gameID + ") ,canceled." +
//                         " Game is already started.");
//
//              return;
//          }
////      } else {    // if game==null true
//          sendAnswer("No such game.");
//          writeToLog("Session: addBot warning. Tryed to add bot to game(gameID=" +
//                     gameID + ") ,canceled." + " No such game.");
//
//          return;
//      }
    }

    @Deprecated    // TODO write for asynchro
    protected void addMessageToChat(String[] queryArgs) {

//      if (queryArgs.length >= 2) {
//          if (this.game != null) {
//              StringBuilder message = new StringBuilder();
//
//              for (int i = 1; i < queryArgs.length; ++i) {
//                  message.append(queryArgs[i] + " ");
//              }
//
//              this.game.addMessageToChat(this.player, message.toString());
//
//              List<String> toSend =
//                  this.game.getNewMessagesFromChat(this.player);
//
//              sendAnswer(toSend);
//              writeToLog("Session: client added message to game chat. message=" +
//                         queryArgs[1]);
//
//              return;
//          } else {
//              sendAnswer("Not joined to any game.");
//              writeToLog("Session: addMessageToChat warning. Client tryed to add message, canceled. Not joined to any game.");
//
//              return;
//          }
//      } else {
//          sendAnswer("Wrong query. Not enough arguments");
//          writeToLog("Session: addMessageToChat error. Client tryed to add message, canceled. Wrong query.");
//
//          return;
//      }
    }

    protected void answerOnCommand(String query) {
        writeToLog("Session: query received. query=" + query);

        if (query.length() == 0) {
            sendAnswer("Empty query. Error on client side.");
            writeToLog("Session: answerOnCommand warning. " +
                       "Empty query received. Error on client side.");

            return;
        }

        Command  cmd = null;
        String[] queryArgs = query.split(" ");

        try {
            int command = Integer.parseInt(queryArgs[0]);

            cmd = Command.fromInt(command);    // throws IllegalArgumentException
        } catch (NumberFormatException ex) {
            sendAnswer("Wrong query.");
            writeToLog("Session: answerOnCommand error. " +
                       "Wrong first part of query. " +
                       "Wrong query from client. " + ex.getMessage());

            return;
        } catch (IllegalArgumentException ex) {
            sendAnswer("Wrong query. Not supported command.");
            writeToLog("Session: answerOnCommand error. " +
                       "Non supported command int from client. " +
                       ex.getMessage());

            return;
        }

        switch (cmd) {
            case GET_GAMES : {

                // "0"
                sendGames();

                break;
            }

            case CREATE_GAME : {

                // "1 gameName mapName maxPlayers" or just "1" for defaults
                createGame(queryArgs);

                break;
            }

            case JOIN_GAME : {

                // "2 gameID botName"
                joinGame(queryArgs);

                break;
            }

            case DO_MOVE : {

                // "3 direction"
                doMove(queryArgs);

                break;
            }

            case GET_GAME_MAP_INFO : {

                // "4"
                sendGameMapArray();

                break;
            }

            case START_GAME : {

                // "5"
                startGame();

                break;
            }

            case LEAVE_GAME : {

                // "6"
                leaveGame();

                break;
            }

            case PLACE_BOMB : {

                // "7"
                placeBomb();

                break;
            }

            case DOWNLOAD_GAME_MAP : {

                // "8 mapName"
                sendDownloadingGameMap(queryArgs);

                break;
            }

            case GET_GAME_STATUS : {

                // "9"
                sendGameStatus();

                break;
            }

            case GET_GAME_MAPS_LIST : {

                // "10"
                sendGameMapsList();

                break;
            }

            case ADD_BOT_TO_GAME : {

                // "11 gameID botName"
                addBot(queryArgs);

                break;
            }

            case GET_MY_GAME_INFO : {

                // "12"
                sendGameInfo();

                break;    // TODO
            }

            case CHAT_ADD_MSG : {

                // "13 message"
                addMessageToChat(queryArgs);

                break;    // TODO
            }

            case CHAT_GET_NEW_MSGS : {

                // "14"
                getNewMessagesFromChat();

                break;    // TODO
            }

            case GET_GAME_MAP_INFO2 : {

                // "15"
                sendGameMapArray2();

                break;
            }

            default : {
                sendAnswer("Unrecognized command!");
                writeToLog("Session: answerOnCommand error." +
                           " Getted unrecognized command.");
            }
        }
    }

    protected void createGame(String[] queryArgs) {

        // Example queryArgs = "1" "gameName" "mapName" "maxpl"
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

        if (queryArgs.length == 4) {    // if we getted command in full syntax
            gameName = queryArgs[1];
            mapName = queryArgs[2];

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
            this.controller.tryCreateGame(mapName, gameName, maxPlayers);
            messages.add("Game created.");
            sendAnswer(messages);

            // notifying others
            messages.clear();
            messages.add(ProtocolConstants.UPDATE_GAMES_LIST +
                         " New game was created.");
            this.server.notifyAllClients(messages);
            writeToLog("Session: client created game." + " Map=" + mapName +
                       " gameName=" + gameName + " maxPlayers=" + maxPlayers);

            return;
        } catch (FileNotFoundException ex) {
            sendAnswer("No such map on server.");
            writeToLog("Session: createGame warning. Client tryed to create game, canceled. " +
                       "Map wasn`t founded on server." + " Map=" + mapName);

            return;
        } catch (IOException ex) {
            sendAnswer("Error on server side, while loading map.");
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
                    writeToLog("Session: doMove error. Unsupported direction(not int). Error on client side." +
                               " direction=" + queryArgs[1]);

                    return;
                } catch (IllegalArgumentException ex) {
                    sendAnswer("Wrong move value.");
                    writeToLog("Session: doMove error. Unsupported direction. Error on client side." +
                               " direction=" + dir);

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
                sendAnswer("false");
                writeToLog("Session: doMove warning. Client tryed to move, canceled. Moves allowed only every " +
                           Constants.GAME_STEP_TIME + "ms.");

                return;
            }
        } else {    // game == null true
            sendAnswer("Not joined to any game.");
            writeToLog("Session: doMove warning. Client tryed to move, canceled. Not joined to any game.");

            return;
        }
    }

    private boolean filtred(String message) {

//      if (message.startsWith("Session")) {
//          return true;
//      }
        return false;
    }

    protected void freeResources() throws IOException {
        if (this.controller != null) {
            this.controller.leaveGame();
        }

        this.server.sessionTerminated(this);
    }

    @Deprecated    // write for asynchro
    public void gameMapChanged() {

//      if (this.game != null) {
//          List<String>   messages = new ArrayList<String>();
//          List<ISession> sessionsToNotify = this.game.getSessions();
//
//          messages.add(ProtocolConstants.UPDATE_GAME_MAP);
//          this.server.notifySomeClients(sessionsToNotify, messages);
//      } else {
//          writeToLog("Session: GAME MAP CHANGED CALLED. BUT GAME=NULL. FIX PLZ.");
//      }
    }

    @Deprecated    // TODO write for asynchro
    protected void getNewMessagesFromChat() {

//      if (this.game != null) {
//          List<String> toSend = this.game.getNewMessagesFromChat(this.player);
//
//          sendAnswer(toSend);
//          writeToLog("Session: client getted new messages from chat. count=" +
//                     toSend.size());
//
//          return;
//      } else {
//          sendAnswer("Not joined to any game.");
//          writeToLog("Session: getNewMessagesFromChat warning. Client tryed to get messages, canceled. Not joined to any game.");
//
//          return;
//      }
    }

    public void interruptSession() throws SecurityException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Deprecated    // not fully work
    protected void joinGame(String[] queryArgs) {

        // "2" "gameID" "playerName"
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_JOIN_GAME);

        if (this.controller.getMyGame() != null) {
            messages.add("Leave another game first.");
            sendAnswer(messages);
            writeToLog("Session: joinGame warning. Client tryed to join game, canceled. Already in other game.");

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
        } else {    // wrong syntax
            messages.add("Wrong query. Error on client side.");
            sendAnswer(messages);
            writeToLog("Session: joinGame error. Wrong command parameters. Error on client side.");

            return;
        }

        // all is ok
        int joinResult = this.controller.tryJoinGame(gameID, playerName);

        switch (joinResult) {
            case -2 : {

                // if no unstarted game with such gameID finded
                messages.add("No such game.");
                sendAnswer(messages);
                writeToLog("Session: client tryed to join gameID=" + gameID +
                           " ,canceled." + " No such game on server.");

                return;
            }

            case -1 : {

                // if game with such gameID already started
                messages.add("Game was already started.");
                sendAnswer(messages);
                writeToLog("Session: joinGame warning. Client tryed to join gameID=" +
                           gameID + " ,canceled." +
                           " Game is already started. ");

                return;
            }

            case 0 : {
                messages.add("Game is full. Try to join later.");
                sendAnswer(messages);
                writeToLog("Session: joinGame warning. Client tryed to join to full game, canceled.");

                return;
            }

            case 1 : {
                messages.add("Joined.");
                sendAnswer(messages);

                // TODO getSESSIONS IN CONTROLLER AND GAME!!!!!!!!!
//              List<ISession> sessionsToNotify = this.controller.getSessions();
//
//              messages = new ArrayList<String>(1);
//              messages.add(0, ProtocolConstants.UPDATE_GAME_INFO);
//              this.server.notifySomeClients(sessionsToNotify, messages);
                List<String> messages2 = new ArrayList<String>(1);

                messages2.add(0, ProtocolConstants.UPDATE_GAMES_LIST);
                this.server.notifyAllClients(messages2);
                writeToLog("Session: client joined to game." + " GameID=" +
                           gameID + " Player=" + playerName);

                return;
            }
        }
    }

    @Deprecated    // not fully work
    protected void leaveGame() {
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_LEAVE_GAME_INFO);

        Game game = this.controller.getMyGame();

        if (game != null) {
            this.controller.leaveGame();
            messages.add("Disconnected.");
            sendAnswer(messages);

//          List<ISession> sessionsToNotify = temp.getSessions();
//          if (temp.isStarted()) {
//              ;    // do nothing
//          } else {
//              messages.clear();
//              messages.add(ProtocolConstants.UPDATE_GAME_INFO);
//              this.server.notifySomeClients(sessionsToNotify, messages);
//          }
            messages.clear();
            messages.add(ProtocolConstants.UPDATE_GAMES_LIST);
            this.server.notifyAllClients(messages);
            writeToLog("Session: player has been disconnected from the game.");

            return;
        } else {
            sendAnswer("Not joined to any game.");
            writeToLog("Session: leaveGame warning. Disconnect from game canceled. Not joined to any game.");

            return;
        }
    }

    protected void placeBomb() {
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_PLACE_BOMB_INFO);

        Game game = this.controller.getMyGame();

        if (game != null) {    // Always if game!=null player is not null too!
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
                writeToLog("Session: placew bomb warning. Cancelled, Game is not started.");
            }
        } else {
            sendAnswer("Not joined to any game.");
            writeToLog("Session: place bomb warning. Canceled. Not joined to any game.");

            return;
        }
    }

    public void sendAnswer(List<String> linesToSend)
                                    throws IllegalArgumentException {
        BufferedWriter out = null;

        try {
            OutputStream os = this.clientSocket.getOutputStream();    // throws IOException
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

            out = new BufferedWriter(osw);
            writeToLog("Session: sending answer...");

            if ((linesToSend == null) || (linesToSend.size() == 0)) {
                writeToLog("Session: sendAnswer error. Realization error." +
                           " Tryed to send 0 strings to client.");

                throw new IllegalArgumentException("Session sendAnswer method must send" +
                                                   " at least one string to client.");
            } else {
                for (String string : linesToSend) {
                    out.write(string);
                    out.newLine();
                }
            }

            out.write("");
            out.newLine();
            out.flush();
        } catch (IOException ex) {
            writeToLog("Session: sendAnswer error. " + ex.getMessage());
        }
    }

    public void sendAnswer(String shortAnswer) {
        BufferedWriter out = null;

        try {
            OutputStream       os = this.clientSocket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

            out = new BufferedWriter(osw);
            writeToLog("Session: sending answer...");
            out.write(shortAnswer);
            out.newLine();
            out.write("");
            out.newLine();
            out.flush();
        } catch (IOException ex) {
            writeToLog("Session: sendAnswer error. " + ex.getMessage());
        }
    }

    protected void sendDownloadingGameMap(String[] queryArgs) {
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_DOWNLOAD_GAME_MAP);

        int[][] ret = null;
        String  mapFileName = queryArgs[1] + ".map";

        if (queryArgs.length == 2) {
            try {
                ret = Creator.createMapAndGetArray(mapFileName);
            } catch (FileNotFoundException ex) {
                sendAnswer("No such map on server.");
                writeToLog("Session: sendMap warning. Client tryed to download map, canceled. " +
                           "Map wasn`t founded on server." + " Map=" +
                           mapFileName + " " + ex.getMessage());

                return;
            } catch (IOException ex) {
                sendAnswer("Error on server side, while loading map.");
                writeToLog("Session: sendMap error. Client tryed to download map, canceled. " +
                           "Error on server side while loading map." +
                           " Map=" + mapFileName + " " + ex.getMessage());

                return;
            }
        } else {    // if arguments!=2
            sendAnswer("Wrong query. Not enough arguments");
            writeToLog("Session: sendMap error. Client tryed to download map, canceled. Wrong query.");

            return;
        }

        // if all is OK.
        messages.addAll(Stringalize.map(ret));
        sendAnswer(messages);
        writeToLog("Session: client downloaded map." + " Map=" + mapFileName);
    }

    protected void sendGameInfo() {
        List<String> info = new ArrayList<String>();

        info.add(0, ProtocolConstants.CAPTION_GAME_INFO);

        Game game = this.controller.getMyGame();

        if (game != null) {
            info.addAll(Stringalize.gameInfo(game, this.controller));
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
                Stringalize.mapExplPlayerInfo(game,
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

    @Deprecated    // write for asynchro
    private void sendGameMapArray2() {

//      Game game = this.controller.getMyGame();
//
//      if (game != null) {
//          List<String> linesToSend =
//              Stringalize.mapExplPlayerInfo2(game,
//                                            this.controller.getPlayer());
//
//          sendAnswer(linesToSend);
//          writeToLog("Session: sended mapArray+explosions+playerInfo" +
//                     " to client.");
//
//          return;
//      } else {
//          sendAnswer("Not joined to any game.");
//          writeToLog("Session: sendMapArray warning. " +
//                     "Canceled. Not joined to any game.");
//
//          return;
//      }
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
            String ret = Stringalize.gameStatus(game);

            messages.add(ret);
            sendAnswer(messages);
            writeToLog("Session: sended game status to client.");

            return;
        } else {
            sendAnswer("Not joined to any game.");
            writeToLog("Session: sendGameStatus warning. Canceled. Not joined to any game.");

            return;
        }
    }

    protected void sendGames() {
        List<String> linesToSend =
            Stringalize.unstartedGames(this.server.getGamesList());

        linesToSend.add(0, ProtocolConstants.CAPTION_GAMES_LIST);

        if (linesToSend.size() == 1) {    // only "Games list." phraze
            linesToSend.add("No unstarted games finded.");
            this.sendAnswer(linesToSend);
            writeToLog("Session: client tryed to get games list. No unstarted games finded.");

            return;
        } else {
            this.sendAnswer(linesToSend);
            writeToLog("Session: sended games list to client.");

            return;
        }
    }

    @Deprecated    // write for asynchro
    protected void startGame() {

//      List<String> messages = new ArrayList<String>();
//
//      messages.add(0, ProtocolConstants.CAPTION_START_GAME_INFO);
//
//      Game game = this.controller.getMyGame();
//
//      if (game != null) {
//          if (!game.isStarted()) {
//              boolean success = this.controller.tryStartGame();
//
////                  List<ISession> sessionsToNotify = this.game.getSessions();
//
////                  messages.add("Game started.");
////                  this.server.notifySomeClients(sessionsToNotify, messages);
////                  writeToLog("Session: started game. " + "(gameName=" +
////                             this.game.getName() + ")");
////
////                  return;
//              } else {    // if player not owner of game
//                  sendAnswer("Not owner of game.");
//                  writeToLog("Session: startGame warning. Client tryed to start game, canceled. Not an owner.");
//              }
//          } else {    // if game.isStarted() true
//              sendAnswer("Game is already started.");
//              writeToLog("Session: startGame warning. Client tryed to start started game. Canceled.");
//
//              return;
//          }
//      } else {    // game == null true
//          sendAnswer("Not joined to any game.");
//          writeToLog("Session: client tryed to start game, canceled. Not joined to any game.");
//
//          return;
//      }
    }

    protected void writeToLog(String message) {
        if ((this.log != null) &&!filtred(message)) {
            this.log.println(message + "(sessionID=" + sessionID + ")");
        }
    }

    protected class MyTimer {
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
