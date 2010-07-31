
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.net;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;
import org.amse.bomberman.server.gameinit.GameStorage;

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
     * Must terminate session.
     */
    void terminateSession();    
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

    GameStorage getGameStorage();
    
    CommandExecutor getCommandExecutor();

    int getID();

    boolean isMustEnd();
}
