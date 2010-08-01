
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.net;

/**
 *
 * @author Kirilchuk V.E
 */
public interface SessionEndListener {

    /**
     * Tells to server that session thread was ended so it can be removed.
     * @param endedSession session that ended.
     */
    void sessionTerminated(ISession endedSession);
}
