/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.protocol;

import java.util.List;
import org.amse.bomberman.FakeAsynchroClient;
import org.amse.bomberman.server.gameservice.Game;
import org.amse.bomberman.server.gameservice.GamePlayer;
import org.amse.bomberman.server.net.tcpimpl.servers.TcpServer;
import org.amse.bomberman.util.Constants.Direction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kirilchuk V.E.
 */
public class DoMoveTest {

    static TcpServer server;
    static FakeAsynchroClient client;
    static RequestCreator requestCreator = new RequestCreator();

    @BeforeClass
    public static void setUpClass() throws Exception {
        server = new TcpServer(65535);
        server.start();
        client = new FakeAsynchroClient(server.getPort());
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        Utilities.disconnectClientAndCloseAll(server, client);
    }

    @Test
    public void doMoveNotJoinedTest() throws Exception {
        ProtocolMessage<Integer, String> request = requestCreator.requestDoMove(Direction.UP);
        client.sendRequest(request);

        ProtocolMessage<Integer, String> response = client.receiveResult();
        assertEquals(ProtocolConstants.INVALID_REQUEST_MESSAGE_ID, (int) response.getMessageId());
        
        List<String> data = response.getData();
        assertEquals(1, data.size());
        String message = response.getData().get(0);
        assertEquals("Can`t do move in 'Not Joined' state.", message);
    }

    @Test
    public void doMoveTest() throws Exception {
        GamePlayer creator = new Utilities.FakeGamePlayer();
        Game game = server.getGameStorage().createGame(creator, "1.map", "game", -1);

        ProtocolMessage<Integer, String> request = requestCreator.requestJoinGame(0);
        client.sendRequest(request);

        ProtocolMessage<Integer, String> response = client.receiveResult();
        assertEquals(ProtocolConstants.JOIN_GAME_MESSAGE_ID, (int) response.getMessageId());

        List<String> data = response.getData();
        assertEquals(1, data.size());
        String message = response.getData().get(0);
        assertEquals("Joined.", message);

        game.tryStartGame(creator);
        response = client.receiveResult();
        assertEquals(ProtocolConstants.NOTIFICATION_MESSAGE_ID, (int) response.getMessageId());
        response = client.receiveResult();
        assertEquals(ProtocolConstants.NOTIFICATION_MESSAGE_ID, (int) response.getMessageId());

        request = requestCreator.requestDoMove(Direction.UP);
        client.sendRequest(request);

        response = client.receiveResult();
        assertEquals(ProtocolConstants.NOTIFICATION_MESSAGE_ID, (int) response.getMessageId());

        response = client.receiveResult();
        assertEquals(ProtocolConstants.DO_MOVE_MESSAGE_ID, (int) response.getMessageId());

        data = response.getData();
        assertEquals(1, data.size());
    }
}
