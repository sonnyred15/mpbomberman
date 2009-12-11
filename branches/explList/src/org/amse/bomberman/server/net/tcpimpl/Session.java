/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl;

import org.amse.bomberman.util.Stringalize;
import org.amse.bomberman.server.net.*;
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
import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.Player;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Constants.*;
import org.amse.bomberman.util.Creator;
import org.amse.bomberman.util.ILog;

/**
 *
 * @author Kirilchuk V.E
 */
public class Session extends Thread implements ISession {

    private final Server server;
    private final Socket clientSocket;
    private final int sessionID;
    private Game game;
    private Player player;
    private MyTimer timer = new MyTimer(System.currentTimeMillis());
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
            writeToLog("Session: Sending answer...");
            out.write(shortAnswer);
            out.newLine();

            out.write("");
            out.newLine();
            out.flush();
        } catch (IOException ex) {
            writeToLog(ex.getMessage() + " In sendAnswer method.");
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

            writeToLog("Session: Sending answer...");

            if (linesToSend == null || linesToSend.size() == 0) {
                writeToLog("ERROR in Session. Tryed to send 0 Strings to client in sendAnswer method");
                throw new IllegalArgumentException("MUST SEND AT LEAST ONE STRING!!!");
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
            writeToLog(ex.getMessage() + " In sendAnswer method.");
        }
    }

    private void answerOnCommand(String query) {
        if (query.length() == 0) {
            sendAnswer("Empty query received. Error on client side.");
            writeToLog("Empty query received. Error on client side. query=" + query);
            return;
        }

        Command cmd = null;
        try {
            // CHECK THIS!!! Command can consist ot 2 digits!!!
            if (query.charAt(0) != '1') {
                int command = Integer.parseInt(query.substring(0, 1));
                cmd = Command.fromInt(command);
            } else {
                // if it is "10" == GET_MAPS_LIST
                if (query.length() == 2) {
                    int command = Integer.parseInt(query.substring(0, 2));
                    cmd = Command.fromInt(command);
                    // if it is "1"+gN+mN+mP
                } else {
                    int command = Integer.parseInt(query.substring(0, 1));
                    cmd = Command.fromInt(command);
                }
            }
        } catch (NumberFormatException nEx) {
            writeToLog(nEx.getMessage() +
                    "First char of command must be int from 0 to 7 inclusive. " +
                    "Error command from client.");
        } catch (IllegalArgumentException ex) {
            writeToLog("Non supported by Command enum int from client.");
        }

        switch (cmd) {
            case GET_GAMES: {
                //"0"
                sendGames();
                break;
            }
            case CREATE_GAME: {
                //"1 gameName mapName maxPlayers" or just "1" for defaults
                createGame(query);
                break;
            }
            case JOIN_GAME: {
                //"2 gameID playerName"
                joinGame(query);
                break;
            }
            case DO_MOVE: {
                //"3"+direction
                doMove(query);
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
                //"8"+" "+mapName
                sendMap(query);
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
            default: { //CHECK < V THIS!!!// never happens!?
                sendAnswer("Wrong query. Unrecognized command!");
                writeToLog("Getted wrong query. Unrecognized command!" +
                        " query=" + query);
            }
        }
    }

    private void sendGames() {
        List<String> linesToSend = Stringalize.unstartedGames(this.server.getGamesList());

        if (linesToSend.size() == 0) {
            this.sendAnswer("No unstarted games finded.");
            writeToLog("Tryed to get games list. No unstarted games finded");
            return;
        } else {
            this.sendAnswer(linesToSend);
            writeToLog(" Tryed to get games list. Some games sended");
            return;
        }
    }

    private void createGame(String query) {
        //Example query = "1 gameName mapName maxpl"
        String gameName = "defaultGame";
        String mapName = "1";
        int maxPlayers = -1;//defines by GameMap;

        String[] args = query.split(" ");
        if (args.length == 4) {//if we getted command in full syntax
            gameName = args[1];
            mapName = args[2];
            try {
                maxPlayers = Integer.parseInt(args[3]);
            } catch (NumberFormatException nEx) {
                sendAnswer("Wrong command parameters. Error on client side.");
                writeToLog(" Tryed to create game, canceled. " +
                        "Wrong command parameters. Error on client side.");
                return;
            }
        }
        try {
            Game g = Creator.createGame(this.server, mapName, gameName, maxPlayers);
            this.server.addGame(g);
            sendAnswer("Game created.");
            writeToLog("Tryed to create game. " +
                    "Game created. Map=" + mapName +
                    " gameName=" + gameName +
                    " maxPlayers=" + maxPlayers +
                    " query=" + query);
            return;
        } catch (FileNotFoundException ex) {
            sendAnswer("No such map on server.");
            writeToLog("Tryed to create game, canceled. " +
                    "Map wasn`t founded on server." +
                    " Map=" + mapName +
                    " query=" + query);
            return;
        } catch (IOException ex) {
            sendAnswer("Error on server side, while loading map.");
            writeToLog("Tryed to create game, canceled. " +
                    "Error on server side while loading map." +
                    " Map=" + mapName +
                    " query=" + query);
            return;
        }
    }

    private void joinGame(String query) {
        //"2 gameID playerName"
        String[] args = query.split(" ");

        int gameID = 0;
        String playerName = "defaultPlayer";
        switch (args.length) {
            case 2: { //to support command in "short" syntax when player name is ommited
                try {
                    gameID = Integer.parseInt(args[1]);
                } catch (NumberFormatException ex) {
                    sendAnswer("Wrong command parameters. Error on client side." +
                            " gameID must be int.");
                    writeToLog(ex.getMessage() + " Wrong command parameters. " +
                            "Error on client side. gameID must be int." +
                            " query=" + query);
                }
                break;
            }
            case 3: { //if we getted command in full syntax
                try {
                    gameID = Integer.parseInt(args[1]);
                } catch (NumberFormatException ex) {
                    sendAnswer("Wrong command parameters. Error on client side." +
                            " gameID must be int.");
                    writeToLog(ex.getMessage() + " Wrong command parameters. " +
                            "Error on client side. gameID must be int." +
                            " query=" + query);
                }
                playerName = args[2];
                break;
            }
            default: { //wrong syntax
                sendAnswer("Wrong command parameters. Error on client side.");
                writeToLog(" Wrong command parameters. Error on client side." +
                        " query=" + query);
                break;
            }
        }

        Game gameToJoin = server.getGame(gameID);
        if (gameToJoin != null) {
            if (!gameToJoin.isStarted()) {
                this.player = gameToJoin.join(playerName);
                if (this.player == null) {
                    sendAnswer("Game is full. Try to join later.");
                    writeToLog("Tryed to join to full game, canceled");
                    return;
                } else {
                    this.game = gameToJoin;
                    sendAnswer("Joined.");
                    writeToLog("Tryed to join to the game. Joined." +
                            " GameID=" + gameID +
                            " Player=" + playerName +
                            " query=" + query);
                    return;
                }
            } else { //if game.isStarted() true
                sendAnswer("Game was  already started.");
                writeToLog("Tryed to join gameID=" + gameID + " but canceled " +
                        "cause game is already started ");
                return;
            }
        } else { //if game==null true
            sendAnswer("No such game.");
            writeToLog("Tryed to join gameID=" + gameID + " but canceled " +
                    "no such game on server. ");
            return;
        }
    }

    private void doMove(String query) {
        if (this.game != null) {
            if (timer.getDiff() > Constants.GAME_STEP_TIME) {
                int dir = 0;
                boolean moved = false;
                try {
                    dir = Integer.parseInt(query.substring(1, 2));//throws NumberFormatException
                    Direction direction = Direction.fromInt(dir); //throws IllegalArgumentException
                    moved = this.game.doMove(player, direction);
                } catch (NumberFormatException ex) {
                    writeToLog("Tryed to move, canceled. Unsupported direction. Error on client side." +
                            " query=" + query);
                } catch (IllegalArgumentException ex) {
                    writeToLog("Tryed to move, canceled. Unsupported direction. Error on client side." +
                            " direction=" + dir +
                            " query=" + query);
                }

                if (moved) {
                    timer.setStartTime(System.currentTimeMillis());
                }

                sendAnswer("" + moved);
                return;
            } else { //timer.getDiff < gameStep true
                sendAnswer("false");
                writeToLog("Tryed to move, canceled. Moves allowed only every " +
                        Constants.GAME_STEP_TIME + "ms." +
                        " query=" + query);
                return;
            }
        } else { //game == null true
            sendAnswer("false");
            writeToLog("Tryed to move, canceled. Not joined to any game.");
            return;
        }
    }

    private void startGame() {
        if (this.game != null) {
            if (!this.game.isStarted()) {
                this.game.startGame();
                sendAnswer("Game started.");
                writeToLog("Started game");
                return;
            } else {
                sendAnswer("Game is already started.");
                writeToLog("Tryed to start started game");
                return;
            }
        } else { // game == null true
            sendAnswer("Error. Game not started. Not joined to any game.");
            writeToLog("Tryed to start game, canceled. Not joined to any game.");
            return;
        }
    }

    private void leaveGame() {
        if (this.game != null) {
            this.game.disconnectFromGame(this.player);
            this.game = null;
            this.player = null;
            sendAnswer("Disconnected.");
            writeToLog("Player has  been disconnected from the game");
            return;
        } else {
            sendAnswer("Cant disconnect. You are not in any game");
            writeToLog("Tryed to disconnect from game, canceled. Not joined to any game");
            return;
        }
    }

    private void placeBomb() {
        if (this.game != null) { // Always if game!=null player is not null too!
            if (this.game.isStarted()) {
                this.game.placeBomb(this.player); //is player alive checking in model
                sendAnswer("Ok.");
                writeToLog("Tryed to plant bomb." +
                        " playerID=" + this.player.getID() +
                        " x=" + this.player.getX() +
                        " y=" + this.player.getY());
            }
        } else {
            sendAnswer("Error.Cant place bomb.");
            writeToLog("Tryed to place bomb in illegal state. Not joined to any game.");
        }
    }

    private void sendMapArray() {
        if (this.game != null) {
            List<String> linesToSend = Stringalize.map(this.game.getMapArray());

            linesToSend.addAll(Stringalize.explosions(this.game.getExplosionSquares()));

            linesToSend.add("" + 1);
            linesToSend.add(Stringalize.playerInfo(this.player));

            sendAnswer(linesToSend);
            writeToLog("Sended mapArray+explosions+playerInfo to client");
        } else {
            sendAnswer("You are not joined to any game. Error.");
            writeToLog("Tryed to getMapArray, canceled. Not joined to any game.");
        }
    }

    private void sendMap(String query) {
        String[] args = query.split(" ");
        int[][] ret = null;

        if (args.length == 2) {
            String mapFileName = args[1] + ".map";
            try {
                ret = Creator.createMapAndGetArray(mapFileName);
            } catch (FileNotFoundException ex) {
                sendAnswer("No such map on server.");
                writeToLog("Tryed to download map, canceled. " +
                        "Map wasn`t founded on server." +
                        " Map=" + mapFileName +
                        " query=" + query +
                        " " + ex.getMessage());
                return;
            } catch (IOException ex) {
                sendAnswer("Error on server side, while loading map.");
                writeToLog("Tryed to download map, canceled. " +
                        "Error on server side while loading map." +
                        " Map=" + mapFileName +
                        " query=" + query +
                        " " + ex.getMessage());
                return;
            }
        } else { //if arguments!=2
            sendAnswer("Wrong query. Not enough arguments");
            writeToLog("Tryed to download map. Canceled. Wrong query. query=" + query);
            return;
        }

        //if all is OK.
        List<String> lst = Stringalize.map(ret);
        sendAnswer(lst);
        writeToLog("Downloaded map. query=" + query);

    }

    private void sendGameStatus() {
        if (this.game != null) {
            String ret = Stringalize.gameStatus(this.game);
            sendAnswer(ret);
            writeToLog("Sended game status.");
            return;
        } else {
            sendAnswer("You are not joined to any game. Error.");
            writeToLog("Tryed to get game status, canceled. Not joined to any game.");
            return;
        }
    }

    public void sendMapsList() {
        List<String> maps = Stringalize.mapsList(Creator.createMapsList());
        if (maps != null) {
            sendAnswer(maps);
            writeToLog("Sended maps list.");
            return;
        } else {
            sendAnswer("No maps on server was founded.");
            writeToLog("Tryed to get maps list. No maps founded on server.");
            return;
        }
    }

    @Override
    public void run() {
        BufferedReader in = null;
        try {
            this.clientSocket.setSoTimeout(Constants.DEFAULT_CLIENT_TIMEOUT); //throws SocketException
        } catch (SocketException ex) {
            writeToLog("Exception in Session run method. " + ex.getMessage()); //Error in the underlaying TCP protocol.
        }
        writeToLog("Session: Waiting for query from client.");
        try {
            in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            String clientQueryLine;

            while (!Thread.interrupted()) {
                try {
                    clientQueryLine = in.readLine(); //throws IOException
                    if (clientQueryLine == null) {
                        break;
                    }
                    writeToLog("Session: Query line received: '" + clientQueryLine + "'.");
                    answerOnCommand(clientQueryLine);

                } catch (SocketTimeoutException ex) { //if client not responsable
                    writeToLog("Session: Terminated by socket timeout.");
                    break;
                }
            }

        } catch (IOException ex) { //IOException in in.readLine()
            writeToLog(ex.getMessage() + " In session.run() method.");
        } finally {

            try {
                if (in != null) {
                    in.close(); //throws IOException     
                }
            } catch (IOException ex) {
                writeToLog("IOException in Session run method while closing `in` stream.");
            }

            try {
                if (this.log != null) {
                    this.log.close(); // throws IOException
                    this.log = null;
                }
            } catch (IOException ex) {
                // can`t close log stream. Log wont be saved
                System.out.println("Error: can`t close log stream. Log won`t be saved " +
                        ex.getMessage());
            }

            try {
                writeToLog("Session: Ended. Freeing resources.");
                freeResources();
            } catch (IOException ex) {
                writeToLog("Session: Error while closing client socket.");
            }

        }
    }

    private void freeResources() throws IOException {
        if (this.game != null) {
            this.game.disconnectFromGame(this.player);
            this.game = null;
            this.player = null;
        }
        this.clientSocket.close();
        this.timer = null;
        this.log = null;
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
            this.log.println(this.sessionID + " " + message);
        }
    }

    private boolean filtred(String message) {
        if (message.startsWith("Session: Query line received: '4")) {
            return true;
        }
        if (message.startsWith("Session: Sending answer")) {
            return true;
        }
        if (message.startsWith("Sended mapArray")) {
            return true;
        }
        if (message.startsWith("Tryed to move, canceled")) {
            return true;
        }
        if (message.startsWith("Query line received: '3")) {
            return true;
        }
        return false;
    }
}
