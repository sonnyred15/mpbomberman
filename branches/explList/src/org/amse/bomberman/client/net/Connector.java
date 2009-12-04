package org.amse.bomberman.client.net;

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
import org.amse.bomberman.client.model.Cell;
import org.amse.bomberman.client.model.IModel;
import org.amse.bomberman.client.model.Model;
import org.amse.bomberman.util.*;
import org.amse.bomberman.util.Constants.Command;
import org.amse.bomberman.util.Constants.Direction;

/**
 *
 * @author michail korovkin
 */
public class Connector implements IConnector{
    private Socket socket;
    private Timer timer;

    public Connector() {
    }

    public void сonnect(InetAddress address, int port) throws UnknownHostException, IOException {
        this.socket = new Socket(address, port);
    }
    public void leaveGame() {
        // true stop timer?
        timer.cancel();
        System.out.println(queryAnswer(""+Command.LEAVE_GAME.getValue()).get(0));
        System.out.println();
    }
    public ArrayList<String> takeGamesList() {
        ArrayList<String> games = queryAnswer(""+Command.GET_GAMES.getValue());
        for (String string : games) {
            System.out.println(string);
        }
        System.out.println();
        return games;
    }
    public void createGame(){
        System.out.println(queryAnswer(""+Command.CREATE_GAME.getValue()).get(0));
        System.out.println();
    }
    public boolean joinGame(int n) throws IOException {
        String answer = queryAnswer("2 " + n).get(0);
        System.out.println(answer);
        System.out.println();
        if (answer.equals("Joined.")) {
            return true;
        } else throw new IOException(answer);
    }
    public boolean doMove(Direction dir) {
        String res = queryAnswer("3" + dir.getValue()).get(0);
        return (res.charAt(0) == 't');
    }
    public void startGame(){
        System.out.println(queryAnswer(""+Command.START_GAME.getValue()).get(0));
        System.out.println();
    }
    public void beginUpdating() {
         // must be here or somewhere else???
        timer = new Timer();
        // period???
        timer.schedule(new UpdateTimerTask(), (long)0,(long) Constants.GAME_STEP_TIME);
    }
    public BombMap getMap(){
        ArrayList<String> mp = queryAnswer(""+Command.GET_MAP_ARRAY.getValue());
        BombMap map = null;
        int n = 0;
        n = Integer.parseInt(mp.get(0));
        map = new BombMap(n);
        for (int i = 0; i < n; i++) {
            String[] numbers = mp.get(i+1).split(" ");
            for (int j = 0; j < numbers.length; j++) {
                map.setCell(new Cell(i,j), (int) Integer.parseInt(numbers[j]));
            }
        }
        // receive list of explosive
        int k = Integer.parseInt(mp.get(n+1));
        ArrayList<Cell> expl = new ArrayList<Cell>(k);
        for (int i = 0; i < k; i++) {
            String[] xy = mp.get(i+n+2).split(" ");
            Cell buf = new Cell((int) Integer.parseInt(xy[0])
                    , (int) Integer.parseInt(xy[1]));
            expl.add(buf);
        }
        // receive player info
        // m == 1 always
        int m = Integer.parseInt(mp.get(n+k+2));
        if (m == 1) {
            String[] info = new String[6];
            info = mp.get(n + k + 3).split(" ");
            int x = Integer.parseInt(info[0]);
            int y = Integer.parseInt(info[1]);
            String nick = info[2];
            int lives = Integer.parseInt(info[3]);
            int bombs = Integer.parseInt(info[4]);
            int maxBombs = Integer.parseInt(info[5]);
            Model.getInstance().setPlayerLives(lives);
        }
        map.setExplosions(expl);
        return map;
    }
    public void plantBomb() {
        System.out.println(queryAnswer(""+Command.PLACE_BOMB.getValue()).get(0));
        //System.out.println();
    }
    public InetAddress getInetAddress() {
        return socket.getInetAddress();
    }
    public int getPort() {
        return socket.getPort();
    }

    private synchronized ArrayList<String> queryAnswer(String query){
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return answer;
    }

    private class UpdateTimerTask extends TimerTask {
        @Override
        public void run() {
            IModel model = Model.getInstance();
            model.setMap(getMap());
            //System.out.println("Map has been updated.");
        }
    }
}
