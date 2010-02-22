package org.amse.bomberman.client.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import org.amse.bomberman.client.model.BombMap;
import org.amse.bomberman.client.net.impl.Connector.NetException;
import org.amse.bomberman.util.Constants.Direction;

/**
 *
 * @author Michail Korovkin
 */
public interface IConnector {

     public void сonnect(InetAddress address, int port)
             throws UnknownHostException, IOException;
     public boolean leaveGame() throws NetException;
     public List<String> takeGamesList() throws NetException;
     public boolean createGame(String gameName, String mapName, int maxPl) 
             throws NetException;
     public boolean joinGame(int gameID) throws NetException;
     public boolean doMove(Direction dir) throws NetException;
     public boolean startGame() throws NetException;
     public BombMap getMap() throws NetException;
     public boolean plantBomb() throws NetException;
     public boolean joinBotIntoGame(int gameNumber) throws NetException;
     // must be here???
     public String[] getMaps() throws NetException;
     public boolean isStarted() throws NetException;
     public List<String> getMyGameInfo() throws NetException;
     public InetAddress getInetAddress();
     public int getPort();
     public void beginUpdating() throws NetException;
}
