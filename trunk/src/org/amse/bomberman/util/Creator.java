/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.GameMap;
import org.amse.bomberman.server.net.IServer;

/**
 *
 * @author Kirilchuk V.E
 */
public class Creator {

    public static String[] createMapsList() {
        String[] maps = null;
        try {
            maps = new File(".").list(new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    if (name.endsWith(".map")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        } catch (SecurityException ex) {
            System.out.println(ex.getMessage() +
                    " Can`t read files in directory. Acess denied");
        } catch (NullPointerException ex){
            System.out.println(ex.getMessage());
        }
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

    private Creator() {
    }
}
