package org.amse.bomberman.client.net.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import org.amse.bomberman.client.net.IConnector;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.control.impl.Controller;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.protocol.ProtocolMessage;

/**
 *
 * @author Mikhail Korovkin
 * @author Kirilchuk V.E.
 */
public class AsynchroConnector implements IConnector {

    private Socket socket;
    private static IConnector connector = null;

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

//    public synchronized void sendRequest(String request) throws NetException {
//        BufferedWriter out = null;
//        try {
//            OutputStream os = this.socket.getOutputStream();
//            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
//            out = new BufferedWriter(osw);
//
//            out.write(request);
//            out.newLine();
//            out.flush();
//        } catch (IOException ex) {
//            System.out.println("AsynchroConnector: sendRequest error." + ex.getMessage());
//            throw new NetException();
//        }
//    }
    public synchronized void sendRequest(ProtocolMessage<Integer, String> request) throws NetException {
        DataOutputStream out = null;
        try {
            OutputStream os = this.socket.getOutputStream();            
            out = new DataOutputStream(new BufferedOutputStream(os));

            //
            out.writeInt(request.getMessageId());
            List<String> data = request.getData();
            int size = data.size();
            out.writeInt(size);
            for(String string : data) {
                out.writeUTF(string);
            }
            //
            out.flush();
        } catch (IOException ex) {
            System.out.println("AsynchroConnector: sendRequest error." + ex.getMessage());
            throw new NetException();
        }
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
                        SwingUtilities.invokeLater(new InvokationServerRequestCommand(message));
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

        private class InvokationServerRequestCommand implements Runnable {
            List<String> message;

            public InvokationServerRequestCommand(List<String> message) {
                this.message = message;
            }

            public void run() {
                processServerMessage(message);
            }
        }
    }
}
