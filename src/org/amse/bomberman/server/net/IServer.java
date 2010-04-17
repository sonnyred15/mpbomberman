/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net;

import java.io.IOException;
import java.util.List;
import org.amse.bomberman.server.view.ServerChangeListener;
import org.amse.bomberman.server.gameinit.Game;

/**
 *
 * @author chibis
 */
public interface IServer {   

    void sessionTerminated(ISession endedSession);

    void start() throws IOException, IllegalStateException;

    void shutdown() throws IOException, IllegalStateException;

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
