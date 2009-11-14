/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import org.amse.bomberman.server.gameInit.Game;
import org.amse.bomberman.server.gameInit.Map;
import org.amse.bomberman.server.gameInit.Player;

/**
 *
 * @author chibis
 */
public class Session extends Thread {

    private Net net;
    private Socket clientSocket;
    private Game game;
    private Player player;
    private MyTimer timer= new MyTimer(System.currentTimeMillis());

    private Session() {
    }

    public Session(Socket clientSocket, Net net) {
        this.setDaemon(true);
        this.clientSocket = clientSocket;
        this.net = net;
    }
    // CHECK V THIS!!!//
    /**
     * If linesToSend!=null method sends ONLY this lines.
     * Otherwise method sends shortAnswer.
     * @param shortAnswer line to send if linesToSend=null.
     * @param linesToSend lines to send.
     */
    private void sendAnswer(String shortAnswer, ArrayList<String> linesToSend) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(this.clientSocket.getOutputStream());

            System.out.println("Session: Sending answer...");

            if (linesToSend != null) {
                for (String string : linesToSend) {
                    out.println(string);
                }
            } else {
                out.println(shortAnswer);
            }

            out.println();
            out.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage() + " In sendAnswer method.");
        }
    }

    private void answerOnCommand(String query) {
        if (query.length() == 0) {
            sendAnswer("Empty query received. Error on client side.", null);
            return;
        }

        int command = -1;
        try {
            command = Integer.parseInt(query.substring(0, 1));
        } catch (NumberFormatException nEx) {
            System.out.println(nEx.getMessage() + " Command must be int!");
        }

        switch (command) {
            case Commands.GET_GAMES: {
                //"0"
                sendGames();
                break;
            }
            case Commands.CREATE_GAME: {
                //"1 gameName mapName maxPlayers"
                createGame(query);
                break;
            }
            // CHECK v THIS!!!//
            case Commands.JOIN_GAME: {
                //"2"+gameID
                int gameID = 0;
                try {
                    gameID = Integer.parseInt(query.substring(1));
                } catch (NumberFormatException ex) {
                    System.out.println(ex.getMessage() + " gameID must be int.");
                }
                joinGame(gameID);
                break;
            }
            // CHECK v THIS!!!//
            case Commands.DO_MOVE: {
                if (timer.getDiff() > 200) {
                    int direction = 0;
                    boolean moved = false;
                    try {
                        direction = Integer.parseInt(query.substring(1, 2));
                        if (direction < 0 || direction > 3) {
                            throw new NumberFormatException("Unsupported direction. Error on client side.");                            
                        }
                        moved = this.game.doMove(player, direction);
                    } catch (NumberFormatException ex) {
                        System.out.println(ex.getMessage() + " Direction must be int from 0 to 3.");
                        break;
                    }
                    if(moved){
                        timer.setStartTime(System.currentTimeMillis());
                    }
                    sendAnswer("" + moved, null);
                    break;
                }else{
                    sendAnswer("false", null);
                    break;
                }
            }
            case Commands.GET_MAP_ARRAY: {
                if (this.game != null) {
                    sendMap(this.game.getMapArray());
                } else {
                    sendAnswer("You are not joined to any game. Error.", null);
                }
                break;
            }
            case Commands.START_GAME: {
                startGame();
                break;
            }
            case Commands.LEAVE_GAME: {
                this.game.disconnect(this.player);
                this.game = null;
                sendAnswer("Disconnected.", null);
                break;
            }
            case Commands.PLACE_BOMB: {
                this.game.placeBomb(this.player);
                sendAnswer("ok", null);
                break;
            }
            default: {
                sendAnswer("Wrong query. Unrecognized command!", null);
            }
        }
    }

    private void sendGames() {
        ArrayList<Game> games = (ArrayList<Game>) net.getGamesList();

        ArrayList<String> linesToSend = new ArrayList<String>();

        int counter = 0;
        Iterator<Game> it = games.iterator();
        for (int i = 0; it.hasNext(); ++i) {
            Game g = it.next();
            //send only games that are not started!!!
            if (!g.isStarted()) {
                ++counter;
                linesToSend.add(i + ":" + g.getName());
            }
        }

        if (counter == 0) {
            this.sendAnswer("No Games Finded.", null);//"???change to no UNSTARTED Games???"
            return;
        } else {
            this.sendAnswer(null, linesToSend);
            return;
        }
    }

    private void createGame(String query) {
        //Example query = "1 gameName mapName maxpl"
        String gameName = "defaultGame";
        String mapName = "1";
        int maxPlayers = -1;//defines by Map;

        String[] args = query.split(" ");
        if (args.length == 4) {
            gameName = args[1];
            mapName = args[2];
            try {
                maxPlayers = Integer.parseInt(args[3]);
            } catch (NumberFormatException numberFormatException) {
                sendAnswer("Wrong command parameters. Error on client side.", null);
                return;
            }
        }
        try {
            Game g = new Game(new Map(mapName + ".map"), gameName, maxPlayers);
            this.net.addGame(g);
            sendAnswer("Game created.", null);
            return;
        } catch (FileNotFoundException ex) {
            sendAnswer("No such map on server.", null);
            return;
        } catch (IOException ex) {
            sendAnswer("Error on server side, while loading map.", null);
            return;
        }
    }

    private void joinGame(int n) {
        Random rnd = new Random();
        int k = rnd.nextInt(10);
        Game g = net.getGame(n);
        if (!g.isStarted()) {
            this.game = g;
            this.player = this.game.join("testPlayer" + k);
            if (this.player == null) {
                sendAnswer("Game is full. Try to join later.", null);
                return;
            } else {
                sendAnswer("Joined.", null);
                return;
            }
        } else {
            sendAnswer("No such game or game was started.", null);
            return;
        }
    }

    private void startGame() {
        if (!this.game.isStarted()) {
            this.game.startGame();
            sendAnswer("Game started.", null);
            return;
        } else {
            sendAnswer("Game is already started.", null);
            return;
        }
    }

    private void sendMap(int[][] map) {
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
        sendAnswer("", linesToSend);
    }

    @Override
    public void run() {
        BufferedReader in = null;
        try {
            this.clientSocket.setSoTimeout(1000);
        } catch (SocketException ex) {
            //
        }
        System.out.println("Session: Waiting for query from client.");
        try {
            in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            String clientQueryLine;

            while (!this.net.isShutdowned()) {
                try {
                    clientQueryLine = in.readLine();
                } catch (SocketTimeoutException ex) {
                    continue;
                }
                if (clientQueryLine == null) {
                    break;
                }
                System.out.println("Session: Query line received: '" + clientQueryLine + "'.");
                answerOnCommand(clientQueryLine);
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage() + " In session.run() method.");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    //
                }
            }
        }
    }
    
    private class MyTimer{
        private long startTime;
        
        public MyTimer(long startTime){
            this.startTime = startTime;
        }
        
        public void setStartTime(long time){
            this.startTime = time;
        }
        
        public long getDiff(){
            return System.currentTimeMillis()-this.startTime;
        }
    }
}
