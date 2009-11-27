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
import org.amse.bomberman.client.model.BombMap.Cell;
import org.amse.bomberman.client.model.BombMap.Direction;
import org.amse.bomberman.client.model.IModel;
import org.amse.bomberman.client.model.Model;

/**
 *
 * @author michail korovkin
 */
public class Connector implements IConnector{
    private Socket socket;
    private static IConnector connector= null;
    private Timer timer;

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
    public void leaveGame() {
        // true stop timer?
        timer.cancel();
        System.out.println(queryAnswer("6").get(0));
        System.out.println();
    }
    public ArrayList<String> takeGamesList() {
        ArrayList<String> games = queryAnswer("0");
        for (String string : games) {
            System.out.println(string);
        }
        System.out.println();
        return games;
    }
    public void createGame(){
        System.out.println(queryAnswer("1").get(0));
        System.out.println();
    }
    public void joinGame(int n) {
        System.out.println(queryAnswer("2 " + n).get(0));
        System.out.println();
    }
    public boolean doMove(Direction dir) {
        String res = queryAnswer("3" + dir.getInt()).get(0);
        return (res.charAt(0) == 't');
    }
    public void startGame(){
        System.out.println(queryAnswer("5").get(0));
        System.out.println();
    }
    public void beginUpdating() {
         // must be here or somewhere else???
        timer = new Timer();
        // period???
        timer.schedule(new UpdateTimerTask(), (long)0,(long) 200);
    }
    public BombMap getMap(){
        ArrayList<String> mp = queryAnswer("4");
        BombMap map = null;
        int n = 0;
        // what does exception throw????
        try {
            n = Integer.parseInt(mp.get(0));
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
            System.out.println(mp.get(0));
        }
        map = new BombMap(n);
        for (int i = 0; i < n; i++) {
            String[] numbers = mp.get(i+1).split(" ");
            for (int j = 0; j < numbers.length; j++) {
                map.setCell(i, j, (int) Integer.parseInt(numbers[j]));
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
        System.out.println(queryAnswer("7").get(0));
        System.out.println();
    }

    private synchronized ArrayList<String> queryAnswer(String query){
        PrintWriter out = null;
        BufferedReader in = null;
        ArrayList<String> answer=null;
        try {
            out = new PrintWriter(this.socket.getOutputStream());
            System.out.println("Client: Sending query: '"+query+"'.");
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
            System.out.println("Client: Answer received.");
        } catch (Exception e) {
        }
        return answer;
    }

    private class UpdateTimerTask extends TimerTask {
        @Override
        public void run() {
                Model model = (Model)Model.getInstance();
                model.setMap(getMap());
                System.out.println("Map has been updated.");
        }
    }
}
