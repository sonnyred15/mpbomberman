/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.protocol;

import java.io.IOException;
import org.amse.bomberman.FakeAsynchroClient;
import org.amse.bomberman.server.net.Server;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Utilities {
    private static RequestCreator requestCreator = new RequestCreator();

    private Utilities(){}

    public static void disconnectClientAndCloseAll(Server server,
            FakeAsynchroClient client) throws IOException {
        client.sendRequest(requestCreator.requestServerDisconnect());
        client.closeConnection();
        server.stop();
    }
}
