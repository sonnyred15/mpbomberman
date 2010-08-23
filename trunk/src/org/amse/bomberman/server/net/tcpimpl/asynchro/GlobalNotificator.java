
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl.asynchro;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.net.IServer;
import org.amse.bomberman.server.net.ISession;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Class that represents clients notificator about
 * global messages - messages that must be sended to <b>all</b> server
 * clients.
 *
 * @author Kirilchuk V.E.
 */
public class GlobalNotificator extends Thread { //TODO bad concurency
    private final BlockingQueue<String> queue;
    private final IServer       server;

    /**
     * Constructs global notificator object.
     *
     * @param server server from which set of sessions to notify will be get.
     */
    public GlobalNotificator(IServer server) {
        super("Global notificator");
        this.server = server;
        this.queue  = new ArrayBlockingQueue<String>(5); //fixed by number of different global messages.
        this.setDaemon(true);
    }

    @Override
    public void run() {
        System.out.println("Global notificator thread started.");
        List<String> messages = new ArrayList<String>();
        while (!server.isShutdowned()) {
            try {

                // get all messages into list
                messages.clear();
                synchronized(queue) {
                    queue.drainTo(messages);
                }

                // notifying all sessions
                Set<ISession> sesions = this.server.getSessions();

                for (ISession ses : sesions) {
                    if(messages.size()==0) {
                        break;
                    }
                    ses.sendAnswer(messages); //this will add message to sending queue.
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
    public void addToQueue(String message) {
        synchronized (queue) {
            if (queue.contains(message)) { //not need to duplicate global messages.
                return;
            }

            //if message is new then try to add it to queue.
            this.queue.offer(message);    // actually can return boolean
        }
    }
}
