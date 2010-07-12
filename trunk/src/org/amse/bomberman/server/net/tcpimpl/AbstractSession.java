
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.net.ISession;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Constants.Command;
import org.amse.bomberman.util.ILog;

//~--- JDK imports ------------------------------------------------------------

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import java.util.List;
import org.amse.bomberman.server.gameinit.GameStorage;
import org.amse.bomberman.server.net.SessionEndListener;
import org.amse.bomberman.util.ProtocolConstants;

/**
 *
 * @author Kirilchuk V.E.
 */
public abstract class AbstractSession extends Thread implements ISession {
    protected ILog                     log = null;    // it can be null. So use writeToLog() instead of log.println()
    protected final Socket             clientSocket;
    protected final GameStorage        gameStorage;
    protected final int                sessionID;
    protected volatile boolean         mustEnd;

    public AbstractSession(Socket clientSocket,
                           GameStorage gameStorage, int sessionID,
                           ILog log) {
        this.setDaemon(true);      
        this.clientSocket = clientSocket;
        this.gameStorage = gameStorage;
        this.sessionID = sessionID;
        this.log = log;
        this.mustEnd = false;
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

            while (!this.mustEnd) {
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
                    this.clientSocket.close();
                }
            } catch (IOException ex) {
                writeToLog("Session: run error. " + ex.getMessage());
            }

            writeToLog("Session: freeing resources.");
            freeResources();
            writeToLog("Session: closing log...and end.");

            try {
                if (this.log != null) {
                    this.log.close();    // throws IOException
                    this.log = null;
                }
            } catch (IOException ex) {

                // can`t close log stream. Log wont be saved
                System.err.println("Session: run error. Can`t close log stream. " +
                                   "Log won`t be saved. " + ex.getMessage());
            }

            System.out.println("Session: session ended.");
        }
    }

    @Override
    public void terminateSession() {
        this.mustEnd = true;

        try {
            this.clientSocket.close();
        } catch (IOException ex) {
            writeToLog("Session: terminateSession error. " + ex.getMessage());
        }
    }

    @Override
    public void sendAnswer(String shortAnswer) {
        BufferedWriter out = null;

        try {
            OutputStream       os = this.clientSocket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

            out = new BufferedWriter(osw);
            writeToLog("Session: sending answer...");
            out.write(shortAnswer);
            out.newLine();
            out.write(""); //TODO magic code...
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
    @Override
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

            out.write(""); //TODO magic code...
            out.newLine();
            out.flush();
        } catch (IOException ex) {
            writeToLog("Session: sendAnswer error. " + ex.getMessage());
        }
    }

    @Override
    public abstract void notifyClient(String message);

    @Override
    public abstract void notifyClient(List<String> messages);

    protected abstract void freeResources();

    protected void answerOnCommand(String query) { //TODO cmd must use polymorphism! Don`t use such switch case!
        writeToLog("Session: query received. query=" + query);

        if (query.length() == 0) {
            sendAnswer("Empty query. Error on client side.");
            writeToLog("Session: answerOnCommand warning. " +
                       "Empty query received. Error on client side.");

            return;
        }

        Command  cmd = null;
        String[] queryArgs = query.split(ProtocolConstants.SPLIT_SYMBOL);

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

                // "1 gameName mapName maxPlayers playerName" or just "1" for defaults
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

                break;
            }

            case CHAT_ADD_MSG : {

                // "13 message"
                addMessageToChat(queryArgs);

                break;
            }

            case CHAT_GET_NEW_MSGS : {

                // "14"
                getNewMessagesFromChat();

                break;
            }

            case REMOVE_BOT_FROM_GAME : {

                // "15"
                removeBotFromGame();

                break;
            }

            case GET_MY_GAME_PLAYERS_STATS : {

                // "16"
                sendGamePlayersStats();

                break;
            }

            case SET_PLAYER_NAME : {

                // "17 name"
                setClientName(queryArgs);

                break;
            }

            default : {
                sendAnswer("Unrecognized command!");
                writeToLog("Session: answerOnCommand error." +
                           " Getted unrecognized command.");
            }
        }
    }

    protected void writeToLog(String message) {
        if ((this.log != null) &&!filtred(message)) {
            this.log.println(message + "(sessionID=" + sessionID + ")");
        }
    }

    protected boolean filtred(String message) {

//      if (message.startsWith("Session")) {
//          return true;
//      }
        return false;
    }

    public GameStorage getGameStorage() {
        return this.gameStorage;
    }

    protected abstract void sendGames();

    protected abstract void createGame(String[] queryArgs);

    protected abstract void joinGame(String[] queryArgs);

    protected abstract void doMove(String[] queryArgs);

    protected abstract void sendGameMapArray();

    protected abstract void startGame();

    protected abstract void leaveGame();

    protected abstract void placeBomb();

    protected abstract void sendDownloadingGameMap(String[] queryArgs);

    protected abstract void sendGameStatus();

    protected abstract void sendGameMapsList();

    protected abstract void addBot(String[] queryArgs);

    protected abstract void sendGameInfo();

    protected abstract void addMessageToChat(String[] queryArgs);

    protected abstract void getNewMessagesFromChat();

    protected abstract void removeBotFromGame();

    protected abstract void sendGamePlayersStats();

    protected abstract void setClientName(String[] name);

}
