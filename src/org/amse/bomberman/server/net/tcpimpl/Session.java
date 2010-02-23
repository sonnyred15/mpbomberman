/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl;

import org.amse.bomberman.util.Stringalize;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import org.amse.bomberman.server.gameinit.bot.Bot;
import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.Player;
import org.amse.bomberman.server.net.IServer;
import org.amse.bomberman.server.net.ISession;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Constants.Command;
import org.amse.bomberman.util.Constants.Direction;
import org.amse.bomberman.util.Creator;
import org.amse.bomberman.util.ILog;

/**
 *
 * @author Kirilchuk V.E
 */
public class Session extends Thread implements ISession {

    private final IServer server;
    private final Socket clientSocket;
    private final int sessionID;
    private Game game;
    private Player player;
    private final MyTimer timer = new MyTimer(System.currentTimeMillis());
    private ILog log = null; //it can be null. So use writeToLog() instead of log.println()

    public Session(Server server, Socket clientSocket, int sessionID, ILog log) {
        this.setDaemon(true);
        this.server = server;
        this.clientSocket = clientSocket;
        this.sessionID = sessionID;
        this.log = log;
    }

    private void sendAnswer(String shortAnswer) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
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
    private void sendAnswer(List<String> linesToSend) throws IllegalArgumentException {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    this.clientSocket.getOutputStream()));//throws IOException

            writeToLog("Session: sending answer...");

            if (linesToSend == null || linesToSend.size() == 0) {
                writeToLog("Session: sendAnswer error. Realization error."
                        + " Tryed to send 0 strings to client.");
                throw new IllegalArgumentException("Session sendAnswer method must send"
                        + " at least one string to client.");
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

    private void answerOnCommand(String query) {
        writeToLog("Session: query received. query=" + query);
        if (query.length() == 0) {
            sendAnswer("Empty query. Error on client side.");
            writeToLog(
                    "Session: answerOnCommand warning. Empty query received. Error on client side.");
            return;
        }

        Command cmd = null;
        String[] queryArgs = query.split(" ");
        try {

            int command = Integer.parseInt(queryArgs[0]);
            cmd = Command.fromInt(command); //throws IllegalArgumentException

        } catch (NumberFormatException ex) {
            sendAnswer("Wrong query.");
            writeToLog("Session: answerOnCommand error. Wrong first part of query. "
                    + "Wrong query from client. " + ex.getMessage());
            return;
        } catch (IllegalArgumentException ex) {
            sendAnswer("Wrong query. Not supported command.");
            writeToLog("Session: answerOnCommand error. Non supported command int from client. "
                    + ex.getMessage());
            return;
        }

        switch (cmd) {
            case GET_GAMES: {
                //"0"
                sendGames();
                break;
            }
            case CREATE_GAME: {
                //"1 gameName mapName maxPlayers" or just "1" for defaults
                createGame(queryArgs);
                break;
            }
            case JOIN_GAME: {
                //"2 gameID botName"
                joinGame(queryArgs);
                break;
            }
            case DO_MOVE: {
                //"3 direction"
                doMove(queryArgs);
                break;
            }
            case GET_MAP_ARRAY: {
                //"4"
                sendMapArray();
                break;
            }
            case START_GAME: {
                //"5"
                startGame();
                break;
            }
            case LEAVE_GAME: {
                //"6"
                leaveGame();
                break;
            }
            case PLACE_BOMB: {
                //"7"
                placeBomb();
                break;
            }
            case DOWNLOAD_MAP: {
                //"8 mapName"
                sendMap(queryArgs);
                break;
            }
            case GET_GAME_STATUS: {
                //"9"
                sendGameStatus();
                break;
            }
            case GET_MAPS_LIST: {
                //"10"
                sendMapsList();
                break;
            }
            case ADD_BOT_TO_GAME: {
                //"11 gameID botName"
                addBot(queryArgs);
                break;
            }
            case GET_MY_GAME_INFO: {
                //"12"
                sendGameInfo();
                break;//TODO
            }
            case CHAT_ADD_MSG: {
                //"13 message"
                addMessageToChat(queryArgs);
                break;//TODO
            }
            case CHAT_GET_NEW_MSGS: {
                //"14"
                getNewMessagesFromChat();
                break;//TODO
            }
            default: {
                sendAnswer("Unrecognized command!");
                writeToLog("Session: answerOnCommand error. Getted unrecognized command.");
            }
        }
    }

    private void sendGames() {
        List<String> linesToSend = Stringalize.unstartedGames(this.server.getGamesList());

        if (linesToSend.size() == 0) {
            this.sendAnswer("No unstarted games finded.");
            writeToLog("Session: client tryed to get games list. No unstarted games finded.");
            return;
        } else {
            this.sendAnswer(linesToSend);
            writeToLog("Session: sended games list to client.");
            return;
        }
    }

    private void createGame(String[] queryArgs) {
        //Example queryArgs = "1" "gameName" "mapName" "maxpl"
        String gameName = "defaultGameName";
        String mapName = "1";
        int maxPlayers = -1;//-1 for: defines by GameMap.

        if (queryArgs.length == 4) {//if we getted command in full syntax
            gameName = queryArgs[1];
            mapName = queryArgs[2];
            try {
                maxPlayers = Integer.parseInt(queryArgs[3]);
            } catch (NumberFormatException ex) {
                sendAnswer("Wrong query parameters. Error on client side.");
                writeToLog("Session: createGame error. Client tryed to create game, canceled. "
                        + "Wrong command parameters. Error on client side. " + ex.getMessage());
                return;
            }
        }

        try {
            Game newGame = Creator.createGame(this.server, mapName, gameName, maxPlayers);
            this.server.addGame(newGame);

            if (this.game != null) { //if not correct client can create multiple games
                if (this.player != null) {//we just disconnect him from first game!
                    this.game.disconnectFromGame(this.player);
                }
            }

            this.game = newGame;
            //TODO
            this.player = this.game.join("HOST");
            this.game.setOwner(this.player);
            sendAnswer("Game created.");
            writeToLog("Session: client created game."
                    + " Map=" + mapName
                    + " gameName=" + gameName
                    + " maxPlayers=" + maxPlayers);
            return;
        } catch (FileNotFoundException ex) {
            sendAnswer("No such map on server.");
            writeToLog("Session: createGame warning. Client tryed to create game, canceled. "
                    + "Map wasn`t founded on server."
                    + " Map=" + mapName);
            return;
        } catch (IOException ex) {
            sendAnswer("Error on server side, while loading map.");
            writeToLog("Session: createGame error while loadimg map. "
                    + " Map=" + mapName + " " + ex.getMessage());
            return;
        }
    }

    private void joinGame(String[] queryArgs) {
        //"2" "gameID" "botName"

        int gameID = 0;
        String playerName = "defaultPlayer";
        switch (queryArgs.length) {
            case 2: { //to support command in "short" syntax when bot name is ommited
                try {
                    gameID = Integer.parseInt(queryArgs[1]);
                } catch (NumberFormatException ex) {
                    sendAnswer("Wrong command parameters. Error on client side."
                            + " gameID must be int.");
                    writeToLog("Session: joinGame error. " + " Wrong command parameters. "
                            + "Error on client side. gameID must be int. " + ex.getMessage());
                }
                break;
            }
            case 3: { //if we getted command in full syntax
                try {
                    gameID = Integer.parseInt(queryArgs[1]);
                } catch (NumberFormatException ex) {
                    sendAnswer("Wrong command parameters. Error on client side."
                            + " gameID must be int.");
                    writeToLog("Session: joinGame error. " + " Wrong command parameters. "
                            + "Error on client side. gameID must be int. " + ex.getMessage());
                }
                playerName = queryArgs[2];
                break;
            }
            default: { //wrong syntax
                sendAnswer("Wrong query. Error on client side.");
                writeToLog(
                        "Session: joinGame error. Wrong command parameters. Error on client side.");
                break;
            }
        }
        
        if(this.game!=null){
            sendAnswer("Leave another game first.");
            writeToLog("Session: joinGame warning. Client tryed to join game, canceled. Already in other game.");
            return;
        }

        //all is ok
        Game gameToJoin = server.getGame(gameID);
        if (gameToJoin != null) {
            if (!gameToJoin.isStarted()) {
                this.player = gameToJoin.join(playerName);
                if (this.player == null) {//if game is full
                    sendAnswer("Game is full. Try to join later.");
                    writeToLog(
                            "Session: joinGame warning. Client tryed to join to full game, canceled.");
                    return;
                } else {
                    this.game = gameToJoin;
                    sendAnswer("Joined.");
                    writeToLog("Session: client joined to game."
                            + " GameID=" + gameID
                            + " Player=" + playerName);
                    return;
                }
            } else { //if game.isStarted() true
                sendAnswer("Game was already started.");
                writeToLog("Session: joinGame warning. Client tryed to join gameID=" + gameID + " ,canceled."
                        + " Game is already started. ");
                return;
            }
        } else { //if gameToJoin==null true
            sendAnswer("No such game.");
            writeToLog("Session: client tryed to join gameID=" + gameID + " ,canceled."
                    + " No such game on server.");
            return;
        }
    }

    private void doMove(String[] queryArgs) {
        if (this.game != null) {
            if (timer.getDiff() > Constants.GAME_STEP_TIME) {
                int dir = 0;
                boolean moved = false;
                try {
                    dir = Integer.parseInt(queryArgs[1]);//throws NumberFormatException
                    Direction direction = Direction.fromInt(dir); //throws IllegalArgumentException
                    moved = this.game.doMove(player, direction);
                } catch (NumberFormatException ex) {
                    writeToLog("Session: doMove error. Unsupported direction(not int). Error on client side."
                            + " direction=" + queryArgs[1]);
                } catch (IllegalArgumentException ex) {
                    writeToLog("Session: doMove error. Unsupported direction. Error on client side."
                            + " direction=" + dir);
                }

                if (moved) {
                    timer.setStartTime(System.currentTimeMillis());
                }

                sendAnswer("" + moved);
                return;
            } else { //timer.getDiff < gameStep true
                sendAnswer("false");
                writeToLog("Session: doMove warning. Client tryed to move, canceled. Moves allowed only every "
                        + Constants.GAME_STEP_TIME + "ms.");
                return;
            }
        } else { //game == null true
            sendAnswer("Not joined to any game.");
            writeToLog(
                    "Session: doMove warning. Client tryed to move, canceled. Not joined to any game.");
            return;
        }
    }

    private void startGame() {
        if (this.game != null) {
            if (!this.game.isStarted()) {
                if (this.player == this.game.getOwner()) { //ONLY HOST(CREATER) CAN START GAME!!!
                    this.game.startGame();
                    sendAnswer("Game started.");
                    writeToLog("Session: started game. " + "(gameName=" + this.game.getName() + ")");
                    return;
                } else { //if player not owner of game
                    sendAnswer("Not owner of game.");
                    writeToLog(
                            "Session: startGame warning. Client tryed to start game, canceled. Not an owner.");
                }
            } else { //if game.isStarted() true
                sendAnswer("Game is already started.");
                writeToLog(
                        "Session: startGame warning. Client tryed to start started game. Canceled.");
                return;
            }
        } else { // game == null true
            sendAnswer("Not joined to any game.");
            writeToLog("Session: client tryed to start game, canceled. Not joined to any game.");
            return;
        }
    }

    private void leaveGame() {
        if (this.game != null) {
            this.game.disconnectFromGame(this.player);
            this.game = null;
            this.player = null;
            sendAnswer("Disconnected.");
            writeToLog("Session: player has been disconnected from the game.");
            return;
        } else {
            sendAnswer("Not joined to any game.");
            writeToLog(
                    "Session: leaveGame warning. Disconnect from game canceled. Not joined to any game.");
            return;
        }
    }

    private void placeBomb() {
        if (this.game != null) { // Always if game!=null player is not null too!
            if (this.game.isStarted()) {
                this.game.placeBomb(this.player); //is player alive checking in model
                sendAnswer("Ok.");
                writeToLog("Session: tryed to plant bomb. "
                        + "playerID=" + this.player.getID() + " "
                        + this.player.getPosition().toString());
            }
            return;
        } else {
            sendAnswer("Not joined to any game.");
            writeToLog("Session: place bomb warning. Canceled. Not joined to any game.");
            return;
        }
    }

    private void sendMapArray() {
        if (this.game != null) {
            List<String> linesToSend = Stringalize.map(this.game.getMapArray());

            linesToSend.addAll(Stringalize.explosions(this.game.getExplosionSquares()));

            linesToSend.add("" + 1);
            linesToSend.add(Stringalize.playerInfo(this.player));

            sendAnswer(linesToSend);
            writeToLog("Session: sended mapArray+explosions+playerInfo to client.");
            return;
        } else {
            sendAnswer("Not joined to any game.");
            writeToLog("Session: sendMapArray warning. Canceled. Not joined to any game.");
            return;
        }
    }

    private void sendMap(String[] queryArgs) {
        int[][] ret = null;
        String mapFileName = queryArgs[1] + ".map";

        if (queryArgs.length == 2) {
            try {
                ret = Creator.createMapAndGetArray(mapFileName);
            } catch (FileNotFoundException ex) {
                sendAnswer("No such map on server.");
                writeToLog("Session: sendMap warning. Client tryed to download map, canceled. "
                        + "Map wasn`t founded on server."
                        + " Map=" + mapFileName
                        + " " + ex.getMessage());
                return;
            } catch (IOException ex) {
                sendAnswer("Error on server side, while loading map.");
                writeToLog("Session: sendMap error. Client tryed to download map, canceled. "
                        + "Error on server side while loading map."
                        + " Map=" + mapFileName
                        + " " + ex.getMessage());
                return;
            }
        } else { //if arguments!=2
            sendAnswer("Wrong query. Not enough arguments");
            writeToLog("Session: sendMap error. Client tryed to download map, canceled. Wrong query.");
            return;
        }

        //if all is OK.
        List<String> lst = Stringalize.map(ret);
        sendAnswer(lst);
        writeToLog("Session: client downloaded map."
                + " Map=" + mapFileName);

    }

    private void sendGameStatus() {
        if (this.game != null) {
            String ret = Stringalize.gameStatus(this.game);
            sendAnswer(ret);
            writeToLog("Session: sended game status to client.");
            return;
        } else {
            sendAnswer("Not joined to any game.");
            writeToLog("Session: sendGameStatus warning. Canceled. Not joined to any game.");
            return;
        }
    }

    public void sendMapsList() {
        List<String> maps = Stringalize.mapsList(Creator.createMapsList());
        if (maps != null || maps.size()>0) {
            sendAnswer(maps);
            writeToLog("Session: sended maps list to client. Maps count=" + maps.size());
            return;
        } else {
            sendAnswer("No maps on server was founded.");
            writeToLog("Session: sendMapsList error. No maps founded on server.");
            return;
        }
    }

    private void addBot(String[] queryArgs) {
        //"11" "gameID" "botName"

        int gameID = 0;
        String botName = "defaultBot";
        switch (queryArgs.length) {
            case 2: { //to support command in "short" syntax when bot name is ommited
                try {
                    gameID = Integer.parseInt(queryArgs[1]);
                } catch (NumberFormatException ex) {
                    sendAnswer("Wrong command parameters. Error on client side."
                            + " gameID must be int.");
                    writeToLog("Session: addBot error. " + " Wrong command parameters. "
                            + "Error on client side. gameID must be int. " + ex.getMessage());
                }
                break;
            }
            case 3: { //if we getted command in full syntax
                try {
                    gameID = Integer.parseInt(queryArgs[1]);
                } catch (NumberFormatException ex) {
                    sendAnswer("Wrong command parameters. Error on client side."
                            + " gameID must be int.");
                    writeToLog("Session: addBot error. " + " Wrong command parameters. "
                            + "Error on client side. gameID must be int. " + ex.getMessage());
                }
                botName = queryArgs[2];
                break;
            }
            default: { //wrong syntax
                sendAnswer("Wrong query. Error on client side.");
                writeToLog("Session: addBot error. Wrong command parameters. Error on client side.");
                break;
            }
        }

        Game gameToJoin = server.getGame(gameID);
        if (gameToJoin != null) {
            if (!gameToJoin.isStarted()) {
                Bot bot = gameToJoin.joinBot(botName);
                if (bot == null) { //if game is full
                    sendAnswer("Game is full. Try to add bot later.");
                    writeToLog("Session: addBot warning. Tryed to add bot, canceled. Game is full.");
                    return;
                } else {
                    //this.game = gameToJoin;
                    sendAnswer("Bot added.");
                    writeToLog("Session: added bot to game."
                            + " GameID=" + gameID
                            + " Player=" + botName);
                    return;
                }
            } else { //if game.isStarted() true
                sendAnswer("Game was already started.");
                writeToLog("Session: addbot warning. Tryed to add bot to game(gameID=" + gameID + ") ,canceled."
                        + " Game is already started.");
                return;
            }
        } else { //if game==null true
            sendAnswer("No such game.");
            writeToLog("Session: addBot warning. Tryed to add bot to game(gameID=" + gameID + ") ,canceled."
                    + " No such game.");
            return;
        }
    }


    private void sendGameInfo() {
        if (this.game!=null){
            List<String> info = Stringalize.gameInfo(this.game, this.player);
            sendAnswer(info);
            writeToLog("Session: sended gameInfo to client.");
            return;
        }else{
            sendAnswer("Not joined to any game.");
            writeToLog("Session: sendGameInfo warning. Client tryed to get game info, canceled. Not joined to any game.");
            return;
        }
    }

    private void addMessageToChat(String[] queryArgs) {
        if (queryArgs.length >= 2) {
            if (this.game != null) {
                StringBuilder message = new StringBuilder();
                for (int i = 1; i < queryArgs.length; ++i) {
                    message.append(queryArgs[i] + " ");
                }
                this.game.addMessageToChat(this.player, message.toString());
                List<String> toSend = this.game.getNewMessagesFromChat(this.player);
                sendAnswer(toSend);
                writeToLog("Session: client added message to game chat. message=" + queryArgs[1]);
                return;
            } else {
                sendAnswer("Not joined to any game.");
                writeToLog("Session: addMessageToChat warning. Client tryed to add message, canceled. Not joined to any game.");
                return;
            }
        } else {
            sendAnswer("Wrong query. Not enough arguments");
            writeToLog("Session: addMessageToChat error. Client tryed to add message, canceled. Wrong query.");
            return;
        }
    }

    private void getNewMessagesFromChat() {
        if (this.game != null) {
            List<String> toSend = this.game.getNewMessagesFromChat(this.player);
            sendAnswer(toSend);
            writeToLog("Session: client getted new messages from chat. count=" + toSend.size());
            return;
        } else {
            sendAnswer("Not joined to any game.");
            writeToLog("Session: getNewMessagesFromChat warning. Client tryed to get messages, canceled. Not joined to any game.");
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

    @Override
    public void run() {
        BufferedReader in = null;
        try {
            this.clientSocket.setSoTimeout(Constants.DEFAULT_CLIENT_TIMEOUT); //throws SocketException
        } catch (SocketException ex) {
            writeToLog("Session: exception in run method. " + ex.getMessage()); //Error in the underlaying TCP protocol.
        }
        writeToLog("Session: waiting query from client...");
        try {
            in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            String clientQueryLine;

            while (!Thread.interrupted()) {
                try {
                    clientQueryLine = in.readLine(); //throws IOException
                    if (clientQueryLine == null) {
                        break;
                    }
                    answerOnCommand(clientQueryLine);

                } catch (SocketTimeoutException ex) { //if client not responsable
                    writeToLog("Session: terminated by socket timeout. " + ex.getMessage());
                    break;
                }
            }

        } catch (IOException ex) { //IOException in in.readLine()
            writeToLog("Session: run error. " + ex.getMessage());
        } finally {

            try {
                if (in != null) {
                    in.close(); //throws IOException     
                }
            } catch (IOException ex) {
                writeToLog("Session: run error. " + ex.getMessage());
            }

            try {
                writeToLog("Session: freeing resources.");
                freeResources();
                writeToLog("Session: ended.");
            } catch (IOException ex) {
                writeToLog("Session: run error. While closing client socket. " + ex.getMessage());
            }

            try {
                if (this.log != null) {
                    this.log.close(); // throws IOException
                    this.log = null;
                }
            } catch (IOException ex) {
                // can`t close log stream. Log wont be saved
                System.out.println("Session: run error. Can`t close log stream. Log won`t be saved. "
                        + ex.getMessage());
            }
        }
    }

    private void freeResources() throws IOException {
        if (this.game != null) {
            this.game.disconnectFromGame(this.player);
        }
        this.server.sessionTerminated(this);
    }

    private class MyTimer {

        private long startTime;

        public MyTimer(long startTime) {
            this.startTime = startTime;
        }

        public void setStartTime(long time) {
            this.startTime = time;
        }

        public long getDiff() {
            return System.currentTimeMillis() - this.startTime;
        }

    }

    private void writeToLog(String message) {
        if (this.log != null && !filtred(message)) {
            this.log.println(message + "(sessionID=" + sessionID + ")");
        }
    }

    private boolean filtred(String message) {
        return false;
    }

}
