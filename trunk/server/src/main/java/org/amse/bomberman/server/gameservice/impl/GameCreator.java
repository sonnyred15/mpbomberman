package org.amse.bomberman.server.gameservice.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.amse.bomberman.server.gameservice.GamePlayer;
import org.amse.bomberman.server.gameservice.gamemap.impl.GameMap;
import org.amse.bomberman.server.util.GameMapXMLParser;
import org.amse.bomberman.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author Kirilchuk V.E.
 */
public class GameCreator {

    private static final Logger logger = LoggerFactory.getLogger(GameCreator.class);
    
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

        //TODO all below must do spesial builder, not storage.
        File f = null;
        try {
            URL mapsDir = GameCreator.class.getClassLoader().getResource("maps");
            f = new File(mapsDir.toURI());
        } catch (URISyntaxException ex) {
            logger.error("Cant find maps directory", ex);
        }

        int extensionIndex = gameMapName.indexOf(".map");
        if (extensionIndex == -1) {
            throw new FileNotFoundException("GameMap name must have .map extension.");
        }

        String name = gameMapName.substring(0, extensionIndex);
        f = new File(f.getPath() + File.separatorChar
                + gameMapName + File.separatorChar + name + ".xml");

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

        return game;
    }
}
