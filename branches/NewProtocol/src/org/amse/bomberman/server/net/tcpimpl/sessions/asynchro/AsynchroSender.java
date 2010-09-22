/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl.sessions.asynchro;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.server.net.Session;
import org.amse.bomberman.util.IOUtilities;

/**
 *
 * @author Kirilchuk V.E.
 */
public class AsynchroSender {

    private final SeparatelySynchronizedMap<Integer, ProtocolMessage<Integer, String>> messagesMap =
            new SeparatelySynchronizedMap<Integer, ProtocolMessage<Integer, String>>(24);
    //
    private final Session session; //TODO maybe use observer for session terminate instead of reference and check mustEnd?
    private final Socket clientSocket;
    private final Thread senderThread;
    private DataOutputStream out;

    public AsynchroSender(Session session, Socket clientSocket) {
        this.session = session;
        this.clientSocket = clientSocket;

        this.senderThread = new SenderThread("Single Notificator("
                + session.getID() + ")");
        this.senderThread.setDaemon(true);
    }

    public void start() throws IOException {
        this.out = initWriter();
        this.senderThread.start();
    }

    private DataOutputStream initWriter() throws IOException {
        OutputStream os = this.clientSocket.getOutputStream();        
        return new DataOutputStream(new BufferedOutputStream(os));
    }

    public void addToQueue(ProtocolMessage<Integer, String> message) {
        messagesMap.put(message.getMessageId(), message);
    }

    private class SenderThread extends Thread {

        private SenderThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            System.out.println(super.getName() + " thread started.");
            Set<Entry<Integer, ProtocolMessage<Integer, String>>> entrySet = messagesMap.entrySet();
            try {
                while(!session.isMustEnd()) {
                    try {
                        for(Entry<Integer, ProtocolMessage<Integer, String>> entry : entrySet) {
                            ProtocolMessage<Integer, String> toSend = entry.setValue(null);
                            if(toSend != null) {
                                this.send(toSend);
                            }
                        }
                        Thread.sleep(20);// sleeping at least for 20 milliseconds
                    } catch (InterruptedException ex) {//must never happen
                        ex.printStackTrace();
                    }
                }
            } finally {
                IOUtilities.close(out);
            }
            
            System.out.println(super.getName() + " thread ended.");
        }

        /**
         * Send strings from linesToSend to client.
         * @param linesToSend lines to send.
         *
         * @throws IllegalArgumentException
         */
        private void send(ProtocolMessage<Integer, String> response) {
            if(response.isBroken()) {
                throw new IllegalArgumentException(
                        "Broken response. Message id or message data are null.");
            }

            try {
                System.out.println(super.getName() + " sending answer...");

                List<String> data = response.getData();
                int size = data.size();

                out.writeInt(response.getMessageId());
                out.writeInt(size);
                for(String string : data) {
                    out.writeUTF(string);
                }
                out.flush();

            } catch (IOException ex) {
                System.err.println(super.getName() + " sendAnswer error. "
                        + ex.getMessage());
            }
        }
    }
}
