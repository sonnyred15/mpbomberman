/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net;

/**
 *
 * @author chibis
 */
public interface ISession { // Session must Extend Thread

    void run(); //method from Thread

    void interrupt() throws SecurityException; //must delegate to thread.interrupt()

    void start(); //method from Thread
}
