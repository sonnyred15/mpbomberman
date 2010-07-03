package org.amse.bomberman.client.net.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.SwingUtilities;
import org.amse.bomberman.client.net.IConnector;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.control.impl.Controller;
import org.amse.bomberman.client.model.impl.Model;
import org.amse.bomberman.util.Constants.Command;
import org.amse.bomberman.util.Constants.Direction;
import org.amse.bomberman.util.ProtocolConstants;

/**
 *
 * @author Michael Korovkin
 * @author Kirilchuk V.E.
 */
public class AsynchroConnector implements IConnector {

    private Socket socket;
    private static IConnector connector = null;
    private static final char split = ProtocolConstants.SPLIT_SYMBOL.charAt(0);

    private AsynchroConnector() {
    }

    public static IConnector getInstance() {
        if (connector == null) {
            connector = new AsynchroConnector();
        }
        return connector;
    }

    public void сonnect(InetAddress address, int port) throws
            UnknownHostException, IOException, IllegalArgumentException {

        this.socket = new Socket(address, port);
        Thread t = new Thread(new ServerListen());
        t.setDaemon(true);
        t.start();
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

    private synchronized void sendRequest(String request) throws NetException {
        BufferedWriter out = null;
        try {
            OutputStream os = this.socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            out = new BufferedWriter(osw);

            out.write(request);
            out.newLine();
            out.flush();
        } catch (IOException ex) {
            System.out.println("AsynchroConnector: sendRequest error." + ex.getMessage());
            throw new NetException();
        }
    }

    public void requestGamesList() throws NetException {
        sendRequest("" + Command.GET_GAMES.getValue());
    }

    public void requestLeaveGame() throws NetException {
        sendRequest("" + Command.LEAVE_GAME.getValue());
    }

    public void requestCreateGame(String gameName, String mapName, int maxPl)
            throws NetException {

        sendRequest("" + Command.CREATE_GAME.getValue() +
                split + gameName +
                split + mapName +
                split + maxPl +
                split + Model.getInstance().getPlayerName());
    }

    public void requestJoinGame(int gameID) throws NetException {
        sendRequest("2" + split + gameID + split + Model.getInstance().getPlayerName());
    }

    public void requestDoMove(Direction dir) throws NetException {
        sendRequest("3" + split + dir.getValue());
    }

    public void requestStartGame() throws NetException {
        sendRequest("" + Command.START_GAME.getValue());
    }

    public void requestGameMap() throws NetException {
        sendRequest("" + Command.GET_GAME_MAP_INFO.getValue());
    }

    public void requestPlantBomb() throws NetException {
        sendRequest("" + Command.PLACE_BOMB.getValue());
    }

    public void requestJoinBotIntoGame() throws NetException {
        Random r = new Random();
        sendRequest("" + Command.ADD_BOT_TO_GAME.getValue() + split +
                SynchroConnector.botNames[r.nextInt(SynchroConnector.botNames.length - 1)]);
    }
    public void requestRemoveBotFromGame() throws NetException {
        sendRequest("" + Command.REMOVE_BOT_FROM_GAME.getValue());
    }

    public void requestGameMapsList() throws NetException {
        sendRequest("" + Command.GET_GAME_MAPS_LIST.getValue());
    }

    public void requestIsGameStarted() throws NetException {
        sendRequest("" + Command.GET_GAME_STATUS.getValue());
    }

    public void requestGameInfo() throws NetException {
        sendRequest("" + Command.GET_MY_GAME_INFO.getValue());
    }

    public void sendChatMessage(String message) throws NetException {
        sendRequest("" + Command.CHAT_ADD_MSG.getValue() + split + message);
    }

    public void requestNewChatMessages() throws NetException {
        sendRequest("" + Command.CHAT_GET_NEW_MSGS.getValue());
    }

    public void requestDownloadGameMap(String gameMapName) throws NetException {
        sendRequest(
                "" + Command.DOWNLOAD_GAME_MAP.getValue() + split + gameMapName);
    }

    private class ServerListen implements Runnable {

        public void run() {
            BufferedReader in = null;
            try {
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                in = new BufferedReader(isr);

                String oneLine = null;
                List<String> message = new ArrayList<String>();
                while (!Thread.interrupted() && (oneLine = in.readLine()) != null) {
                    if (oneLine.length() == 0) {
                        SwingUtilities.invokeLater(new InvokationServerCommand(message));
                        message = new ArrayList<String>();
                        continue;
                    }
                    message.add(oneLine);
                }

            } catch (IOException ex) {
                System.out.println("ServerListen: run error. " + ex.getMessage());
            }

            System.out.println("ServerListen: run ended.");
            try {
                socket.close();
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }

        private void processServerMessage(List<String> message) {
            String firstLine = message.get(0);
            /* for debugging */
            System.out.println("GETTED SERVER MESSAGE:");
            for (String string : message) {
                System.out.println(string);
            }
            /*--------------*/
            try {
                if (firstLine.equals(ProtocolConstants.UPDATE_CHAT_MSGS)) {
                    Controller.getInstance().requestNewChatMessages();
                } else if (firstLine.equals(
                        ProtocolConstants.UPDATE_GAMES_LIST)) {
                    Controller.getInstance().requestGamesList();
                } else if (firstLine.equals(
                        ProtocolConstants.UPDATE_GAME_INFO)) {
                    Controller.getInstance().requestGameInfo();
                } else if (firstLine.equals(
                        ProtocolConstants.UPDATE_GAME_MAP)) {
                    Controller.getInstance().requestGameMap();
                } else if (firstLine.equals(
                        ProtocolConstants.UPDATE_CHAT_MSGS)) {
                    Controller.getInstance().requestNewChatMessages();
                } else {
                    Controller.getInstance().receivedRequestResult(message);
                }
            } catch (NetException ex) {
                //TODO
                ex.printStackTrace();
                System.out.println("aaa");
            }
        }

        private class InvokationServerCommand implements Runnable {
            List<String> message;

            public InvokationServerCommand(List<String> message) {
                this.message = message;
            }

            public void run() {
                processServerMessage(message);
            }
        }
    }
}
