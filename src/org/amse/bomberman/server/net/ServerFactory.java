package org.amse.bomberman.server.net;

import org.amse.bomberman.server.ServiceContext;
import org.amse.bomberman.server.net.tcpimpl.servers.TcpServer;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ServerFactory {

    public Server newInstance() {
        ServiceContext context = new ServiceContext();
        return new TcpServer(context);
    }
}
