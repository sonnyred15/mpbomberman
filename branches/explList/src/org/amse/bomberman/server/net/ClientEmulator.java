/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net;

import org.amse.bomberman.server.net.tcpimpl.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 *
 * @author chibis
 */
public class ClientEmulator {

    Session session;
    Socket socket;

    public ClientEmulator() {
        this.session = null;
        this.socket = null;
    }

    public void connect(int port) throws UnknownHostException, IOException {
        this.socket = new Socket(InetAddress.getByName("localhost"), port);
    }

    public void takeGamesList() throws IOException {
        ArrayList<String> games = queryAnswer("0");
        for (String string : games) {
            System.out.println(string);
        }
        System.out.println();
    }

    public void createGame() {
        System.out.println(queryAnswer("1").get(0));
        System.out.println();
    }

    public void joinGame(int n) {
        System.out.println(queryAnswer("2 " + n).get(0));
        System.out.println();
    }

    public void doMove(int direction) {
        System.out.println(queryAnswer("3" + direction).get(0));
        System.out.println();
    }

    public void startGame() {
        System.out.println(queryAnswer("5").get(0));
        System.out.println();
    }

    public void getMap() {
        ArrayList<String> map = queryAnswer("4");
        for (String string : map) {
            System.out.println(string);
        }
        System.out.println();
    }

    public ArrayList<String> queryAnswer(String query) {
        PrintWriter out = null;
        BufferedReader in = null;
        ArrayList<String> answer = null;
        try {
            out = new PrintWriter(this.socket.getOutputStream());
            System.out.println("Client: Sending qurery: '" + query + "'.");
            out.println(query);
//            out.println();
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
