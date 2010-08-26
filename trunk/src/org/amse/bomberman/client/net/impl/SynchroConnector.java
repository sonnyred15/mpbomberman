package org.amse.bomberman.client.net.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import org.amse.bomberman.client.control.IController;
import org.amse.bomberman.client.net.IConnector;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.control.impl.Controller;
import org.amse.bomberman.client.model.impl.Model;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.protocol.RequestCommand;
import org.amse.bomberman.util.Constants.Direction;
import org.amse.bomberman.protocol.ProtocolConstants;

/**
 *
 * @author Kirilchuk V.E. and Mikhail Korovkin
 */
public class SynchroConnector implements IConnector {

    private Timer timer;
    private Socket socket;
    private static IConnector connector = null;
    public static final String[] botNames = {"BOT_ANDY", "BOT_SAM", "BOT_JOE", "BOT_VASYA"
    , "BOT_PETYA", "BOT_ANYA", "BOT_LOOSER", "BOT_UNLUCKY", "BOT_FOOL", "BOT_SUICIDE"};

    private SynchroConnector() {
        
    }
    public static IConnector getInstance() {
        if (connector == null) {
            connector = new SynchroConnector();
        }
        return connector;
    }

    public void сonnect(InetAddress address, int port) throws
            UnknownHostException,IOException {
        this.socket = new Socket(address, port);
    }
    public void disconnect() {
        if (socket != null) {
            try {
                this.socket.close();
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
    }

    public void requestLeaveGame() throws NetException {
        List<String> answer = queryAnswer("" + RequestCommand.LEAVE.getValue());
        System.out.println(answer.get(0));
        // not essential, can be deleted. Use for stop timers faster.
        if (answer.get(0).equals("Disconnected.")) {
            stopUpdating();
        }
        answer.add(0, ProtocolConstants.CAPTION_LEAVE_GAME_RESULT);
        Controller.getInstance().receivedRequestResult(answer);
    }

    public void requestGamesList() throws NetException {
        List<String> games = queryAnswer("" + RequestCommand.GET_GAMES.getValue());
        for (String string : games) {
            System.out.println(string);
        }
        games.add(0, ProtocolConstants.CAPTION_GAMES_LIST);
        Controller.getInstance().receivedRequestResult(games);
    }

    public void requestCreateGame(String gameName, String mapName, int maxPl)
            throws NetException {
        List<String> answer = queryAnswer("" + RequestCommand.CREATE_GAME.getValue() +
                ProtocolConstants.SPLIT_SYMBOL + gameName + ProtocolConstants.SPLIT_SYMBOL
                + mapName + ProtocolConstants.SPLIT_SYMBOL + maxPl
                + ProtocolConstants.SPLIT_SYMBOL
                + Model.getInstance().getPlayerName());
        if (answer.get(0).equals("Game created.")) {
            beginPanel3Updating();
        }
        answer.add(0, ProtocolConstants.CAPTION_CREATE_GAME_RESULT);
        Controller.getInstance().receivedRequestResult(answer);
    }

    public void requestJoinGame(int gameID) throws NetException {
        List<String> list = queryAnswer("2" + ProtocolConstants.SPLIT_SYMBOL + gameID + ProtocolConstants.SPLIT_SYMBOL
                + Model.getInstance().getPlayerName());
        System.out.println(list.get(0));
        if (list.get(0).equals("Joined.")) {
            beginPanel3Updating();
        }
        list.add(0, ProtocolConstants.CAPTION_JOIN_GAME_RESULT);
        Controller.getInstance().receivedRequestResult(list);
    }

    public void requestDoMove(Direction dir) throws NetException {
        List<String> list = queryAnswer("3" + ProtocolConstants.SPLIT_SYMBOL + dir.getValue());
        list.add(0, ProtocolConstants.CAPTION_DO_MOVE_RESULT);
        Controller.getInstance().receivedRequestResult(list);
    }

    public void requestStartGame() throws NetException {
        List<String> list = queryAnswer("" + RequestCommand.START_GAME.getValue());
        System.out.println(list);
        // start timer for updating map of game
        if (list.get(0).equals("Game started.") && !Model.getInstance().isStarted()) {
            this.beginGameUpdating();
        }
        list.add(0, ProtocolConstants.CAPTION_START_GAME_RESULT);
        Controller.getInstance().receivedRequestResult(list);
    }

    public void requestGameMap() throws NetException {
        List<String> gameMap = queryAnswer("" + RequestCommand.GET_GAME_MAP_INFO.getValue());
        gameMap.add(0, ProtocolConstants.CAPTION_GAME_MAP_INFO);
        Controller.getInstance().receivedRequestResult(gameMap);
    }

    public void requestPlantBomb() throws NetException {
        List<String> list = queryAnswer("" + RequestCommand.PLACE_BOMB.getValue());
        list.add(0, ProtocolConstants.CAPTION_PLACE_BOMB_RESULT);
        Controller.getInstance().receivedRequestResult(list);
    }

    public void requestJoinBotIntoGame() throws NetException {
        Random r = new Random();
        List<String> list = queryAnswer("" +
                RequestCommand.ADD_BOT_TO_GAME.getValue()+" "
                + botNames[r.nextInt(botNames.length-1)]);
        list.add(0, ProtocolConstants.CAPTION_JOIN_BOT_RESULT);
        Controller.getInstance().receivedRequestResult(list);
    }
    public void requestRemoveBotFromGame() throws NetException {
        List<String> list = queryAnswer("" + RequestCommand.REMOVE_BOT_FROM_GAME.getValue());
        list.add(0, ProtocolConstants.CAPTION_REMOVE_BOT_RESULT);
        Controller.getInstance().receivedRequestResult(list);
    }

    public void requestGameMapsList() throws NetException {
        List<String> gameMaps = queryAnswer("" + RequestCommand.GET_GAME_MAPS_LIST.getValue());
        gameMaps.add(0, ProtocolConstants.CAPTION_GAME_MAPS_LIST);
        Controller.getInstance().receivedRequestResult(gameMaps);
    }

    public void requestIsGameStarted() throws NetException {
        List<String> list = queryAnswer("" + RequestCommand.GET_GAME_STATUS.getValue());
        // does it work??? for start updating not a host.
        if (list.get(0).equals("started.") && !Model.getInstance().isStarted()) {
            this.beginGameUpdating();
        }
        list.add(0, ProtocolConstants.CAPTION_GAME_STATUS);
        Controller.getInstance().receivedRequestResult(list);
    }

    public void requestGameInfo() throws NetException {
        List<String> list = queryAnswer("" + RequestCommand.GET_GAME_INFO.getValue());
        list.add(0, ProtocolConstants.CAPTION_GAME_INFO);
        Controller.getInstance().receivedRequestResult(list);
    }

    public void sendChatMessage(String message) throws NetException {
        List<String> answer = queryAnswer("" + RequestCommand.CHAT_ADD_MSG.getValue() +
                ProtocolConstants.SPLIT_SYMBOL + message);
        answer.add(0, ProtocolConstants.CAPTION_ADD_CHAT_MSG_RESULT);
        Controller.getInstance().receivedRequestResult(answer);
    }

    public void requestNewChatMessages() throws NetException {
        List<String> answer = queryAnswer("" + RequestCommand.CHAT_GET_NEW_MSGS.getValue());
        answer.add(0, ProtocolConstants.CAPTION_NEW_CHAT_MSGS);
        Controller.getInstance().receivedRequestResult(answer);
    }

    public void requestDownloadGameMap(String gameMapName) throws NetException {
        List<String> answer = queryAnswer("" + RequestCommand.DOWNLOAD_GAME_MAP + " " + gameMapName);
        answer.add(0, ProtocolConstants.CAPTION_DOWNLOAD_GAME_MAP);
        Controller.getInstance().receivedRequestResult(answer);
    }

    private synchronized ArrayList<String> queryAnswer(String query) throws
            NetException {
        PrintWriter out = null;
        BufferedReader in = null;
        ArrayList<String> answer = null;
        try {
            OutputStream os = this.socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            out = new PrintWriter(osw);
            //System.out.println("Client: Sending query: '"+query+"'.");
            out.println(query);
            out.flush();

            InputStream is = this.socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            in = new BufferedReader(isr);
            String oneLine;
            answer = new ArrayList<String>();
            while ((oneLine = in.readLine()) != null) {
                if (oneLine.length() == 0) {
                    break;
                }
                answer.add(oneLine);
            }
        } catch (IOException e) {
            System.out.println(e);
            throw new NetException();
        }
        if (answer.size() == 0) {
            throw new NetException();
        }
        return answer;
    }
     // must be here or somewhere else???
    public void beginGameUpdating(){
        timer = new Timer();
        timer.schedule(new GameTimerTask(), (long)0,(long) Constants.GAME_STEP_TIME);
    }
     // must be here or somewhere else???
    private void beginPanel3Updating(){
        timer = new Timer();
        timer.schedule(new Panel3TimerTask(), (long)0,(long) Constants.GAME_STEP_TIME);
    }
    private void stopUpdating() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    public void requestSetPlayerName(String playerName) throws NetException {
        List<String> answer = queryAnswer("" + ProtocolConstants.CAPTION_SET_CLIENT_NAME
                + ProtocolConstants.SPLIT_SYMBOL + playerName);
        answer.add(0, ProtocolConstants.CAPTION_SET_CLIENT_NAME);
        Controller.getInstance().receivedRequestResult(answer);
    }
    private class GameTimerTask extends TimerTask{
        @Override
        public void run() {
            try {
                if (!Model.getInstance().isStarted()) {
                    stopUpdating();
                }
                requestIsGameStarted();
                requestGameMap();
            } catch (NetException ex) {
                // is it good???
                //ex.printStackTrace();
                System.out.println(ex);
                this.cancel();
            }
        }
    }
    private class Panel3TimerTask extends TimerTask {

        @Override
        public void run() {
            try {
                IController c = Controller.getInstance();
                c.requestGameInfo();
                c.requestNewChatMessages();
                c.requestIsGameStarted();
                // how do it better??? ----------------------------------------
                /*RequestResultListener listener = c.getReceiveInfoListener();
                if (listener instanceof Wizard) {
                    JPanel panel = ((Wizard) listener).getCurrentJPanel();
                    if (panel instanceof Panel3) {
                        Panel3 panel3 = (Panel3) panel;
                        if (!((Wizard) listener).isShowing()) {
                            stopUpdating();
                        }
                    }
                }*/

            } catch (NetException ex) {
                // is it good???
                //ex.printStackTrace();
                System.out.println(ex);
                stopUpdating();
                this.cancel();
            }
        }
    }
}
