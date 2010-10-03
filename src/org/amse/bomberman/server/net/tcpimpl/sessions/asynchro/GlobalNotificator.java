
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl.sessions.asynchro;

//~--- non-JDK imports --------------------------------------------------------
import org.amse.bomberman.server.net.Server;
import org.amse.bomberman.server.net.Session;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.protocol.responses.ResponseCreator;

/**
 * Class that represents clients notificator about
 * global messages - messages that must be sended to <b>all</b> server
 * clients.
 *
 * @author Kirilchuk V.E.
 */
public class GlobalNotificator extends Thread { //TODO bad concurency

    private final ResponseCreator protocol = new ResponseCreator(); //TODO inject from session
    private final BlockingQueue<ProtocolMessage<Integer, String>> queue;
    private final Server server;

    /**
     * Constructs global notificator object.
     *
     * @param server server from which set of sessions to notify will be get.
     */
    public GlobalNotificator(Server server) {
        super("Global notificator");
        this.server = server;
        this.queue = new ArrayBlockingQueue<ProtocolMessage<Integer, String>>(5); //fixed by number of different global messages.
        this.setDaemon(true);
    }

    @Override
    public void run() {
        System.out.println("Global notificator thread started.");
        List<ProtocolMessage<Integer, String>> copy
                        = new ArrayList<ProtocolMessage<Integer, String>>();
        while (!server.isStopped()) {
            try {

                // notifying all sessions
                Set<Session> sesions = this.server.getSessions();
                synchronized(copy) {
                    copy.clear();
                }
                synchronized (queue) {
                    queue.drainTo(copy);
                }
                for(Session session : sesions) {
                    synchronized(copy) {
                        for (ProtocolMessage<Integer, String> message : copy) {
                            session.send(message); //this will add message to sending queue.
                        }
                    }
                }

                // sleeping at least for 1 second
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        System.out.println("Global notificator thread ended.");
    }

    /**
     * Adds message to notificator queue.
     * Ignore message if there is too much messages in queue.
     *
     * @param message to send.
     */
    public void addToQueue(int messageId, String detail) {
        synchronized(queue) {
            ProtocolMessage<Integer, String> message = protocol.ok(messageId, detail);
            this.queue.offer(message);    // actually can return false but we don`t care
        }
    }

}
