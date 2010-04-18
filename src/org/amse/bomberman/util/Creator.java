
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

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

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
        return new ArrayList<String>(Constants.maps);
    }

    /**
     * Creates gameMap with defined name and return this gameMap field.
     * @param fileName name of gameMap.
     * @return field of gameMap.
     * @throws FileNotFoundException if gameMap with such name was not founded.
     * @throws IOException if IO errors occurs while creating gameMap.
     */
    public static int[][] createMapAndGetField(String fileName)
                                    throws FileNotFoundException,
                                           IOException {
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
    public static Game createGame(IServer server, String gameMapName,
                                  String gameName, int maxPlayers)
                                    throws FileNotFoundException,
                                           IOException {

        // TODO migration version
        if (!gameMapName.endsWith(".map")) {
            gameMapName = gameMapName + ".map";
        }

        GameMap map = new GameMap(gameMapName);

        return new Game(server, map, gameName, maxPlayers);
    }

    /**
     * Creates error JDialog.
     * @see JDialog
     * @param parent determines the Frame in which the dialog is displayed.
     * If null, or if the parentComponent has no Frame, a default Frame is used
     * @param description description of error.
     * @param message message of error.
     */
    public static void createErrorDialog(Component parent, String description,
                                         String message) {
        JOptionPane.showMessageDialog(parent, description + "\n" + message,
                                      "Error", JOptionPane.ERROR_MESSAGE);
    }
}
