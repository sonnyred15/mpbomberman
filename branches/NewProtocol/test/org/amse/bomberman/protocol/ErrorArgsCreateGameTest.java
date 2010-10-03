package org.amse.bomberman.protocol;

import org.amse.bomberman.protocol.requests.RequestCreator;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.amse.bomberman.FakeAsynchroClient;
import org.amse.bomberman.server.net.tcpimpl.servers.TcpServer;
import org.amse.bomberman.util.Constants.Direction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;

/**
 *
 * @author Kirilchuk V.E.
 */
@RunWith(Parameterized.class)
public class ErrorArgsCreateGameTest {

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

    @Parameters
    public static Collection<?> createGameArgs() {
        return Arrays.asList(new Object[][] {
            {null,""},
            {"", null},
            {null, null}
        });
    }

    private String gameName;
    private String mapName;
    private int maxPlayers = -1;

    public ErrorArgsCreateGameTest(String gameName, String mapName) {
        this.gameName = gameName;
        this.mapName = mapName;
    }

    @Ignore
    @Test(expected=IllegalArgumentException.class)
    public void createGameWithNullArgsTest() throws Exception {
        ProtocolMessage<Integer, String> request = requestCreator.requestCreateGame(gameName, mapName, maxPlayers);
        client.sendRequest(request);
    }
}
