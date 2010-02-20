package org.amse.bomberman.client.net.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.amse.bomberman.client.model.BombMap;
import org.amse.bomberman.client.model.IModel;
import org.amse.bomberman.client.model.impl.Model;
import org.amse.bomberman.client.net.IConnector;
import org.amse.bomberman.util.*;
import org.amse.bomberman.util.Constants.Command;
import org.amse.bomberman.util.Constants.Direction;
import org.amse.bomberman.util.impl.Parser;

/**
 *
 * @author Michail Korovkin
 */
public class Connector implements IConnector{
    private Socket socket;
    private Timer timer;
    private static IConnector connector = null;

    private Connector() {
    }
    public static IConnector getInstance() {
        if (connector == null) {
            connector = new Connector();
        }
        return connector;
    }

    public void сonnect(InetAddress address, int port) throws UnknownHostException, IOException {
        this.socket = new Socket(address, port);
    }
    public void leaveGame() throws NetException{
        // true stop timer?
        if (timer != null) {
            timer.cancel();
        }
        ArrayList<String> list = queryAnswer(""+Command.LEAVE_GAME.getValue());
        System.out.println(list.get(0));
        System.out.println();
    }
    public ArrayList<String> takeGamesList() throws NetException{
        ArrayList<String> games = queryAnswer(""+Command.GET_GAMES.getValue());
        for (String string : games) {
            System.out.println(string);
        }
        System.out.println();
        return games;
    }
    /**
     * Create new game in server.
     * @param gameName Name of game.
     * @param mapName Name of map of new game.
     * @param maxPl maximum value of players that can connect to this new game.
     * @return true if game is created. Don't return false :)
     * @throws java.io.IOException if game isn't created and server has some
     * troubles with arguments.
     */
    public boolean createGame(String gameName, String mapName, int maxPl)
            throws IOException, NetException{
        ArrayList<String> answer = queryAnswer(""+Command.CREATE_GAME.getValue()
                +" "+ gameName +" "+ mapName +" "+ maxPl);
        if (answer.get(0).equals("Game created.")) {
            return true;
        } else {
            throw new IOException(answer.get(0));
        }
    }
    public boolean joinGame(int n) throws IOException, NetException{
        ArrayList<String> list = queryAnswer("2 " + n);
        String answer = list.get(0);
        System.out.println(answer);
        System.out.println();
        if (answer.equals("Joined.")) {
            return true;
        } else throw new IOException(answer);
    }
    public boolean doMove(Direction dir) throws NetException {
        ArrayList<String> list = queryAnswer("3 " + dir.getValue());
        String res = list.get(0);
        // if res == "true"
        return (res.charAt(0) == 't');
    }
    public void startGame() throws NetException{
        System.out.println(queryAnswer(""+Command.START_GAME.getValue()));
    }
    public void beginUpdating(){
         // must be here or somewhere else???
        timer = new Timer();
        timer.schedule(new UpdateTimerTask(), (long)0,(long) Constants.GAME_STEP_TIME);
    }
    public BombMap getMap() throws NetException{
        ArrayList<String> mp = queryAnswer(""+Command.GET_MAP_ARRAY.getValue());
        Parser parser = new Parser();
        return parser.parse(mp);
    }
    public void plantBomb() throws NetException {
        System.out.println(queryAnswer(""+Command.PLACE_BOMB.getValue()));
    }
    // if server has not any maps, return one String "No maps on server was founded."
    public String[] getMaps() throws NetException {
        ArrayList<String> maps = queryAnswer(""+Command.GET_MAPS_LIST.getValue());
        String[] res = new String[maps.size()];
        for(int i = 0; i < maps.size(); i++) {
            res[i] = maps.get(i);
        }
        return res;
    }

    /**
     * Check if game that you connected is started already.
     * @return true if game is started, false if isn't.
     * @throws java.io.IOException if you are not connected to any game.
     */
    public boolean isStarted() throws IOException, NetException {
        ArrayList<String> status = queryAnswer(""+Command.GET_GAME_STATUS.getValue());
        if (status.get(0).equals("started.")) {
            return true;
        } else {
            if (status.get(0).equals("not started.")) {
                return false;
            } else {
                throw new IOException(status.get(0));
            }
        }
    }
    public boolean joinBotIntoGame(int n) throws IOException, NetException{
        String answer = queryAnswer("11 " + n).get(0);
        System.out.println(answer);
        System.out.println();
        if (answer.equals("Bot added.")) {
            return true;
        } else throw new IOException(answer);
    }

    public InetAddress getInetAddress() {
        return socket.getInetAddress();
    }
    public int getPort() {
        return socket.getPort();
    }
    
    private synchronized ArrayList<String> queryAnswer(String query) throws NetException{
        PrintWriter out = null;
        BufferedReader in = null;
        ArrayList<String> answer=null;
        try {
            out = new PrintWriter(this.socket.getOutputStream());
            //System.out.println("Client: Sending query: '"+query+"'.");
            out.println(query);
            out.flush();

            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            String oneLine;
            answer = new ArrayList<String>();
            while ((oneLine = in.readLine()) != null) {
                if (oneLine.length() == 0) {
                    break;
                }
                answer.add(oneLine);
            }
            //System.out.println("Client: Answer received.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new NetException();
        }
        if (answer.size() == 0) {
            throw new NetException();
        }
        return answer;
    }
    private class UpdateTimerTask extends TimerTask{
        @Override
        public void run() {
            IModel model = Model.getInstance();
            try {
                model.setMap(getMap());
                //System.out.println("Map has been updated.");
            } catch (NetException ex) {
                // is it good???
                ex.printStackTrace();
                this.cancel();
            }
        }
    }
    public class NetException extends Exception {
        public NetException(){
            super("NetException!!!\nServer is inaccessible now.\nPlease reconnect!");
        }
    }
}