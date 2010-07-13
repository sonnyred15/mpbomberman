
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.net.IServer;
import org.amse.bomberman.server.net.ISession;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Notificator extends Thread {
    private final Queue<String> queue;
    private final IServer       server;

    public Notificator(IServer server) {
        this.server = server;
        this.queue  = new ArrayBlockingQueue<String>(10);
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (!server.isShutdowned()) {
            try {

                // get all messages into list
                List<String> messages = new ArrayList<String>();
                String       message  = null;

                while ((message = queue.poll()) != null) {//TODO maybe use drainTo?
                    messages.add(message);
                }

                // notifying all sessions
                List<ISession> sesions = this.server.getSessions();

                for (ISession ses : sesions) {
                    if(messages.size()==0) {
                        break;
                    }
                    ses.sendAnswer(messages);
                }

                // sleeping at least for 1 second
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        System.out.println("Notificator thread ended.");
    }

    /**
     * Ignore message if there is too much messages in queue.
     * @param message to send.
     */
    public void addToQueue(String message) {
        this.queue.offer(message);    // actually can return boolean
    }
}
