
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.protocol;

//~--- non-JDK imports --------------------------------------------------------

/**
 *
 * @author Kirilchuk V.E
 */
public interface RequestExecutor {

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

    void sendNewMessagesFromChat();

    void tryRemoveBot();

    void sendGamePlayersStats();

    void setClientName(String[] args);
}
