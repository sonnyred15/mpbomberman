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
 * 
 * @author Kirilchuk V.E
 */
public interface IConnector2 {

    public void сonnect(InetAddress address, int port)
            throws UnknownHostException, IOException;

    public void requestLeaveGame() throws NetException;

    public void requestGamesList() throws NetException;

    public void requestCreateGame(String gameName, String mapName, int maxPl)
            throws NetException;

    public void requestJoinGame(int gameID) throws NetException;

    public void requestDoMove(Direction dir) throws NetException;

    public void requestStartGame() throws NetException;

    public void requestMap() throws NetException;

    public void requestPlantBomb() throws NetException;

    public void requestJoinBotIntoGame(int gameNumber) throws NetException;

    public void requestMapsList() throws NetException;

    public void requestIsGameStarted() throws NetException;

    public void requestGameInfo() throws NetException;

    public void sendChatMessage(String message) throws NetException;

    public void getNewChatMessages() throws NetException;

    @Deprecated
    public InetAddress getInetAddress();

    @Deprecated
    public int getPort();

    @Deprecated
    public void beginUpdating() throws NetException;//???
}
