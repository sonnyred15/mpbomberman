/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net;

/**
 *
 * @author chibis
 */
public interface ISession {

    void run();

    void interrupt() throws SecurityException;

    void start();
}
