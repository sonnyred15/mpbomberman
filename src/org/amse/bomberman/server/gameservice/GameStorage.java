package org.amse.bomberman.server.gameservice;

import org.amse.bomberman.server.gameservice.impl.Game;
import org.amse.bomberman.server.gameservice.gamemap.impl.GameMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.amse.bomberman.server.gameservice.listeners.GameChangeListener;
import org.amse.bomberman.server.net.Server;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.GlobalNotificator;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.GameMapXMLParser;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author Kirilchuk V.E.
 */
public class GameStorage implements GameChangeListener {
    private final List<Game> games = new LinkedList<Game>(); //TODO whats about synchronization?
    private final GlobalNotificator notificator;

    public GameStorage(Server server) {
        this.notificator = new GlobalNotificator(server);
        this.notificator.start();
    }

    /**
     * Creates game.
     * @param gameMapName name of gameMap.
     * @param gameName name of game.
     * @param maxPlayers maxPlayers parameter of game.
     * @return created game.
     * @throws FileNotFoundException if gameMap with defined name was not finded.
     * @throws IOException if IO errors occurs while creating gameMap.
     */
    public Game createGame(GamePlayer creator, String gameMapName,
                           String gameName, int maxPlayers)
                           throws FileNotFoundException, IOException {
        if(creator == null || gameMapName == null || gameName == null) {
            throw new IllegalArgumentException("Args can`t be null.");
        }

        //TODO all below must do spesial builder, not storage.
        File f = Constants.RESOURSES_GAMEMAPS_DIRECTORY;

        int extensionIndex = gameMapName.indexOf(".map");
        if(extensionIndex == -1) {
            throw new FileNotFoundException("GameMap name must have .map extension.");
        }

        String name = gameMapName.substring(0, extensionIndex);
        f = new File(f.getPath() + File.separatorChar +
                     gameMapName + File.separatorChar + name + ".xml");

        GameMap gameMap = null;
        try {
            gameMap = new GameMapXMLParser().parseAndCreate(f);
        } catch (SAXException ex) {
            throw new IOException("SAXException while creating gameMap.");
        } catch (DOMException ex) {
            throw new IOException("DOMException while creating gameMap.");
        } catch (IllegalArgumentException ex) {
            throw new IOException("Wrong gameMap xml file." + ex.getMessage());
        }

        Game game = new Game(creator, gameMap, gameName, maxPlayers);
        game.addGameChangeListener(this);
        this.addGame(game);

        return game;
    }

    public synchronized int addGame(Game gameToAdd) {
        int n = -1;

        this.games.add(gameToAdd);
        n = this.games.indexOf(gameToAdd);
        System.out.println("GameStorage: game added.");
        this.notificator.addToQueue(ProtocolConstants.GAMES_LIST_NOTIFY_ID,
                ProtocolConstants.UPDATE_GAMES_LIST);


        return n;
    }

    public synchronized void removeGame(Game gameToRemove) {
        if (this.games.remove(gameToRemove)) {
            System.out.println("GameStorage: game removed.");
            this.notificator.addToQueue(ProtocolConstants.GAMES_LIST_NOTIFY_ID,
                ProtocolConstants.UPDATE_GAMES_LIST);
        } else {
            System.err.println("GameStorage: removeGame warning. " + "No specified game found.");
        }
    }

    public synchronized Game getGame(int n) {
        Game game = null;
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
        this.notificator.addToQueue(ProtocolConstants.GAMES_LIST_NOTIFY_ID,
                ProtocolConstants.UPDATE_GAMES_LIST);
    }

    @Override
    public void gameStarted(Game game) {
        this.notificator.addToQueue(ProtocolConstants.GAMES_LIST_NOTIFY_ID,
                ProtocolConstants.UPDATE_GAMES_LIST);
    }

    @Override
    public void gameTerminated(Game game) {
        this.removeGame(game);
    }

    public void newChatMessage(String message) {
        //ignore =)
    }

    public void fieldChanged() {
        //ignore again =)
    }

    public void gameEnded(Game game) {
        //ignore again =)
    }

    public void statsChanged(Game game) {
        //ignore again =)
    }
}
