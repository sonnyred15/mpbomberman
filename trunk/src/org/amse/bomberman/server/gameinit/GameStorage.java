package org.amse.bomberman.server.gameinit;

//~--- JDK imports ------------------------------------------------------------

import org.amse.bomberman.server.net.tcpimpl.Notificator;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.amse.bomberman.server.gameinit.control.GameChangeListener;
import org.amse.bomberman.server.net.IServer;
import org.amse.bomberman.server.net.ISession;
import org.amse.bomberman.util.ProtocolConstants;

/**
 *
 * @author Kirilchuk V.E.
 */
public class GameStorage implements GameChangeListener {
    private final List<Game> games = new LinkedList<Game>();
    private final Notificator notificator;


    public GameStorage(IServer server) {
        this.notificator = new Notificator(server);
        this.notificator.start();
    }

    public synchronized int addGame(Game gameToAdd) {
        int n = -1;

        this.games.add(gameToAdd);
        n = this.games.indexOf(gameToAdd);
        System.out.println("GameStorage: game added.");
        this.notificator.addToQueue(ProtocolConstants.UPDATE_GAMES_LIST);


        return n;
    }

    public synchronized void removeGame(Game gameToRemove) {
        if (this.games.remove(gameToRemove)) {
            System.out.println("GameStorage: game removed.");
            this.notificator.addToQueue(ProtocolConstants.UPDATE_GAMES_LIST);
        } else {
            System.err.println("GameStorage: removeGame warning. " + "No specified game found.");
        }
    }

    public synchronized Game getGame(int n) {
        Game game = null;//return null if no game with such index was founded

        try {
            game = this.games.get(n);
        } catch (IndexOutOfBoundsException ex) {
            System.err.println("GameStorager: getGame warning. " + "Tryed to get game with illegal ID.");
        }

        return game;
    }

    public List<Game> getGamesList() {
        return Collections.unmodifiableList(this.games);
    }

    public synchronized void clearGames() {
        games.clear();
    }

    @Override
    public void parametersChanged(Game game) {
        this.notificator.addToQueue(ProtocolConstants.UPDATE_GAMES_LIST);
    }

    @Override
    public void started(Game game) {
        this.notificator.addToQueue(ProtocolConstants.UPDATE_GAMES_LIST);
    }

    @Override
    public void gameEnded(Game game) {
        this.removeGame(game);
    }
}
