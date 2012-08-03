package org.amse.bomberman.server.net.tcpimpl.servers;

import java.io.IOException;

/**
 *
 * @author Kirilchuk V.E.
 */
interface ServerState {

    void start(TcpServer server, int port) throws IOException, IllegalStateException;

    void stop(TcpServer server) throws IOException, IllegalStateException;
}
