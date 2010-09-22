package org.amse.bomberman.client.net.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
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
import org.amse.bomberman.client.control.impl.ControllerImpl;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.util.IOUtilities;

/**
 *
 * @author Mikhail Korovkin
 * @author Kirilchuk V.E.
 */
public class AsynchroConnector implements IConnector {
    private static IConnector connector = null;

    private Socket socket;
    private Thread inputThread;
    private DataOutputStream out = null;
    private DataInputStream in = null;

    private AsynchroConnector() {
    }

    public static IConnector getInstance() {
        if (connector == null) {
            connector = new AsynchroConnector();
        }
        return connector;
    }

    public synchronized void сonnect(InetAddress address, int port) throws
            UnknownHostException, IOException, IllegalArgumentException {

        this.socket = new Socket(address, port);
        out = initOut();
        in = initIn();

        inputThread = new Thread(new ServerListen());
        inputThread.setDaemon(true);
        inputThread.start();
    }

    private DataOutputStream initOut() throws IOException {
        OutputStream os = this.socket.getOutputStream();
        return new DataOutputStream(new BufferedOutputStream(os));
    }

    private DataInputStream initIn() throws IOException {
        InputStream is = socket.getInputStream();
        return new DataInputStream(new BufferedInputStream(is));
    }

    public synchronized void closeConnection() {
        try {
            inputThread.interrupt();
            IOUtilities.close(out);
            if(socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ex) {
            System.err.println("Session: terminating error. IOException "
                    + "while closing resourses. " + ex.getMessage());
        }
    }

    public synchronized void sendRequest(ProtocolMessage<Integer, String> request) throws NetException {        
        try {
            List<String> data = request.getData();
            int size = data.size();

            out.writeInt(request.getMessageId());
            out.writeInt(size);
            for(String string : data) {
                out.writeUTF(string);
            }
            //
            out.flush();
        } catch (IOException ex) {
            System.out.println("AsynchroConnector: sendRequest error." + ex.getMessage());
            throw new NetException();
        } finally {
            closeConnection();
        }
    }

    private class ServerListen implements Runnable {
                
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    ProtocolMessage<Integer, String> message = new ProtocolMessage<Integer, String>();
                    int messageId = in.readInt();
                    if (messageId == ProtocolConstants.DISCONNECT_MESSAGE_ID) {
                        break;
                    }

                    message.setMessageId(messageId);

                    int size = in.readInt();
                    List<String> data = new ArrayList<String>(size);
                    for (int i = 0; i < size; i++) {
                        data.add(in.readUTF());
                    }

                    SwingUtilities.invokeLater(new InvokationCommand(message));
                }

            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("ServerListen: run error. " + ex.getMessage());
            } finally {
                IOUtilities.close(in);
            }

            System.out.println("ServerListen: run ended.");            
        }

        private class InvokationCommand implements Runnable {

            ProtocolMessage<Integer, String> message;

            public InvokationCommand(ProtocolMessage<Integer, String> message) {
                this.message = message;
            }

            public void run() {
                processServerMessage();
            }

            private void processServerMessage() {
                int messageId = message.getMessageId();

                /* for debugging */
                // TODO USE LOGGER!
                /*--------------*/
                try {
                    if(messageId == ProtocolConstants.NOTIFICATION_MESSAGE_ID) {
                        List<String> notifications = message.getData();
                        for (String string : notifications) {
                            processNotification(string);
                        }
                    } else {
                        ControllerImpl.getInstance().receivedRequestResult(message);
                    }
                } catch (NetException ex) {
                    //TODO what to do with it?
                    ex.printStackTrace();
                }
            }

            private void processNotification(String string) throws NetException {
                if (string.equals(ProtocolConstants.UPDATE_CHAT_MSGS)) {
                    ControllerImpl.getInstance().requestNewChatMessages();
                } else if (string.equals(ProtocolConstants.UPDATE_GAMES_LIST)) {
                    ControllerImpl.getInstance().requestGamesList();
                } else if (string.equals(ProtocolConstants.UPDATE_GAME_INFO)) {
                    ControllerImpl.getInstance().requestGameInfo();
                } else if (string.equals(ProtocolConstants.UPDATE_GAME_MAP)) {
                    ControllerImpl.getInstance().requestGameMap();
                } else if (string.equals(ProtocolConstants.UPDATE_CHAT_MSGS)) {
                    ControllerImpl.getInstance().requestNewChatMessages();
                }
            }
        }
    }
}
