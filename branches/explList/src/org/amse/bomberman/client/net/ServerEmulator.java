package org.amse.bomberman.client.net;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import org.amse.bomberman.client.model.BombMap;
import org.amse.bomberman.client.model.BombMap.Direction;
import org.amse.bomberman.client.model.Player;

/**
 *
 * @author michail korovkin
 */
public class ServerEmulator extends Thread{
    public static final int GET_HOSTS = 0;
    public static final int CREATE_HOST = 1;
    public static final int JOIN_HOST = 2;
    public static final int DO_MOVE = 3;
    public static final int GET_MAP = 4;

    private final int port =10500;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BombMap map;

    public ServerEmulator() throws IOException {
        serverSocket = new ServerSocket(port,0,InetAddress.getByName("localhost"));
    }
    @Override
    public void run() {
        System.out.println("Server: started.");
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                System.out.println("Server: Client is connected. Start new thread for him...");
                break;
            } catch (IOException ex) {
                //
            }
        }
        BufferedReader in = null;

        System.out.println("Session: Ожидается запрос от клиента.");
        try {
            in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

            String clientMessage;
            while((clientMessage=in.readLine())!=null){
                System.out.println("Session: Получен запрос: '"+ clientMessage+"'.");
                answerOnCommand(clientMessage);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage()+ " In session.run() method.");
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
     private void answerOnCommand(String query){
        int command = Integer.parseInt(query.substring(0, 1));
        switch (command){
            case ServerEmulator.GET_HOSTS:{
                //sendHosts();
                break;
            }
            case ServerEmulator.CREATE_HOST:{
                //createHost();
                try {
                    map = new BombMap("input2.txt");
                } catch (FileNotFoundException ex) {
                    System.out.println(ex.getMessage());
                } catch (UnsupportedOperationException ex) {
                    System.out.println(ex.getMessage());
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
                sendAnswer("Host Created", null);
                break;
            }
            case ServerEmulator.JOIN_HOST:{
                int hostNum=Integer.parseInt(query.substring(1));
                //joinHost(hostNum);
                //map.addPlayer(new Player("Mavr"),0, 0);
                sendAnswer("Joined.", null);
                break;
            }
            case ServerEmulator.DO_MOVE:{
                int dir=Integer.parseInt(query.substring(1, 2));
                //map.movePlayer(1, Direction.getDirection(dir));
                sendOK();
                break;
            }
            case ServerEmulator.GET_MAP:{
                sendMap(map.getMassive());
                break;
            }
            default: {
                sendAnswer("Unrecognized command!", null);
            }
        }
    }
    private void sendMap(int[][] map) {
        ArrayList<String> linesToSend = new ArrayList<String>();
        linesToSend.add(String.valueOf(map.length));
        for (int i = 0; i < map.length; i++) {
            StringBuilder buff = new StringBuilder();
            for (int j = 0; j < map.length; j++) {
                buff.append(map[i][j]);
                // ????
                buff.append(" ");
            }
            linesToSend.add(buff.toString());
        }
        sendAnswer("", linesToSend);
    }

    private void sendOK() {
        this.sendAnswer("OK", null);
    }
    private void sendAnswer(String shortAnswer, ArrayList<String> linesToSend) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(this.clientSocket.getOutputStream());

            System.out.println("Session: Answer is sending...");

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
}
