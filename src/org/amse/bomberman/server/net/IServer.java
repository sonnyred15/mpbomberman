
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.net;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.view.ServerChangeListener;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.List;

/**
 * Interface that represents server.
 * Server is responsable for accepting clients, giving personal ISession
 * for them. Additionally server knows about games created by server`s clients.
 * @author Kirilchuk V.E.
 */
public interface IServer {

    /**
     * Tells to server that session thread was ended so it can be removed.
     * @param endedSession
     */
    void sessionTerminated(ISession endedSession);

    /**
     * Raise server on it`s port.
     * @throws IOException if any IO errors occurs while raising server.
     * @throws IllegalStateException if server was already raised.
     */
    void start() throws IOException,
                        IllegalStateException;

    /**
     * Shutdown`s server, so it stop`s to listen on it`s port and unavailiable
     * for new clients.
     * @throws IOException if any IO errors occurs while shutdowning server.
     * @throws IllegalStateException if server was already shutdowned.
     */
    void shutdown() throws IOException,
                           IllegalStateException;

    /**
     * Add game to server.
     * @param game game to add.
     * @return ID of the game. By this ID game can be getted from server.
     */
    int addGame(Game game);

    /**
     * Removing game from server.
     * @param game gam to remove.
     */
    void removeGame(Game game);

    /**
     * Gets game from server by game ID.
     * @param gameID ID of game to get.
     * @return game with defined ID.
     */
    Game getGame(int gameID);

    /**
     * Returns list of games on server.
     * @return list of games on server.
     */
    List<Game> getGamesList();

    /**
     * Checks if server is shutdowned.
     * @return true if server is shutdowned, false otherwise.
     */
    boolean isShutdowned();

    /**
     * Returns port of this server.
     * @return port of this server.
     */
    int getPort();

    /**
     * Returns list of sessions on this server.
     * @return list of sessions on this server.
     */
    List<ISession> getSessions();

    /**
     * Returns lifetime in ms. of this server from it`s first raise.
     * @return lifetime in ms. of this server from it`s first raise.
     */
    long getWorkTime();

    /**
     * Returns log of this server.
     * @return log of this server.
     */
    List<String> getLog();

    /**
     * Write some message to server log.
     * @param message message to add to server log.
     */
    void writeToLog(String message);

    /**
     * Setting change listener of server.
     * @param changeListener listener.
     */
    void setChangeListener(ServerChangeListener changeListener);

    /**
     * Returns reference to server change listener if it exists or null.
     * @return reference to server change listener if it exists or null..
     */
    ServerChangeListener getChangeListener();
}
