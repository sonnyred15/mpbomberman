package org.amse.bomberman.client.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import org.amse.bomberman.client.model.BombMap;
import org.amse.bomberman.client.model.BombMap.Direction;

/**
 *
 * @author michail korovkin
 */
public interface IConnector {
     public void сonnect(InetAddress address, int port)
             throws UnknownHostException, IOException;
     public void leaveGame();
     public ArrayList<String> takeGamesList();
     public void createGame();
     public void joinGame(int gameID);
     public boolean doMove(Direction dir);
     public void startGame();
     public BombMap getMap();
     public void plantBomb();
     // must be here???
     public void beginUpdating();
     public InetAddress getInetAddress();
     public int getPort();
}
