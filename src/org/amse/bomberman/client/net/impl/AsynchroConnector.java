/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.util.Timer;
import java.util.TimerTask;
import org.amse.bomberman.client.model.BombMap;
import org.amse.bomberman.client.model.IModel;
import org.amse.bomberman.client.model.impl.Model;
import org.amse.bomberman.client.net.IConnector;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Constants.Command;
import org.amse.bomberman.util.Constants.Direction;
import org.amse.bomberman.util.impl.Parser;
import org.amse.bomberman.client.net.NetException;

/**
 *
 * @author Kirilchuk V.E.
 */
public class AsynchroConnector implements IConnector {

    private Socket socket;
    private Timer timer;
    private static IConnector connector = null;
    private final List<String> gamesList = new ArrayList<String>();
    private final Integer created = -1;

    public static IConnector getInstance() {
        if (connector == null) {
            connector = new AsynchroConnector();
        }
        return connector;
    }

    public void сonnect(InetAddress address, int port) throws UnknownHostException, IOException {
        this.socket = new Socket(address, port);
    }

    public List<String> takeGamesList() throws NetException {
        queryAnswer("" + Command.GET_GAMES.getValue());


        if (this.gamesList.size() == 0) {
            synchronized (this.gamesList) {
                try {
                    while (this.gamesList.size() == 0) {
                        this.gamesList.wait();
                    }
                    List<String> result = new ArrayList<String>();
                    result.addAll(this.gamesList);
                    this.gamesList.clear();
                    return result;
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    return null;
                }
            }
        } else {
            List<String> result = new ArrayList<String>();
            result.addAll(this.gamesList);
            this.gamesList.clear();
            return result;
        }
    }

    /**
     * Create new game in server.
     * @param gameName Name of game.
     * @param mapName Name of map of new game.
     * @param maxPl maximum value of players that can connect to this new game.
     * @return true if game is created. Return false if something wrong :)
     */
    public boolean createGame(String gameName, String mapName, int maxPl)
            throws NetException {

        queryAnswer("" + Command.CREATE_GAME.getValue() + " " + gameName + " " + mapName + " " + maxPl);

        if (this.created == -1) {
            synchronized (this.created) {
                try {
                    while (this.created == -1) {
                        this.created.wait();
                    }
                    boolean result = (this.created == 1 ? true : false);
                    //
                    return result;
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    return false;
                }
            }
        } else {
            return this.created == 0 ? false : true;
        }
    }

    public boolean leaveGame() throws NetException {
        // true stop timer?
        if (timer != null) {
            timer.cancel();
        }

        ArrayList<String> list = queryAnswer("" + Command.LEAVE_GAME.getValue());
        System.out.println(list.get(0));
        return (list.get(0).equals("Disconnected."));
    }

    public boolean joinGame(int n) throws NetException {
        ArrayList<String> list = queryAnswer("2 " + n);
        String answer = list.get(0);
        System.out.println(answer);
        if (answer.equals("Joined.")) {
            return true;
        } else {
            return false;
        }

    }

    public boolean doMove(Direction dir) throws NetException {
        ArrayList<String> list = queryAnswer("3 " + dir.getValue());
        String res = list.get(0);
        return (res.equals("true"));
    }

    public boolean startGame() throws NetException {
        List<String> list = queryAnswer("" + Command.START_GAME.getValue());
        System.out.println(list);
        return (list.get(0).equals("Game started."));
    }

    public BombMap getMap() throws NetException {
        ArrayList<String> mp = queryAnswer("" + Command.GET_MAP_ARRAY.getValue());
        Parser parser = new Parser();
        return parser.parse(mp);
    }

    public boolean plantBomb() throws NetException {
        List<String> list = queryAnswer("" + Command.PLACE_BOMB.getValue());
        System.out.println(list.get(0));
        return (list.get(0).equals("Ok."));
    }
// if server has not any maps, return one String "No maps on server was founded."

    public String[] getMaps() throws NetException {
        ArrayList<String> maps = queryAnswer("" + Command.GET_MAPS_LIST.getValue());
        String[] res = new String[maps.size()];
        for (int i = 0; i <
                maps.size(); i++) {
            res[i] = maps.get(i);
        }

        return res;
    }

    /**
     * Check if game that you connected is started already.
     * @return true if game is started, false if isn't.
     * @throws java.io.IOException if you are not connected to any game.
     */
    public boolean isStarted() throws NetException {
        ArrayList<String> status = queryAnswer("" + Command.GET_GAME_STATUS.getValue());
        if (status.get(0).equals("started.")) {
            return true;
        } else {
            return false;
        }

    }

    public boolean joinBotIntoGame(int n) throws NetException {
        // bot name???!!!
        String answer = queryAnswer("" + Command.ADD_BOT_TO_GAME.getValue() + " " + n + " BOT").get(0);
        System.out.println(answer);
        if (answer.equals("Bot added.")) {
            return true;
        } else {
            return false;
        }

    }
    // if you are nit joined to any games, return one String "Not joined to any game."

    public List<String> getMyGameInfo() throws NetException {
        List<String> answer = queryAnswer("" + Command.GET_MY_GAME_INFO.getValue());
        return answer;
    }

    public List<String> sendChatMessage(String message) throws NetException {
        List<String> answer = queryAnswer("" + Command.CHAT_ADD_MSG.getValue() + " " + message);
        return answer;
    }

    public List<String> getNewChatMessages() throws NetException {
        List<String> answer = queryAnswer("" + Command.CHAT_GET_NEW_MSGS.getValue());
        return answer;
    }

    public InetAddress getInetAddress() {
        return socket.getInetAddress();
    }

    public int getPort() {
        return socket.getPort();
    }

    private synchronized ArrayList<String> queryAnswer(String query) throws NetException {
        PrintWriter out = null;
        ArrayList<String> answer = null;
        try {
            OutputStream os = this.socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

            out =
                    new PrintWriter(osw);
            out.println(query);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new NetException();
        }

        return null;
    }

    public void beginUpdating() {
        // must be here or somewhere else???
        timer = new Timer();
        timer.schedule(new UpdateTimerTask(), (long) 0, (long) Constants.GAME_STEP_TIME);
    }

    private class UpdateTimerTask
            extends TimerTask {

        @Override
        public void run() {
            IModel model = Model.getInstance();
            try {
                model.setMap(getMap());
            } catch (NetException ex) {
                // is it good???
                ex.printStackTrace();
                this.cancel();
            }
        }
    }

    private class ServerListen implements Runnable {

        public void run() {
            BufferedReader in = null;
            try {
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                in = new BufferedReader(isr);

                String oneLine;
                List<String> message = new ArrayList<String>();
                while (!Thread.interrupted()) {
                    while ((oneLine = in.readLine()) != null) {
                        if (oneLine.length() == 0) {
                            break;
                        }
                        message.add(oneLine);
                    }
                    processServerMessage(message);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private void processServerMessage(List<String> message) {//TODO fix NPE
            String firstLine = message.get(0);

            if (firstLine.startsWith("Games list")) {
                message.remove(0);
                synchronized (gamesList) {
                    gamesList.clear();
                    gamesList.addAll(message);
                    gamesList.notifyAll();
                }
            } else if (firstLine.startsWith("Created game")) {
            }


        }
    }
}
