package org.amse.bomberman.client.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import org.amse.bomberman.client.model.BombMap;
import org.amse.bomberman.client.net.impl.Connector.NetException;
import org.amse.bomberman.util.Constants.Direction;

/**
 *
 * @author Michail Korovkin
 */
public interface IConnector {

     public boolean joinBotIntoGame(int gameNumber) throws IOException, NetException;
     public void сonnect(InetAddress address, int port)
             throws UnknownHostException, IOException;
     public void leaveGame() throws NetException;
     public ArrayList<String> takeGamesList() throws NetException;
     public boolean createGame(String gameName, String mapName, int maxPl) 
             throws IOException, NetException;
     public boolean joinGame(int gameID) throws IOException, NetException;
     public boolean doMove(Direction dir) throws NetException;
     public void startGame() throws NetException;
     public BombMap getMap() throws NetException;
     public void plantBomb() throws NetException;
     // must be here???
     public void beginUpdating() throws NetException;
     public InetAddress getInetAddress();
     public int getPort();
     public String[] getMaps() throws NetException;
     public boolean isStarted() throws IOException, NetException;
}
