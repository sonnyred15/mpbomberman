
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.net;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.util.Constants.Direction;

//~--- JDK imports ------------------------------------------------------------

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Kirilchuk V.E
 */
public interface CommandExecutor {

    // commands
    void sendGames();

    void tryCreateGame(String[] args);

    void tryJoinGame(String[] args);

    void tryDoMove(String[] args);

    void sendGameMapInfo();

    void tryStartGame();

    void tryLeave();

    void tryPlaceBomb();

    void sendDownloadingGameMap(String[] args);

    void sendGameStatus();

    void sendGameMapsList();

    void tryAddBot(String[] args);

    void sendGameInfo();

    void addMessageToChat(String[] args);

    void getNewMessagesFromChat();

    void tryRemoveBot();

    void sendGamePlayersStats();

    void setClientName(String[] args);
}
