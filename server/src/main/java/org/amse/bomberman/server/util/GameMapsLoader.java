package org.amse.bomberman.server.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.amse.bomberman.server.gameservice.gamemap.impl.GameMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

public class GameMapsLoader {

    private static final Logger LOG = LoggerFactory.getLogger(GameMapsLoader.class);
    
    public List<String> createGameMapsList() {
        List<String> result = null;
        try {
            URL url = GameMapsLoader.class.getClassLoader().getResource("maps");
            if (url != null) {
                File mapsDir = new File(url.toURI());
                String[] maps = mapsDir.list(new MapsFilter());
                if (maps != null) {
                    result = Arrays.asList(maps);
                }
            }
        } catch (URISyntaxException ex) {
            LOG.error("Error while getting maps list", ex);
        }
        
        if (result == null) {
            return Collections.emptyList();
        } else {
            return result;
        }
    }
    
    public GameMap createGameMap(String gameMapName) throws FileNotFoundException, IOException {
        try {
            URL url = GameMapsLoader.class.getClassLoader().getResource("maps");
            if (url != null) {
                File mapsDir = new File(url.toURI());
                int extensionIndex = gameMapName.indexOf(".map");
                if (extensionIndex == -1) {
                    throw new FileNotFoundException("GameMap name must have .map extension.");
                }
                String name = gameMapName.substring(0, extensionIndex);
                mapsDir = new File(mapsDir, gameMapName); //step into
                File mapFile = new File(mapsDir, name + ".xml");
                GameMap gameMap = new GameMapXMLParser().parseAndCreate(mapFile);
                return gameMap;
            } else {
                throw new FileNotFoundException("Cant find maps directory");
            }
        } catch (URISyntaxException ex) {
            LOG.error("Cant find maps directory", ex);
            throw new FileNotFoundException("Cant find maps directory");
        } catch (SAXException ex) {
            throw new IOException("SAXException while creating gameMap.");
        } catch (DOMException ex) {
            throw new IOException("DOMException while creating gameMap.");
        } catch (IllegalArgumentException ex) {
            throw new IOException("Wrong gameMap xml file." + ex.getMessage());
        }
    }
        
    private static class MapsFilter implements FilenameFilter {
        
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".map");
        }    
    }
}
