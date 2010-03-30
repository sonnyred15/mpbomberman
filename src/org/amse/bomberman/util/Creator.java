/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.util;

import java.awt.Component;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;
import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.GameMap;
import org.amse.bomberman.server.net.IServer;

/**
 *
 * @author Kirilchuk V.E
 */
public class Creator {

    public static List<String> createGameMapsList() {
        return Collections.unmodifiableList(Constants.maps);
    }

    public static int[][] createMapAndGetArray(String fileName) throws
            FileNotFoundException, IOException {

        int[][] ret = null;
        GameMap map = new GameMap(fileName);
        ret = map.getField();
        return ret;
    }

    public static Game createGame(IServer server, String mapName, String gameName, int maxPlayers)
            throws FileNotFoundException, IOException {
            //TODO migration version
        if(!mapName.endsWith(".map")){
            mapName = mapName + ".map";
        }
        GameMap map = new GameMap(mapName);
        return new Game(server, map, gameName, maxPlayers);
    }

    public static void createErrorDialog(Component parent, String description, String message) {
        JOptionPane.showMessageDialog(
                parent, description + "\n" + message,
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    private Creator() {
    }
}
