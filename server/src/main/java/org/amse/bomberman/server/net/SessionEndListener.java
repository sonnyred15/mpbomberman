package org.amse.bomberman.server.net;

/**
 *
 * @author Kirilchuk V.E
 */
public interface SessionEndListener {

    /**
     * Tells to listener that session thread was ended so it can be removed.
     * @param endedSession session that ended.
     */
    void sessionTerminated(Session endedSession);
}
