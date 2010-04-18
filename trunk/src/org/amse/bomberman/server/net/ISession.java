
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.net;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

/**
 * Interface that represents session between client side and server side
 * of application. ISession is responsable for work with client request`s,
 * for answer`s on this requests and so on..
 *
 * <p>Supposed that inherited class will extend Thread!!!
 * @author Kirilchuk V.E
 */
public interface ISession {

    /**
     * Method in which session must listen request`s from client.
     */
    void run();

    /**
     * Must interrupt session and terminate it.
     * @throws SecurityException if interrupting thread cannot interrupt session.
     */
    void interruptSession() throws SecurityException;    // must delegate to thread.interrupt()

    /**
     * Tells session to start receiving requests and answer on them and to do
     * all needed for that things.
     */
    void start();    // method from Thread

    /**
     * Sends list of strings to client.
     * Each string must be sended as new line.
     * @param messages
     */
    void sendAnswer(List<String> messages);

    /**
     * Sends simple one-string message to client.
     * @param message
     */
    void sendAnswer(String message);

    /**
     * Notifyes client about something by sending message to him.
     * <p>This method is not the same as sendAnswer by one reason:
     * if connection between session and client is synchronous then session
     * can only answer on such client requests and can`t notify client about
     * something without his request, so notifyClient must do nothing.
     * On the other hand if connection is
     * asynchronous then session can send messages to client whenever
     * it is needed.
     * @param message notification to send.
     */
    void notifyClient(String message);
}
