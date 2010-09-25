
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.util;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameservice.GameMap;

//~--- JDK imports ------------------------------------------------------------

import java.awt.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

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
        String[]     gameMaps = createGameMapsListFromDirectory();
        List<String> result   = new ArrayList<String>();

        for (String string : gameMaps) {// TODO game maps can be null...
            result.add(string);
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
        GameMap map = new GameMap(fileName);

        ret = map.getField();

        return ret;
    }

    /**
     * Creates error JDialog.
     * @see JDialog
     * @param parent determines the Frame in which the dialog is displayed.
     * If null, or if the parentComponent has no Frame, a default Frame is used
     * @param description description of error.
     * @param message message of error.
     */
    public static void createErrorDialog(Component parent, 
                                         String description,
                                         String message) {
        //
        JOptionPane.showMessageDialog(parent, description + "\n" + message,
                                     "Error", JOptionPane.ERROR_MESSAGE);
    }
}
