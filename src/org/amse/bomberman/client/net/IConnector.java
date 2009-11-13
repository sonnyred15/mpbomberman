package org.amse.bomberman.client.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import org.amse.bomberman.client.model.Map;

/**
 *
 * @author michail korovkin
 */
public interface IConnector {
     public void сonnect(InetAddress address, int port)
             throws UnknownHostException, IOException;
     public void disconnect();
     public ArrayList<String> takeGamesList();
     public void createGame();
     public void joinGame(int gameID);
     public boolean doMove(int direction);
     public void startGame();
     public Map getMap();
}
