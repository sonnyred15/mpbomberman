
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.protocol.requests;

//~--- non-JDK imports --------------------------------------------------------

import java.util.List;


/**
 *
 * @author Kirilchuk V.E
 */
public interface RequestExecutor {

    // commands
    void sendGames() throws InvalidDataException ;

    void tryCreateGame(List<String> args) throws InvalidDataException ;

    void tryJoinGame(List<String> args) throws InvalidDataException ;

    void tryDoMove(List<String> args) throws InvalidDataException ;

    void sendGameMapInfo() throws InvalidDataException ;

    void tryStartGame() throws InvalidDataException ;

    void tryLeave() throws InvalidDataException ;

    void tryPlaceBomb() throws InvalidDataException ;

    void sendDownloadingGameMap(List<String> args) throws InvalidDataException ;

    void sendGameStatus() throws InvalidDataException ;

    void sendGameMapsList() throws InvalidDataException ;

    void tryAddBot(List<String> args) throws InvalidDataException ;

    void sendGameInfo() throws InvalidDataException ;

    void addMessageToChat(List<String> args) throws InvalidDataException ;

    void sendNewMessagesFromChat() throws InvalidDataException ;

    public abstract void tryKickPlayer(List<String> args) throws InvalidDataException ;

    void sendGamePlayersStats() throws InvalidDataException ;

    void setClientNickName(List<String> args) throws InvalidDataException ;
}
