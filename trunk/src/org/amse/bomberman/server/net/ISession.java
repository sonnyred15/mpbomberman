
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.net;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

/**
 *
 * @author Kirilchuk V.E
 */
public interface ISession {    // Session must Extend Thread
    void run();    // method from Thread

    void interruptSession() throws SecurityException;    // must delegate to thread.interrupt()

    void start();    // method from Thread

    void sendAnswer(List<String> messages);

    void sendAnswer(String message);

    void notifyClientAboutGameMapChange();

    void notifyClientAboutGameStart();

    void notifyClientAboutGameDisconnect();
}
