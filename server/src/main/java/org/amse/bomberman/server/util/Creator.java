package org.amse.bomberman.server.util;

//~--- non-JDK imports --------------------------------------------------------

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.amse.bomberman.util.Constants;
//~--- JDK imports ------------------------------------------------------------

/**
 * Utility class.
 * Main appointment to create something.
 * @author Kirilchuk V.E.
 */
public class Creator {

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

        String[] list = Constants.RESOURSES_GAMEMAPS_DIRECTORY.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".map");
            }
        });

        return list;
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
