package org.amse.bomberman.server.util;

//~--- non-JDK imports --------------------------------------------------------

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.amse.bomberman.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//~--- JDK imports ------------------------------------------------------------

/**
 * Utility class.
 * Main appointment to create something.
 * @author Kirilchuk V.E.
 */
public class Creator {

    private static final Logger logger = LoggerFactory.getLogger(Creator.class);
    
    private Creator() {}

    /**
     * Creates list of gameMaps.
     * @return list of gameMaps.
     */
    public static List<String> createGameMapsList() {
        String[] gameMaps = createGameMapsListFromDirectory();
        List<String> result = new ArrayList<String>();

        if (gameMaps != null) {
            for (String string : gameMaps) {
                result.add(string);
            }
        }

        return result;
    }

    private static String[] createGameMapsListFromDirectory() {
        assert Constants.RESOURSES_GAMEMAPS_DIRECTORY != null;

        String[] result = null;
        
        try {
            URL url = Creator.class.getClassLoader().getResource("maps");
            File mapsDir = new File(url.toURI()); //TODO: NPE
            result = mapsDir.list(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".map");
                }
            });
        } catch (URISyntaxException ex) {
            logger.error("Can`t get maps list!", ex);
        }
        
        return result;
    }

    /**
     * Creates gameMap with defined name and return this gameMap field.
     * @param fileName name of gameMap.
     * @return field of gameMap.
     * @throws FileNotFoundException if gameMap with such name was not founded.
     * @throws IOException if IO errors occurs while creating gameMap.
     */
    @Deprecated //TODO replaced with XML map!!! ERRORS!!!!!!
    public static int[][] createMapAndGetField(String fileName) throws FileNotFoundException, IOException {
        int[][] ret = null;
        
        return ret;
    }

    static String[] botNames =
    {"Suicider", "Terminator",
     "Nike", "Budda",
     "Adibas", "Killer",
     "Gosu", "Nietzsche",
     "Idiot", "Archimed",
     "Gosling", "Switch",
     "Gauss", "KISS",
     "Lenin", "Factory",
     "Stalin", "Singletone",
     "Steveee", "Compositor",
     "Patriot", "Adapter",
     "Slayer", "Debian",
     "J2SEBot", "Lenny",
     "iBot", "Squeeze",
     "I_KILL_YOU", "SuperMan",
     "Ololo", "Linus T.",
     "Bug", "Feature"
    };
    static Random rnd = new Random();

    public static String randomBotName() {
        int id = rnd.nextInt(botNames.length);
        return botNames[id];
    }
}
