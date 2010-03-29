/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.client.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.amse.bomberman.util.Constants.Direction;

/**
 * @author Michael Korovkin
 * @author Kirilchuk V.E
 */
public interface IConnector2 {

    void сonnect(InetAddress address, int port) throws UnknownHostException
            , IOException;
    void disconnect();
    void requestLeaveGame() throws NetException;
    void requestGamesList() throws NetException;
    void requestCreateGame(String gameName, String mapName, int maxPl)
            throws NetException;
    void requestJoinGame(int gameID) throws NetException;
    void requestDoMove(Direction dir) throws NetException;
    void requestStartGame() throws NetException;
    void requestGameMap() throws NetException;
    void requestDownloadGameMap(String gameMapName) throws NetException;
    void requestPlantBomb() throws NetException;
    void requestJoinBotIntoGame() throws NetException;
    void requestGameMapsList() throws NetException;
    void requestIsGameStarted() throws NetException;
    void requestGameInfo() throws NetException;
    void sendChatMessage(String message) throws NetException;
    void requestNewChatMessages() throws NetException;
}
