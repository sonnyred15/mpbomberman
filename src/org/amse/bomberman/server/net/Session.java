package org.amse.bomberman.server.net;

//~--- non-JDK imports --------------------------------------------------------

import java.io.IOException;
import org.amse.bomberman.server.gameservice.GameStorage;

//~--- JDK imports ------------------------------------------------------------

import org.amse.bomberman.protocol.ProtocolMessage;

/**
 * Interface that represents session between client side and server side
 * of application. ISession is responsable for work with client request`s,
 * for answer`s on this requests and so on..
 *
 * @author Kirilchuk V.E
 */
public interface Session {
    
    /**
     * Must somehow terminate session. After terminate session can`t be reused.
     */
    void terminateSession();

    /**
     * Tells session to start receiving requests and process them.
     */
    void start() throws IOException;

    /**
     * Sends list of strings to client.
     * @param messages lines of strings to send to client.
     */
    void send(ProtocolMessage<Integer, String> message);

    /**
     * Returns GameStorage where games are storing.
     * @see GameStorage
     * @return game storage.
     */
    GameStorage getGameStorage();

    /**
     * Returns the id of this session.
     *
     * <p> Note that if id type overflow, id would be not unique.
     * @return unique id of session.
     */
    long getId();

    /**
     * Returns if current session must terminate.
     * @return true if session must terminate, false - otherwise.
     */
    boolean isMustEnd();

    void addEndListener(SessionEndListener listener);

    void removeEndListener(SessionEndListener listener);
}
