
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */

package org.amse.bomberman.protocol;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.net.tcpimpl.Controller;
import org.amse.bomberman.util.Creator;
import org.amse.bomberman.util.Stringalize;

//~--- JDK imports ------------------------------------------------------------

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

/**
 * Invoker of commands
 * @author Kirilchuk V.E.
 */
public class ResponseCreator {

    /**
     * Constructs protocol answerer.
     */
    public ResponseCreator() {}

    /**
     * Method that returns "empty query" message to send.
     *
     * @return "empty query" message to send.
     */
    public List<String> emptyQueryError() {
        List<String> messages = new ArrayList<String>();

        messages.add("Empty query. Error on client side.");

        return messages;
    }

    /**
     * Method that returns "wrong query" message to send.
     *
     * @return "wrong query" message to send.
     */
    public List<String> wrongQuery() {
        List<String> messages = new ArrayList<String>();

        messages.add("Wrong query.");

        return messages;
    }

    /**
     * Method that returns "wrong query" message to send
     * with specified cause of uncorrectness.
     *
     * @param cause remark about uncorrectness.
     *
     * @return "wrong query" message to send
     * with specified cause of uncorrectness.
     */
    public List<String> wrongQuery(String cause) {
        List<String> messages = new ArrayList<String>();

        messages.add("Wrong query." + " " + cause);

        return messages;
    }

    /**
     * Method that returns "wrong query" message to send
     * with specified protocol caption and
     * with specified cause of uncorrectness .
     *
     * @param caption protocol caption of message.
     * @param cause remark about uncorrectness.
     *
     * @return "wrong query" message to send
     * with specified protocol caption and
     * with specified cause of uncorrectness .
     */
    public List<String> wrongQuery(String caption, String cause) {
        List<String> messages = new ArrayList<String>();

        messages.add(0, caption);
        messages.add("Wrong query." + " " + cause);

        return messages;
    }

    /**
     * Method that returns "ok" message to send for client
     * with specified protocol caption and
     * with specified additional message.
     *
     * @param caption protocol caption of message.
     * @param msg message.
     *
     * @return
     */
    public List<String> ok(String caption, String msg) {
        List<String> messages = new ArrayList<String>();

        messages.add(0, caption);
        messages.add(msg);

        return messages;
    }

    /**
     * Method that returns "not ok" message to send for client
     * with specified protocol caption and
     * with specified additional message..
     *
     * @param caption protocol caption of message.
     * @param msg message
     *
     * @return
     */
    public List<String> notOK(String caption, String msg) {    // TODO  no difference between it and ok(...)
        List<String> messages = new ArrayList<String>();

        messages.add(0, caption);
        messages.add(msg);

        return messages;
    }

    /**
     * Method that returns "no unstarted games" message to send to cient.
     *
     * @return "no unstarted games" message to send to cient.
     */
    public List<String> noUnstartedGames() {
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_GAMES_LIST);
        messages.add("No unstarted games finded.");

        return messages;
    }

    /**
     * Method that returns message with unstarted games list to send to cient.
     *
     * @param games list of all games(started and unstarted)
     * @return message with unstarted games list to send to cient.
     */
    public List<String> unstartedGamesList(List<Game> games) {
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_GAMES_LIST);
        messages.addAll(Stringalize.unstartedGames(games));    // TODO synchronization problem may occur

        return messages;
    }

    /**
     * Method that returns "not joined to any game" message to send to cient with
     * specified protocol caption.
     *
     * @param caption protocol caption of message.
     *
     * @return "not joined to any game" message to send to cient with
     * specified protocol caption.
     */
    public List<String> notJoined(String caption) {
        List<String> messages = new ArrayList<String>();

        messages.add(0, caption);
        messages.add("Not joined to any game.");

        return messages;
    }

    /**
     * Method that returns message for client, that contains gameMap.
     * If errors occurs during creation of gameMap then message will
     * contain remark about error.
     *
     * @param gameMapName to send to client in message.
     *
     * @return message for client, that contains game map.
     * If errors occurs during creation of gameMap then message will
     * contain remark about error.
     */
    public List<String> downloadGameMap(String gameMapName) {
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_DOWNLOAD_GAME_MAP);

        int[][] ret = null;

        try {
            ret = Creator.createMapAndGetField(gameMapName);

            System.out.println("Session: client downloaded gameMap." + " GameMap=" + gameMapName);
            messages.addAll(Stringalize.field(ret));

        } catch (FileNotFoundException ex) {
            System.out.println("Session: sendMap warning. "
                               + "Client tryed to download map, canceled. "
                               + "Map wasn`t founded on server." + " Map=" + gameMapName + " "
                               + ex.getMessage());
            messages.add("No such map on server.");
        } catch (IOException ex) {
            System.out.println("Session: sendMap error. "
                               + "Client tryed to download map, canceled. "
                               + "Error on server side while loading map." + " Map=" + gameMapName
                               + " " + ex.getMessage());
            messages.add("Error on server side, while loading map.");
        }

        return messages;
    }

    /**
     * Method that returns message to send to client
     * that contains availiable gameMaps.
     *
     * @return message that contains availiable
     * gameMaps.
     */
    public List<String> sendGameMapsList() {
        List<String> messages = Stringalize.gameMapsList();

        messages.add(0, ProtocolConstants.CAPTION_GAME_MAPS_LIST);

        if ((messages == null) || (messages.size() == 0)) {
            System.out.println("Session: sendMapsList error. No maps founded on server.");
            messages.add("No maps on server was founded.");
        } else {
            System.out.println("Session: sended maps list to client. Maps count="
                               + (messages.size() - 1));
        }

        return messages;
    }

    /**
     * Method that returns message to send to client
     * that contains game status.
     *
     * @param game status will be getted from this game.
     *
     * @return message to send to client
     * that contains game status.
     */
    public List<String> sendGameStatus(Game game) {
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_GAME_STATUS);
        messages.add(Stringalize.gameStartStatus(game));

        return messages;
    }

    /**
     * Method that returns message to send to client
     * that contains game info.
     *
     * @param controller //TODO fill this javaDoc
     *
     * @return message to send to client
     * that contains game info.
     */
    public List<String> sendGameInfo(Controller controller) {
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_GAME_INFO);
        messages.addAll(Stringalize.gameInfoForClient(controller));

        return messages;
    }

    /**
     * Method that returns message to send for client that contains
     * gameMap info with exlosions and client player status.
     *
     * @param controller //TODO fill this javaDoc
     *
     * @return message to send for client that contains
     * gameMap info with exlosions and client player status.
     */
    public List<String> gameMapInfo(Controller controller) {
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_GAME_MAP_INFO);
        messages.addAll(Stringalize.fieldExplPlayerInfo(controller.getMyGame(),
                                controller.getPlayer()));

        return messages;
    }

    /**
     * Method that returns message to send to client that contains
     * client game`s players stats.
     *
     * @param game the game of client to take players stats from.
     *
     * @return message to send to client that contains
     * client game`s players stats.
     */
    public List<String> sendPlayersStats(Game game) {
        List<String> messages = new ArrayList<String>();

        messages.add(0, ProtocolConstants.CAPTION_GAME_PLAYERS_STATS);
        messages.addAll(Stringalize.playersStats(game.getCurrentPlayers()));

        return messages;
    }
}
