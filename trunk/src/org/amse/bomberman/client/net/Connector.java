package org.amse.bomberman.client.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.amse.bomberman.client.control.ConnectorListener;
import org.amse.bomberman.protocol.ProtocolMessage;

/**
 * @author Mikhail Korovkin
 * @author Kirilchuk V.E
 */
public interface Connector {

    void сonnect(InetAddress address, int port)
            throws UnknownHostException, IOException;

    void closeConnection();

    void sendRequest(ProtocolMessage<Integer, String> message)
            throws NetException;

    void setListener(ConnectorListener listener);
}
