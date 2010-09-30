package org.amse.bomberman.protocol;

import java.io.IOException;
import java.util.List;
import org.amse.bomberman.FakeAsynchroClient;
import org.amse.bomberman.server.net.tcpimpl.servers.TcpServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kirilchuk V.E.
 */
public class CreateGameTest {

    static TcpServer server;
    static FakeAsynchroClient client;
    RequestCreator requestCreator = new RequestCreator();

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
    public void createGameIllegalGameMapTest() throws Exception {
        illegalCreateGame("nosuchmap");
        illegalCreateGame("nosuchmap.map");
        illegalCreateGame("no such map");
    }

    private void illegalCreateGame(String gameMapName) throws IOException {
        ProtocolMessage<Integer, String> request = requestCreator.requestCreateGame("qwe", gameMapName, 10);
        client.sendRequest(request);
        ProtocolMessage<Integer, String> response = client.receiveResult();
        assertEquals(ProtocolConstants.CREATE_GAME_MESSAGE_ID, (int) response.getMessageId());
        List<String> data = response.getData();
        assertEquals(1, data.size());
        String message = response.getData().get(0);
        assertEquals("No such map on server.", message);
    }

    @Test
    public void createGame() throws IOException {
        ProtocolMessage<Integer, String> request = requestCreator.requestCreateGame("game", "1.map", -1);
        client.sendRequest(request);

        ProtocolMessage<Integer, String> response = client.receiveResult();
        assertEquals(ProtocolConstants.CREATE_GAME_MESSAGE_ID, (int) response.getMessageId());
        List<String> data = response.getData();
        assertEquals(1, data.size());
        String message = response.getData().get(0);
        assertEquals("Game created.", message);
    }
}
