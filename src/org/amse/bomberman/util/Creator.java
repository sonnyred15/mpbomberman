/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.util;

import java.awt.Component;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JOptionPane;
import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.GameMap;
import org.amse.bomberman.server.net.IServer;

/**
 *
 * @author Kirilchuk V.E
 */
public class Creator {

    public static String[] createMapsList() {
        String[] maps = new String[Constants.maps.size()];
        maps = Constants.maps.toArray(maps);
        return maps;
    }

    public static int[][] createMapAndGetArray(String fileName) throws FileNotFoundException, IOException {
        int[][] ret = null;
        GameMap map = new GameMap(fileName);
        ret = map.getMapArray();
        return ret;
    }

    public static Game createGame(IServer server, String mapName, String gameName, int maxPlayers)
            throws FileNotFoundException, IOException {
        GameMap map = new GameMap(mapName + ".map");
        return new Game(server, map, gameName, maxPlayers);
    }

    public static void createErrorDialog(Component parent,String description, String message){
        JOptionPane.showMessageDialog(parent, description + "\n" +message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private Creator() {
    }
}
