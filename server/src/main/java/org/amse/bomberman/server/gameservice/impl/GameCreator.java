package org.amse.bomberman.server.gameservice.impl;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.amse.bomberman.server.gameservice.GamePlayer;
import org.amse.bomberman.server.gameservice.gamemap.impl.GameMap;
import org.amse.bomberman.server.util.GameMapsLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kirilchuk V.E.
 */
public class GameCreator {

    private static final Logger LOG = LoggerFactory.getLogger(GameCreator.class);
    
    private GameMapsLoader gameMapsLoader = new GameMapsLoader();
    
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
        if (creator == null || gameMapName == null || gameName == null) {
            throw new IllegalArgumentException("Args can`t be null.");
        }

        GameMap gameMap = gameMapsLoader.createGameMap(gameMapName);
        Game game = new Game(creator, gameMap, gameName, maxPlayers);

        return game;
    }
}
