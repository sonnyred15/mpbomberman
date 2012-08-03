package org.amse.bomberman.server.net.tcpimpl.sessions.asynchro;

import org.amse.bomberman.util.structs.SeparatelySynchronizedMap;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.amse.bomberman.protocol.impl.ProtocolConstants;
import org.amse.bomberman.protocol.impl.ProtocolMessage;
import org.amse.bomberman.server.net.Session;
import org.amse.bomberman.server.net.SessionEndListener;
import org.amse.bomberman.util.IOUtilities;

/**
 *
 * @author Kirilchuk V.E.
 */
public class AsynchroSender implements SessionEndListener {

    private final SeparatelySynchronizedMap<Integer, ProtocolMessage> messagesMap =
            new SeparatelySynchronizedMap<Integer, ProtocolMessage>(30);
    //
    private final Socket clientSocket;
    private final SenderThread senderThread;
    private volatile boolean mustEnd = false;

    public AsynchroSender(Socket clientSocket, long sessionId) {
        this.clientSocket = clientSocket;

        this.senderThread = new SenderThread("Single Notificator("
                + sessionId + ")");
        this.senderThread.setDaemon(true);
    }

    public void start() {
        this.senderThread.start();
    }

    public void addToQueue(ProtocolMessage message) {
        messagesMap.put(message.getMessageId(), message);
    }

    @Override
    public void sessionTerminated(Session endedSession) {
        this.mustEnd = true;
        ProtocolMessage disconnectMessage
                = new ProtocolMessage();
        disconnectMessage.setMessageId(ProtocolConstants.DISCONNECT_MESSAGE_ID);
        disconnectMessage.setData(new ArrayList<String>());
        try {
            senderThread.send(disconnectMessage);
        } catch (IOException ex) {
            ex.printStackTrace();
            //ignore cause can do nothing..
        }
        senderThread.interrupt();
    }

    private class SenderThread extends Thread {

        private DataOutputStream out;

        private SenderThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            System.out.println(super.getName() + " thread started.");
            Set<Entry<Integer, ProtocolMessage>> entrySet = messagesMap.entrySet();
            try {
                out = initWriter();
                while (!mustEnd && !isInterrupted()) {
                    try {
                        for (Entry<Integer, ProtocolMessage> entry : entrySet) {
                            ProtocolMessage toSend = entry.setValue(null);
                            if (toSend != null) {
                                send(toSend);
                            }
                        }
                        Thread.sleep(30);//to not spam client every nanosecond =)
                    } catch (InterruptedException ex) {                                                
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                IOUtilities.close(out);
                try {
                    if(clientSocket != null && !clientSocket.isClosed()) {
                        clientSocket.close();
                        System.out.println("Session: closed socket.");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            System.out.println(super.getName() + " output listening thread ended.");
        }

        /**
         * Send strings from linesToSend to client.
         * @param linesToSend lines to send.
         *
         * @throws IllegalArgumentException
         */
        private void send(ProtocolMessage response)
                throws IOException {
            if (response.isBroken()) {
                throw new IllegalArgumentException("Broken response. "
                        + "Message id or message data are null.");
            }

            List<String> data = response.getData();
            int size = data.size();
            
            out.writeInt(response.getMessageId());
            out.writeInt(size);
            for (String string : data) {
                out.writeUTF(string);
            }
            out.flush();
        }

        private DataOutputStream initWriter() throws IOException {
            OutputStream os = clientSocket.getOutputStream();
            return new DataOutputStream(new BufferedOutputStream(os));
        }
    }
}
