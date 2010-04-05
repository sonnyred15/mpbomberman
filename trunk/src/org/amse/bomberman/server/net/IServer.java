/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net;

import java.io.IOException;
import java.util.List;
import org.amse.bomberman.server.ServerChangeListener;
import org.amse.bomberman.server.gameinit.Game;

/**
 *
 * @author chibis
 */
public interface IServer {   

    void sessionTerminated(ISession endedSession);

    void start() throws IOException, IllegalStateException;

    void shutdown() throws IOException, IllegalStateException;

    @Deprecated
    void notifyAllClients(String message);

    @Deprecated
    void notifyAllClients(List<String> messages);

    @Deprecated
    void notifyAllClientsExceptOne(List<String> messages, ISession sessionToIgnore);

    @Deprecated
    void notifySomeClients(List<ISession> sessions, List<String> messages);

    @Deprecated
    void notifySomeClients(List<ISession> sessions, String message);

    int addGame(Game game);

    void removeGame(Game game);

    Game getGame(int gameID);

    List<Game> getGamesList();

    boolean isShutdowned();

    int getPort();

    List<ISession> getSessions();

    long getWorkTime();

    List<String> getLog();

    void writeToLog(String message);

    void setChangeListener(ServerChangeListener logListener);
}
