/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.server.net.tcpimpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.amse.bomberman.server.net.ISession;

/**
 *
 * @author Kirilchuk V.E.
 */
public class SingleNotificator extends Thread {
    private final BlockingQueue<String> simpleQueue;
    private final BlockingQueue<List<String>> listQueue;
    private final ISession session;//actually only for asycnhro session

    public SingleNotificator (ISession session) {
        super("Single Notificator");
        this.session = session;
        this.simpleQueue  = new ArrayBlockingQueue<String>(10);
        this.listQueue  = new ArrayBlockingQueue<List<String>>(20);
        this.setDaemon(true);        
    }

    @Override
    public void run() {
        System.out.println("Single notificator thread started.");
        List<String> simple = new ArrayList<String>();
        List<List<String>> complicated = new ArrayList<List<String>>();
        while (!session.isMustEnd()) {
            try {

                // get all messages into list
                simple.clear();
                simpleQueue.drainTo(simple);

                // notifying client
                //not ses.notifyClient() cause it is already asynchronous and lead to this class
                if(simple.size()!=0){
                    this.session.sendAnswer(simple);
                }

                complicated.clear();
                listQueue.drainTo(complicated);
                for (List<String> list : complicated) {
                    if(list.size()!= 0){
                        // sending to client
                        this.session.sendAnswer(list);
                    }
                }
 
                // sleeping at least for 30 milliseconds
                Thread.sleep(10);
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
        if(simpleQueue.contains(message)){
            return;
        }

        this.simpleQueue.offer(message);
    }

    /**
     * Ignore message if there is too much messages in queue.
     * @param message to send.
     */
    public synchronized void addToQueue(List<String> message) {
        String caption = message.get(0);
        for (Iterator<List<String>> it = listQueue.iterator();it.hasNext();) {
            List<String> list  = it.next();
            if(list.get(0).equals(caption)) {
                it.remove();//replacing with newest one
                this.listQueue.offer(message);
                return;
            }
        }

        this.listQueue.offer(message);
    }




}