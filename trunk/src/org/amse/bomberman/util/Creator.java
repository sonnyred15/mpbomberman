
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.util;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.GameMap;
import org.amse.bomberman.server.net.IServer;

//~--- JDK imports ------------------------------------------------------------

import java.awt.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import org.amse.bomberman.server.gameinit.control.Controller;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

/**
 * Utility class.
 * Main appointment to create something.
 * @author Kirilchuk V.E
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

        for (String string : gameMaps) {
            result.add(string);
        }

        return result;
    }

    private static String[] createGameMapsListFromDirectory() {    // TODO fix NPE and others
        return Constants.RESOURSES_GAMEMAPS_DIRECTORY.list();
    }

    /**
     * Creates gameMap with defined name and return this gameMap field.
     * @param fileName name of gameMap.
     * @return field of gameMap.
     * @throws FileNotFoundException if gameMap with such name was not founded.
     * @throws IOException if IO errors occurs while creating gameMap.
     */
    public static int[][] createMapAndGetField(String fileName) throws FileNotFoundException, IOException {
        int[][] ret = null;
        GameMap map = new GameMap(fileName);

        ret = map.getField();

        return ret;
    }

    /**
     * Creates game.
     * @param server server on which game must be created.
     * @param gameMapName name of gameMap.
     * @param gameName name of game.
     * @param maxPlayers maxPlayers parameter of game.
     * @return created game.
     * @throws FileNotFoundException if gameMap with defined name was not finded.
     * @throws IOException if IO errors occurs while creating gameMap.
     */
    public static Game createGame(IServer server,
                                   Controller controller,
                                   String gameMapName,
                                   String gameName,
                                   int maxPlayers)
            throws FileNotFoundException, IOException {

        File f = Constants.RESOURSES_GAMEMAPS_DIRECTORY;
        String name = gameMapName.substring(0, gameMapName.indexOf(".map"));
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

        return new Game(server, controller, gameMap, gameName, maxPlayers);
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

    public static void main(String[] args) {
        String[] maps = Creator.createGameMapsListFromDirectory();

        for (String string : maps) {
            createErrorDialog(null, "Game maps.", string);
        }
    }
}
