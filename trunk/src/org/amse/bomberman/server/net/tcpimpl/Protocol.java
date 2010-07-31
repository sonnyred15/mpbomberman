/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.server.net.tcpimpl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.net.CommandExecutor;
import org.amse.bomberman.util.Command;
import org.amse.bomberman.util.Creator;
import org.amse.bomberman.util.ProtocolConstants;
import org.amse.bomberman.util.Stringalize;

/**
 * Invoker of commands
 * @author Kirilchuk V.E.
 */
public class Protocol {
    public static List<String> emptyQueryError() {
        List<String> messages = new ArrayList<String>();
        messages.add("Empty query. Error on client side.");

        return messages;
    }

    public static List<String> wrongQuery() {
        List<String> messages = new ArrayList<String>();
        messages.add("Wrong query.");

        return messages;
    }

    public static List<String> wrongQuery(String cause) {
        List<String> messages = new ArrayList<String>();
        messages.add("Wrong query." + " " + cause);

        return messages;
    }

    public static List<String> wrongQuery(String caption, String cause) {
        List<String> messages = new ArrayList<String>();
        messages.add(0, caption);
        messages.add("Wrong query." + " " + cause);

        return messages;
    }

    public static List<String> ok(String caption, String msg) {
        List<String> messages = new ArrayList<String>();
        messages.add(0, caption);
        messages.add(msg);

        return messages;
    }

    public static List<String> notOK(String caption, String msg) {
        List<String> messages = new ArrayList<String>();
        messages.add(0, caption);
        messages.add(msg);

        return messages;
    }

    public static List<String> noUnstartedGames() {
        List<String> messages = new ArrayList<String>();
        messages.add(0, ProtocolConstants.CAPTION_GAMES_LIST);
        messages.add("No unstarted games finded.");

        return messages;
    }

    public static List<String> unstartedGamesList(List<Game> games) {
        List<String> messages = new ArrayList<String>();
        messages.add(0, ProtocolConstants.CAPTION_GAMES_LIST);
        messages.addAll(Stringalize.unstartedGames(games));//TODO synchronization problem may occur

        return messages;
    }

    public static List<String> notJoined(String caption) {
        List<String> messages = new ArrayList<String>();
        messages.add(0, caption);
        messages.add("Not joined to any game.");

        return messages;
    }
    
    public static List<String> downloadGameMap(String gameMapName) {
        List<String> messages = new ArrayList<String>();
        messages.add(0, ProtocolConstants.CAPTION_DOWNLOAD_GAME_MAP);

        int[][] ret = null;
        try {
            ret = Creator.createMapAndGetField(gameMapName);
            System.out.println("Session: client downloaded gameMap." + " GameMap="
                    + gameMapName);
            messages.addAll(Stringalize.field(ret));

        } catch (FileNotFoundException ex) {
            System.out.println("Session: sendMap warning. "
                    + "Client tryed to download map, canceled. "
                    + "Map wasn`t founded on server." + " Map="
                    + gameMapName + " " + ex.getMessage());
            messages.add("No such map on server.");
        } catch (IOException ex) {
            System.out.println("Session: sendMap error. "
                    + "Client tryed to download map, canceled. "
                    + "Error on server side while loading map."
                    + " Map=" + gameMapName + " " + ex.getMessage());
            messages.add("Error on server side, while loading map.");
        }

        return messages;
    }

    public static List<String> sendGameMapsList() {
        List<String> messages = Stringalize.gameMapsList();
        messages.add(0, ProtocolConstants.CAPTION_GAME_MAPS_LIST);

        if (messages == null || messages.size() == 0) {
            System.out.println("Session: sendMapsList error. No maps founded on server.");
            messages.add("No maps on server was founded.");
        } else {
            System.out.println("Session: sended maps list to client. Maps count="
                               + (messages.size() - 1));
        }

        return messages;
    }

    public static List<String> sendGameStatus(Game game) {
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_GAME_STATUS_INFO);
        messages.add(Stringalize.gameStartStatus(game));
        
        return messages;
    }
    
    public static List<String> sendGameInfo(Controller controller) {
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_GAME_INFO);
        messages.addAll(Stringalize.gameInfoForClient(controller));

        return messages;
    }

    public static List<String> gameMapInfo(Controller controller) {
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_GAME_MAP_INFO);
        messages.addAll(Stringalize.fieldExplPlayerInfo(controller.getMyGame(),
                        controller.getPlayer()));

        return messages;
    }

    public static List<String> sendPlayersStats(Game game) {
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_GET_MY_GAME_PLAYERS_STATS);
        messages.addAll(Stringalize.playersStats(game.getCurrentPlayers()));

        return messages;
    }
}
