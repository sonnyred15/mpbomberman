package org.amse.bomberman.client.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import org.amse.bomberman.client.model.Map;

/**
 *
 * @author michail korovkin
 */
public class Connector implements IConnector{
    private Socket socket;
    private static IConnector connector= null;

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
    // is realy able to???
    public void disconnect() {
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
        System.out.println(queryAnswer("2" + n).get(0));
        System.out.println();
    }
    public boolean doMove(int direction) {
        String res = queryAnswer("3" + direction).get(0);
        return (res.charAt(0) == 't');
    }
    public void startGame(){
        System.out.println(queryAnswer("5").get(0));
        System.out.println();
    }
    public Map getMap(){
        ArrayList<String> mp = queryAnswer("4");
        Map map = null;
        int i = 0;
        for (String string : mp) {
            // first is size
            if (i != 0) {
                String[] numbers = string.split(" ");
                for (int j = 0; j < numbers.length; j++) {
                    map.setCell(i-1, j, (int)Integer.parseInt(numbers[j]));
                }
            } else {
                // what does exception throw????
                try {
                map = new Map((int)Integer.parseInt(string));
                } catch (NumberFormatException ex){
                    ex.printStackTrace();
                    System.out.println(ex.getMessage());
                    System.out.println(string);
                }
            }
            i++;
        }
        return map;
    }
    public ArrayList<String> queryAnswer(String query){
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
}
