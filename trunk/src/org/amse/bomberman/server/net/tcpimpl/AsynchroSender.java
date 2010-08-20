/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.server.net.tcpimpl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.server.net.ISession;

/**
 *
 * @author Kirilchuk V.E.
 */
public class AsynchroSender extends Thread { //TODO use super.getName and set the name with id of session.
    private final List<SynchronizedEntry<String, List<String>>> messagesMap =
            new ArrayList<SynchronizedEntry<String, List<String>>>(24);
    {
        messagesMap.add(new SynchronizedEntry<String, List<String>>(ProtocolConstants.CAPTION_ADD_CHAT_MSG_RESULT, null));
        messagesMap.add(new SynchronizedEntry<String, List<String>>(ProtocolConstants.CAPTION_CREATE_GAME_RESULT, null));
        messagesMap.add(new SynchronizedEntry<String, List<String>>(ProtocolConstants.CAPTION_DOWNLOAD_GAME_MAP, null));
        messagesMap.add(new SynchronizedEntry<String, List<String>>(ProtocolConstants.CAPTION_DO_MOVE_RESULT, null));
        messagesMap.add(new SynchronizedEntry<String, List<String>>(ProtocolConstants.CAPTION_GAMES_LIST, null));
        messagesMap.add(new SynchronizedEntry<String, List<String>>(ProtocolConstants.CAPTION_GAME_END_RESULTS, null));
        messagesMap.add(new SynchronizedEntry<String, List<String>>(ProtocolConstants.CAPTION_GAME_INFO, null));
        messagesMap.add(new SynchronizedEntry<String, List<String>>(ProtocolConstants.CAPTION_GAME_MAPS_LIST, null));
        messagesMap.add(new SynchronizedEntry<String, List<String>>(ProtocolConstants.CAPTION_GAME_MAP_INFO, null));
        messagesMap.add(new SynchronizedEntry<String, List<String>>(ProtocolConstants.CAPTION_GAME_PLAYERS_STATS, null));
        messagesMap.add(new SynchronizedEntry<String, List<String>>(ProtocolConstants.CAPTION_GAME_STATUS, null));
        messagesMap.add(new SynchronizedEntry<String, List<String>>(ProtocolConstants.CAPTION_JOIN_BOT_RESULT, null));
        messagesMap.add(new SynchronizedEntry<String, List<String>>(ProtocolConstants.CAPTION_JOIN_GAME_RESULT, null));
        messagesMap.add(new SynchronizedEntry<String, List<String>>(ProtocolConstants.CAPTION_LEAVE_GAME_RESULT, null));
        messagesMap.add(new SynchronizedEntry<String, List<String>>(ProtocolConstants.CAPTION_NEW_CHAT_MSGS, null));
        messagesMap.add(new SynchronizedEntry<String, List<String>>(ProtocolConstants.CAPTION_PLACE_BOMB_RESULT, null));
        messagesMap.add(new SynchronizedEntry<String, List<String>>(ProtocolConstants.CAPTION_REMOVE_BOT_RESULT, null));
        messagesMap.add(new SynchronizedEntry<String, List<String>>(ProtocolConstants.CAPTION_SET_CLIENT_NAME_RESULT, null));
        messagesMap.add(new SynchronizedEntry<String, List<String>>(ProtocolConstants.CAPTION_START_GAME_RESULT, null));
        messagesMap.add(new SynchronizedEntry<String, List<String>>(ProtocolConstants.MESSAGE_GAME_KICK, null));
        messagesMap.add(new SynchronizedEntry<String, List<String>>(ProtocolConstants.MESSAGE_GAME_START, null));
        messagesMap.add(new SynchronizedEntry<String, List<String>>(ProtocolConstants.UPDATE_CHAT_MSGS, null));
        messagesMap.add(new SynchronizedEntry<String, List<String>>(ProtocolConstants.UPDATE_GAMES_LIST, null));
        messagesMap.add(new SynchronizedEntry<String, List<String>>(ProtocolConstants.UPDATE_GAME_INFO, null));
        messagesMap.add(new SynchronizedEntry<String, List<String>>(ProtocolConstants.UPDATE_GAME_MAP, null));
    }

    //
    private final ISession session;
    private final Socket  clientSocket;

    public AsynchroSender (ISession session, Socket clientSocket) {
        super("Single Notificator");
        this.session = session;
        this.clientSocket = clientSocket;
        this.setDaemon(true);
    }

    @Override
    public void run() {
        System.out.println("Single notificator thread started.");
        while (!session.isMustEnd()) {
            try {
             
                for (SynchronizedEntry<String, List<String>> entry : messagesMap) {
                    if (entry.value != null) {
                        this.send(entry.getAndSet(null));
                    }
                }
                Thread.sleep(20);// sleeping at least for 20 milliseconds
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        System.out.println("Single notificator thread ended.");
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

    private void addToQueue(String key, List<String> value){
        for (SynchronizedEntry<String, List<String>> entry : messagesMap) {
            if(entry.key.equals(key)) {
                entry.getAndSet(value);
            }
        }
    }

//    /**
//     * Method
//     *
//     * @param shortAnswer
//     */
//    private void sendAnswer2(String shortAnswer) {    // TODO in asychro session answers must be asynchronous...
//        BufferedWriter out = null;
//
//        try {
//            out = initWriter();
//
//            System.out.println("Session: sending answer...");
//            out.write(shortAnswer);
//            out.newLine();
//            out.write("");    // TODO magic code...
//            out.newLine();
//            out.flush();
//        } catch (IOException ex) {
//            System.err.println("Session: sendAnswer error. " + ex.getMessage());
//        }
//    }

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

            System.out.println("Session: sending answer...");

            for (String string : linesToSend) {
                out.write(string);
                out.newLine();
            }

            out.write("");    // TODO magic code...
            out.newLine();
            out.flush();
        } catch (IOException ex) {
            System.err.println("Session: sendAnswer error. " + ex.getMessage());
        }
    }

    private BufferedWriter initWriter() throws IOException {
        BufferedWriter     out;
        OutputStream       os  = this.clientSocket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

        out = new BufferedWriter(osw);

        return out;
    }

    private static class SynchronizedEntry<K, V> {

        private final K key;
        private volatile V value;

        SynchronizedEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        synchronized V getAndSet(V newValue) {
            V previous = this.value;
            this.value = newValue;
            return previous;
        }
    }
}