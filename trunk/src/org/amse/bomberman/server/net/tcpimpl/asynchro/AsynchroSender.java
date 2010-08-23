/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl.asynchro;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.amse.bomberman.server.net.ISession;

/**
 *
 * @author Kirilchuk V.E.
 */
public class AsynchroSender extends Thread {

    private final SeparatelySynchronizedMap<String, List<String>> messagesMap =
            new SeparatelySynchronizedMap<String, List<String>>(24);
    //
    private final ISession session;
    private final Socket clientSocket;

    public AsynchroSender(ISession session, Socket clientSocket) {
        super("Single Notificator(" + session.getID() + ")");
        this.session = session;
        this.clientSocket = clientSocket;
        this.setDaemon(true);
    }

    @Override
    public void run() {
        System.out.println(super.getName() + " thread started.");
        Set<Entry<String, List<String>>> entrySet = messagesMap.entrySet();
        while (!session.isMustEnd()) {
            try {
                for (Entry<String, List<String>> entry : entrySet) {
                    List<String> toSend = entry.setValue(null);
                    if (toSend != null) {
                        this.send(toSend);
                    }
                }
                Thread.sleep(20);// sleeping at least for 20 milliseconds
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        System.out.println(super.getName() + " thread ended.");
    }

    /**
     * Ignore message if there is too much messages in queue.
     * @param message to send.
     */
    public void addToQueue(String message) {
        List<String> list = new ArrayList(1);
        list.add(message);
        addToQueue(message, list);
    }

    /**
     * Ignore message if there is too much messages in queue.
     * @param message to send.
     */
    public void addToQueue(List<String> message) {
        String caption = message.get(0);
        addToQueue(caption, message);
    }

    private void addToQueue(String key, List<String> value) {
        messagesMap.put(key, value);
    }

    /**
     * Send strings from linesToSend to client.
     * @param linesToSend lines to send.
     *
     * @throws IllegalArgumentException
     */
    private void send(List<String> linesToSend) throws IllegalArgumentException {
        assert (linesToSend != null);
        assert (linesToSend.size() > 0);

        BufferedWriter out = null;

        try {
            out = initWriter();

            System.out.println(super.getName() + " sending answer...");

            for (String string : linesToSend) {
                out.write(string);
                out.newLine();
            }

            out.write("");    // TODO magic code...
            out.newLine();
            out.flush();
        } catch (IOException ex) {
            System.err.println(super.getName() + " sendAnswer error. " + ex.getMessage());
        }
    }

    private BufferedWriter initWriter() throws IOException {
        BufferedWriter out;
        OutputStream os = this.clientSocket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

        out = new BufferedWriter(osw);

        return out;
    }
}
