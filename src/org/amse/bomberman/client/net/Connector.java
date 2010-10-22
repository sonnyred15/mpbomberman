package org.amse.bomberman.client.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.amse.bomberman.client.control.ConnectorListener;
import org.amse.bomberman.protocol.ProtocolMessage;

/**
 * Class that corresponds for connection between client and server.
 * This interface is for asynchronous connections.
 * It means that responses from server are automatically delivers by
 * using listener method:
 * <p>
 * {@link ConnectorListener#received(org.amse.bomberman.protocol.ProtocolMessage)}
 * <p>
 * So, you can`t use blocking methods like receive or something else.
 *
 * @author Mikhail Korovkin
 * @author Kirilchuk V.E.
 */
public interface Connector {

    /**
     * Tryes to connect to server with specified inet adress and port.
     *
     * @param address inet adress of server to connect.
     * @param port port of server to connect.
     * @throws IOException if io errors occurs during connection.
     */
    void сonnect(InetAddress address, int port) throws IOException;

    /**
     * Closes connection. It is OK to call this method even
     * if you are not connected.
     */
    void closeConnection();

    /**
     * Sends request to server.
     *
     * @param message request message.
     * @throws NetException if connection is broken.
     */
    void sendRequest(ProtocolMessage<Integer, String> message)
            throws NetException;

    /**
     * Sets listener of connector. Currently support only one listener.
     *
     * @param listener listener to set.
     */
    void setListener(ConnectorListener listener);
}
