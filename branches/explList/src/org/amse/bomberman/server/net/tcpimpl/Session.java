/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl;

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.GameMap;
import org.amse.bomberman.server.gameinit.Pair;
import org.amse.bomberman.server.gameinit.Player;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Constants.*;
import org.amse.bomberman.util.ILog;

/**
 *
 * @author chibis
 */
public class Session extends Thread implements ISession{

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
    private void sendAnswer(ArrayList<String> linesToSend) throws IllegalArgumentException {
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
            int command = Integer.parseInt(query.substring(0, 1));
            cmd = Command.fromInt(command);
        } catch (NumberFormatException nEx) {
            writeToLog(nEx.getMessage() +
                    "First char of command must be int from 0 to 7 inclusive. " +
                    "Error command from client.");
        } catch (IllegalArgumentException ex){
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
            // CHECK v THIS!!!//
            case JOIN_GAME: {
                //"2 gameID playerName"
                joinGame(query);
                break;
            }
            // CHECK v THIS!!!//
            case DO_MOVE: {
                //"3"+direction
                doMove(query);
                break;
            }
            case GET_MAP_ARRAY: {
                //"4"
                sendMap();
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
            default: { //CHECK < V THIS!!!// never happens!?
                sendAnswer("Wrong query. Unrecognized command!");
                writeToLog("Getted wrong query. Unrecognized command!" +
                        " query=" + query);
            }
        }
    }

    private void sendGames() {
        List<Game> games =  server.getGamesList();

        ArrayList<String> linesToSend = new ArrayList<String>();

        int counter = 0;
        Iterator<Game> it = games.iterator();
        for (int i = 0; it.hasNext(); ++i) {
            Game g = it.next();
            //send only games that are not started!!!
            if (!g.isStarted()) {
                ++counter;
                linesToSend.add(i + " " + 
                        g.getName() + " " +
                        g.getMapName() + " " +
                        g.getCurrentPlayers() + " " +
                        g.getGameMaxPlayers());
            }
        }

        if (counter == 0) {
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
        if (args.length == 4) {
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
            Game g = new Game(new GameMap(mapName + ".map"), gameName, maxPlayers);
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
            case 2: {
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
            case 3: {
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
            default: {
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
            } else {
                sendAnswer("Game was  already started.");
                writeToLog("Tryed to join gameID=" + gameID + " but canceled " +
                        "cause game is already started ");
                return;
            }
        } else { //if game==null true;
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
                writeToLog("Tryed to move, canceled. Moves allowed only every 200ms." +
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
        } else{ // game == null true
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
                this.game.placeBomb(this.player); //CHECK < THIS// whats about player isAlive?
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

    private void sendMap() {
        if (this.game != null) {
            int[][] map = this.game.getMapArray();
            ArrayList<String> linesToSend = new ArrayList<String>();
            linesToSend.add(String.valueOf(map.length));
            for (int i = 0; i < map.length; i++) {
                StringBuilder buff = new StringBuilder();
                for (int j = 0; j < map.length; j++) {
                    buff.append(map[i][j]);
                    buff.append(" ");
                }
                buff.deleteCharAt(buff.length() - 1);
                linesToSend.add(buff.toString());
            }
            ////////////////explosionSquares//////////////////
            List<Pair> explSq = this.game.getExplosionSquares();            
            linesToSend.add(""+explSq.size());
            for (Pair pair : explSq) {
                linesToSend.add(pair.getX() + " " + pair.getY());
            }
            //////////////////////////////////////////////////
            
            ///////////////playerInfo//////////////////
            String info = this.player.getInfo();
            linesToSend.add(""+1);
            linesToSend.add(info);
            //////////////////////////////////////////
            
            sendAnswer(linesToSend);
            writeToLog("Sended mapArray+explosions+playerInfo to client");
        } else {
            sendAnswer("You are not joined to any game. Error.");
            writeToLog("Tryed to getMapArray, canceled. Not joined to any game.");
        }
    }

    @Override
    public void run() {
        BufferedReader in = null;
        try {
            this.clientSocket.setSoTimeout(1000); //throws SocketException
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
                } catch (SocketTimeoutException ex) {
                    continue;
                }
                if (clientQueryLine == null) {
                    break;
                }
                writeToLog("Session: Query line received: '" + clientQueryLine + "'.");
                answerOnCommand(clientQueryLine);
            }

        } catch (IOException ex) { //IOException in in.readLine()
            writeToLog(ex.getMessage() + " In session.run() method.");
        } finally {
            if (in != null) {
                try {
                    in.close(); //throws IOException                    
                } catch (IOException ex) {
                    writeToLog("IOException in Session run method while closing `in` stream.");
                }
            }

            try {
                if (log != null) {
                    log.close(); // throws IOException
                }
            } catch (IOException ex) {
                // can`t close log stream. Log wont be saved
                System.out.println("Error: can`t close log stream. Log won`t be saved " +
                        ex.getMessage());
            }
        }
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
    
    private boolean filtred(String message){
        if (message.equals("Session: Query line received: '4'.")){
            return true;
        }
        if (message.equals("Session: Sending answer...")){
            return true;
        }
        if (message.equals("Sended mapArray to client")){
            return true;
        }
        return false;
    }
}
