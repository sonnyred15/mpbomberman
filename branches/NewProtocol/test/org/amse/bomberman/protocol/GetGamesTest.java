package org.amse.bomberman.protocol;

import org.amse.bomberman.protocol.requests.RequestCreator;
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
public class GetGamesTest {

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
    public void getNoGamesTest() throws Exception {
        ProtocolMessage<Integer, String> request = requestCreator.requestGamesList();
        client.sendRequest(request);

        ProtocolMessage<Integer, String> response = client.receiveResult();
        assertEquals(ProtocolConstants.GAMES_LIST_MESSAGE_ID, (int) response.getMessageId());
        List<String> data = response.getData();
        assertEquals(1, data.size());
        String message = response.getData().get(0);
        assertEquals("No unstarted games finded.", message);
    }

    @Test
    public void getOneGameTest() throws Exception {
        createGame();

        ProtocolMessage<Integer, String> request = requestCreator.requestGamesList();
        client.sendRequest(request);

        ProtocolMessage<Integer, String> response = client.receiveResult();
        assertEquals(ProtocolConstants.GAMES_LIST_MESSAGE_ID, (int) response.getMessageId());
        List<String> data = response.getData();
        assertEquals(1, data.size());
        String message = response.getData().get(0);
        assertEquals("0/game/1.map/1/4", message);
    }

    private void createGame() throws IOException {
        ProtocolMessage<Integer, String> request = requestCreator.requestCreateGame("game", "1.map", -1);
        client.sendRequest(request);

        ProtocolMessage<Integer, String> response = client.receiveResult();
        assertEquals(ProtocolConstants.CREATE_GAME_MESSAGE_ID, (int) response.getMessageId());
    }
}
