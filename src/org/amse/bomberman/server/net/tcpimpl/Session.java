
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.control.Controller;
import org.amse.bomberman.server.net.IServer;
import org.amse.bomberman.server.net.ISession;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Constants.Command;
import org.amse.bomberman.util.Constants.Direction;
import org.amse.bomberman.util.Creator;
import org.amse.bomberman.util.ILog;
import org.amse.bomberman.util.Stringalize;

//~--- JDK imports ------------------------------------------------------------

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import java.util.List;

/**
 *
 * @author Kirilchuk V.E
 */
public class Session extends Thread implements ISession {
    protected ILog log = null;    // it can be null. So use writeToLog() instead of log.println()
    protected final MyTimer    timer = new MyTimer(System.currentTimeMillis());
    protected final Socket     clientSocket;
    protected final Controller controller;
    protected final IServer    server;
    protected final int        sessionID;

    public Session(Server server, Socket clientSocket, int sessionID,
                   ILog log) {
        this.setDaemon(true);
        this.server = server;
        this.clientSocket = clientSocket;
        this.sessionID = sessionID;
        this.log = log;
        this.controller = new Controller(server, this);
    }

    protected void addBot(String[] queryArgs) {    // migrating version need remove case 3

        // "11" "gameID" "botName"
        int    gameID = 0;
        String botName = "defaultBot";

        switch (queryArgs.length) {
            case 2 : {    // to support command in "short" syntax when bot name is ommited
                try {
                    botName = queryArgs[1];
                } catch (NumberFormatException ex) {
                    sendAnswer("Wrong command parameters. Error on client side." +
                               " gameID must be int.");
                    writeToLog("Session: addBot error. " +
                               " Wrong command parameters. " +
                               "Error on client side. gameID must be int. " +
                               ex.getMessage());
                }

                break;
            }

            case 3 : {    // if we getted command in full syntax
                try {
                    gameID = Integer.parseInt(queryArgs[1]);
                } catch (NumberFormatException ex) {
                    sendAnswer("Wrong command parameters. " +
                               "Error on client side." +
                               " gameID must be int.");
                    writeToLog("Session: addBot error. " +
                               " Wrong command parameters. " +
                               "Error on client side. gameID must be int. " +
                               ex.getMessage());
                }

                botName = queryArgs[2];

                break;
            }

            default : {    // wrong syntax
                sendAnswer("Wrong query. Error on client side.");
                writeToLog("Session: addBot error. " +
                           "Wrong command parameters. Error on client side.");

                break;
            }
        }

        int joinResult = this.controller.tryAddBotIntoMyGame(botName);

        switch (joinResult) {
            case -2 : {

                // if game==null true
                sendAnswer("Not joined to any game.");
                writeToLog("Session: addBot warning. " +
                           "Tryed to add bot to game(gameID=" + gameID + ")" +
                           " ,canceled." + " No such game.");

                return;
            }

            case -1 : {

                // if game.isStarted() true
                sendAnswer("Game was already started.");
                writeToLog("Session: addbot warning. " +
                           "Tryed to add bot to game(gameID=" + gameID + ")" +
                           " ,canceled." + " Game is already started.");

                return;
            }

            case 0 : {

                // if game is full
                sendAnswer("Game is full. Try to add bot later.");
                writeToLog("Session: addBot warning. " +
                           "Tryed to add bot, canceled. Game is full.");

                return;
            }

            case 1 : {
                sendAnswer("Bot added.");
                writeToLog("Session: added bot to game." + " GameID=" +
                           gameID + " Player=" + botName);

                return;
            }
        }
    }

    protected void addMessageToChat(String[] queryArgs) {
        if (queryArgs.length >= 2) {
            if (this.controller.getMyGame() != null) {
                StringBuilder message = new StringBuilder();

                for (int i = 1; i < queryArgs.length; ++i) {
                    message.append(queryArgs[i] + " ");
                }

                this.controller.addMessageToChat(message.toString());

                List<String> toSend = this.controller.getNewMessagesFromChat();

                sendAnswer(toSend);
                writeToLog("Session: client added message to game chat. message=" +
                           queryArgs[1]);

                return;
            } else {
                sendAnswer("Not joined to any game.");
                writeToLog("Session: addMessageToChat warning. " +
                           "Client tryed to add message, canceled. " +
                           "Not joined to any game.");

                return;
            }
        } else {
            sendAnswer("Wrong query. Not enough arguments");
            writeToLog("Session: addMessageToChat error. " +
                       "Client tryed to add message, canceled. " +
                       "Wrong query.");

            return;
        }
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
        String gameName = "defaultGameName";
        String mapName = "1";
        int    maxPlayers = -1;    // -1 for: defines by GameMap.

        if (queryArgs.length == 4) {    // if we getted command in full syntax
            gameName = queryArgs[1];
            mapName = queryArgs[2];

            try {
                maxPlayers = Integer.parseInt(queryArgs[3]);
            } catch (NumberFormatException ex) {
                sendAnswer("Wrong query parameters. Error on client side.");
                writeToLog("Session: createGame error. " +
                           "Client tryed to create game, canceled. " +
                           "Wrong command parameters. Error on client side. " +
                           ex.getMessage());

                return;
            }
        } else {    // if command have more or less arguments than must have.
            sendAnswer("Wrong query parameters. Error on client side.");
            writeToLog("Session: createGame error. " +
                       "Client tryed to create game, canceled. " +
                       "Wrong command parameters. Error on client side.");

            return;
        }

        try {
            this.controller.tryCreateGame(mapName, gameName, maxPlayers);
            sendAnswer("Game created.");
            writeToLog("Session: client created game." + " Map=" + mapName +
                       " gameName=" + gameName + " maxPlayers=" + maxPlayers);

            return;
        } catch (FileNotFoundException ex) {
            sendAnswer("No such map on server.");
            writeToLog("Session: createGame warning. " +
                       "Client tryed to create game, canceled. " +
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
        if (this.controller.getMyGame() != null) {
            if (timer.getDiff() > Constants.GAME_STEP_TIME) {
                int     dir = 0;
                boolean moved = false;

                try {
                    dir = Integer.parseInt(queryArgs[1]);    // throws NumberFormatException

                    Direction direction = Direction.fromInt(dir);    // throws IllegalArgumentException

                    moved = this.controller.tryDoMove(direction);
                } catch (NumberFormatException ex) {
                    writeToLog("Session: doMove error. " +
                               "Unsupported direction(not int). " +
                               "Error on client side." + " direction=" +
                               queryArgs[1]);
                } catch (IllegalArgumentException ex) {
                    writeToLog("Session: doMove error. " +
                               "Unsupported direction. " +
                               "Error on client side." + " direction=" + dir);
                }

                if (moved) {
                    timer.setStartTime(System.currentTimeMillis());
                }

                sendAnswer("" + moved);

                return;
            } else {    // timer.getDiff < gameStep true
                sendAnswer("false");
                writeToLog("Session: doMove warning. " +
                           "Client tryed to move, canceled. " +
                           "Moves allowed only every " +
                           Constants.GAME_STEP_TIME + "ms.");

                return;
            }
        } else {    // game == null true
            sendAnswer("Not joined to any game.");
            writeToLog("Session: doMove warning. " +
                       "Client tryed to move, canceled. " +
                       "Not joined to any game.");

            return;
        }
    }

    private boolean filtred(String message) {
        if (message.startsWith("Session")) {
            return true;
        }

        return false;
    }

    protected void freeResources() throws IOException {
        if (this.controller != null) {
            this.controller.leaveGame();
        }

        this.server.sessionTerminated(this);
    }

    public void gameMapChanged() {
        ;    // do nothing in current implementation
    }

    protected void getNewMessagesFromChat() {
        if (this.controller.getMyGame() != null) {
            List<String> toSend = this.controller.getNewMessagesFromChat();

            sendAnswer(toSend);
            writeToLog("Session: client getted new messages from chat. " +
                       "count=" + toSend.size());

            return;
        } else {
            sendAnswer("Not joined to any game.");
            writeToLog("Session: getNewMessagesFromChat warning. " +
                       "Client tryed to get messages, canceled. " +
                       "Not joined to any game.");

            return;
        }
    }

    public void interruptSession() throws SecurityException {
        this.interrupt();

        try {
            this.clientSocket.close();
        } catch (IOException ex) {
            writeToLog("Session: interruptSession error. " + ex.getMessage());
        }
    }

    protected void joinGame(String[] queryArgs) {

        // "2" "gameID" "playerName"
        int    gameID = 0;
        String playerName = "defaultPlayer";

        switch (queryArgs.length) {
            case 2 : {    // to support command in "short" syntax when bot name is ommited
                try {
                    gameID = Integer.parseInt(queryArgs[1]);
                } catch (NumberFormatException ex) {
                    sendAnswer("Wrong command parameters. Error on client side." +
                               " gameID must be int.");
                    writeToLog("Session: joinGame error. " +
                               " Wrong command parameters. " +
                               "Error on client side. gameID must be int. " +
                               ex.getMessage());
                }

                break;
            }

            case 3 : {    // if we getted command in full syntax
                try {
                    gameID = Integer.parseInt(queryArgs[1]);
                } catch (NumberFormatException ex) {
                    sendAnswer("Wrong command parameters. Error on client side." +
                               " gameID must be int.");
                    writeToLog("Session: joinGame error. " +
                               " Wrong command parameters. " +
                               "Error on client side. gameID must be int. " +
                               ex.getMessage());
                }

                playerName = queryArgs[2];

                break;
            }

            default : {    // wrong syntax
                sendAnswer("Wrong query. Error on client side.");
                writeToLog("Session: joinGame error. " +
                           "Wrong command parameters. Error on client side.");

                break;
            }
        }

        // all is ok
        int joinResult = this.controller.tryJoinGame(gameID, playerName);

        switch (joinResult) {
            case -2 : {

                // if no unstarted game with such gameID finded
                sendAnswer("No such game.");
                writeToLog("Session: client tryed to join " + "gameID=" +
                           gameID + " ,canceled." + " No such game on server.");

                return;
            }

            case -1 : {

                // if game with such gameID already started
                sendAnswer("Game was already started.");
                writeToLog("Session: joinGame warning. " +
                           "Client tryed to join gameID=" + gameID +
                           " ,canceled." + " Game is already started. ");

                return;
            }

            case 0 : {
                sendAnswer("Game is full. Try to join later.");
                writeToLog("Session: joinGame warning. " +
                           "Client tryed to join to full game, canceled.");

                return;
            }

            case 1 : {
                sendAnswer("Joined.");
                writeToLog("Session: client joined to game." + " GameID=" +
                           gameID + " Player=" + playerName);

                return;
            }
        }
    }

    protected void leaveGame() {
        Game game = this.controller.getMyGame();

        if (game != null) {
            this.controller.leaveGame();
            sendAnswer("Disconnected.");
            writeToLog("Session: player has been disconnected from the game.");

            return;
        } else {
            sendAnswer("Not joined to any game.");
            writeToLog("Session: leaveGame warning. " +
                       "Disconnect from game canceled. " +
                       "Not joined to any game.");

            return;
        }
    }

    protected void placeBomb() {
        Game game = this.controller.getMyGame();

        if (game != null) {
            if (game.isStarted()) {
                this.controller.tryPlaceBomb();
                sendAnswer("Ok.");
                writeToLog("Session: tryed to plant bomb. " + "playerID=" +
                        this.controller.getPlayer().getID() + " " +
                        this.controller.getPlayer().getPosition().toString());

                return;
            } else {
                sendAnswer("Game is not started. Can`t place bomb.");    // TODO add this to protocol!!!
                writeToLog("Session: placew bomb warning. " +
                           "Cancelled, Game is not started.");

                return;
            }
        } else {
            sendAnswer("Not joined to any game.");
            writeToLog("Session: place bomb warning. " +
                       "Canceled. Not joined to any game.");

            return;
        }
    }

    @Override
    public void run() {
        BufferedReader in = null;

        try {
            this.clientSocket.setSoTimeout(Constants.DEFAULT_CLIENT_TIMEOUT);    // throws SocketException
        } catch (SocketException ex) {
            writeToLog("Session: exception in run method. " + ex.getMessage());    // Error in the underlaying TCP protocol.
        }

        writeToLog("Session: waiting query from client...");

        try {
            InputStream       is = this.clientSocket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");

            in = new BufferedReader(isr);

            String clientQueryLine;

            while (!Thread.interrupted()) {
                try {
                    clientQueryLine = in.readLine();    // throws IOException

                    if (clientQueryLine == null) {
                        break;
                    }

                    answerOnCommand(clientQueryLine);
                } catch (SocketTimeoutException ex) {    // if client not responsable
                    writeToLog("Session: terminated by socket timeout. " +
                               ex.getMessage());

                    break;
                }
            }
        } catch (IOException ex) {    // IOException in in.readLine()
            writeToLog("Session: run error. " + ex.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();    // throws IOException
                }
            } catch (IOException ex) {
                writeToLog("Session: run error. " + ex.getMessage());
            }

            try {
                writeToLog("Session: freeing resources.");
                freeResources();
                writeToLog("Session: ended.");
            } catch (IOException ex) {
                writeToLog("Session: run error. While closing client socket. " +
                           ex.getMessage());
            }

            try {
                if (this.log != null) {
                    this.log.close();    // throws IOException
                    this.log = null;
                }
            } catch (IOException ex) {

                // can`t close log stream. Log wont be saved
                System.out.println("Session: run error. Can`t close log stream. " +
                                   "Log won`t be saved. " + ex.getMessage());
            }
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

    /**
     * Send strings from linesToSend to client.
     * @param linesToSend lines to send.
     */
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

    protected void sendDownloadingGameMap(String[] queryArgs) {
        int[][] ret = null;
        String  mapFileName = queryArgs[1] + ".map";

        if (queryArgs.length == 2) {
            try {
                ret = Creator.createMapAndGetArray(mapFileName);
            } catch (FileNotFoundException ex) {
                sendAnswer("No such map on server.");
                writeToLog("Session: sendMap warning. " +
                           "Client tryed to download map, canceled. " +
                           "Map wasn`t founded on server." + " Map=" +
                           mapFileName + " " + ex.getMessage());

                return;
            } catch (IOException ex) {
                sendAnswer("Error on server side, while loading map.");
                writeToLog("Session: sendMap error. " +
                           "Client tryed to download map, canceled. " +
                           "Error on server side while loading map." +
                           " Map=" + mapFileName + " " + ex.getMessage());

                return;
            }
        } else {    // if arguments!=2
            sendAnswer("Wrong query. Not enough arguments");
            writeToLog("Session: sendMap error. " +
                       "Client tryed to download map, canceled. Wrong query.");

            return;
        }

        // if all is OK.
        List<String> lst = Stringalize.map(ret);

        sendAnswer(lst);
        writeToLog("Session: client downloaded map." + " Map=" + mapFileName);
    }

    protected void sendGameInfo() {
        Game game = this.controller.getMyGame();

        if (game != null) {
            List<String> info = Stringalize.gameInfo(game, this.controller);

            sendAnswer(info);
            writeToLog("Session: sended gameInfo to client.");

            return;
        } else {
            sendAnswer("Not joined to any game.");
            writeToLog("Session: sendGameInfo warning. " +
                       "Client tryed to get game info, canceled. " +
                       "Not joined to any game.");

            return;
        }
    }

    protected void sendGameMapArray() {
        Game game = this.controller.getMyGame();

        if (game != null) {
            List<String> linesToSend =
                Stringalize.mapExplPlayerInfo(game,
                                              this.controller.getPlayer());

            sendAnswer(linesToSend);
            writeToLog("Session: sended mapArray+explosions+playerInfo" +
                       " to client.");

            return;
        } else {
            sendAnswer("Not joined to any game.");
            writeToLog("Session: sendMapArray warning. " +
                       "Canceled. Not joined to any game.");

            return;
        }
    }

    private void sendGameMapArray2() {
        Game game = this.controller.getMyGame();

        if (game != null) {
            List<String> linesToSend =
                Stringalize.mapExplPlayerInfo2(game,
                                              this.controller.getPlayer());

            sendAnswer(linesToSend);
            writeToLog("Session: sended mapArray+explosions+playerInfo" +
                       " to client.");

            return;
        } else {
            sendAnswer("Not joined to any game.");
            writeToLog("Session: sendMapArray warning. " +
                       "Canceled. Not joined to any game.");

            return;
        }
    }

    protected void sendGameMapsList() {
        List<String> maps = Stringalize.gameMapsList();

        if ((maps != null) || (maps.size() > 0)) {
            sendAnswer(maps);
            writeToLog("Session: sended maps list to client. " +
                       "Maps count=" + maps.size());

            return;
        } else {
            sendAnswer("No maps on server was founded.");
            writeToLog("Session: sendMapsList error. " +
                       "No maps founded on server.");

            return;
        }
    }

    protected void sendGameStatus() {
        Game game = this.controller.getMyGame();

        if (game != null) {
            String ret = Stringalize.gameStatus(game);

            sendAnswer(ret);
            writeToLog("Session: sended game status to client.");

            return;
        } else {
            sendAnswer("Not joined to any game.");
            writeToLog("Session: sendGameStatus warning. " +
                       "Canceled. Not joined to any game.");

            return;
        }
    }

    protected void sendGames() {
        List<String> linesToSend =
            Stringalize.unstartedGames(this.server.getGamesList());

        if (linesToSend.size() == 0) {
            this.sendAnswer("No unstarted games finded.");
            writeToLog("Session: client tryed to get games list. " +
                       "No unstarted games finded.");

            return;
        } else {
            this.sendAnswer(linesToSend);
            writeToLog("Session: sended games list to client.");

            return;
        }
    }

    protected void startGame() {
        Game game = this.controller.getMyGame();

        if (game != null) {
            if (!game.isStarted()) {
                boolean success = this.controller.tryStartGame();

                if (success) {
                    sendAnswer("Game started.");
                    writeToLog("Session: started game. " + "(gameName=" +
                               this.controller.getMyGame().getName() + ")");

                    return;
                } else {
                    sendAnswer("Not owner of game.");
                    writeToLog("Session: startGame warning. " +
                               "Client tryed to start game, canceled. " +
                               "Not an owner.");
                }
            } else {    // if game.isStarted() true
                sendAnswer("Game is already started.");
                writeToLog("Session: startGame warning. " +
                           "Client tryed to start started game. " +
                           "Canceled.");

                return;
            }
        } else {    // game == null true
            sendAnswer("Not joined to any game.");
            writeToLog("Session: client tryed to start game, canceled. " +
                       "Not joined to any game.");

            return;
        }
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
