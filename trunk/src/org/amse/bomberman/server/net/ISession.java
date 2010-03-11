/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net;

import java.util.List;
import org.amse.bomberman.server.gameinit.GameMapUpdateListener;
import org.amse.bomberman.server.gameinit.Player;

/**
 *
 * @author chibis
 */
public interface ISession extends GameMapUpdateListener{ // Session must Extend Thread

    void run(); //method from Thread

    void interruptSession() throws SecurityException; //must delegate to thread.interrupt()

    void start(); //method from Thread

    Player getPlayer();

    boolean correspondTo(Player player);

    void sendAnswer(List<String> messages);

    void sendAnswer(String message);
}
