package org.amse.bomberman.client.net.impl.netty;

import org.amse.bomberman.protocol.ProtocolMessage;

/**
 * Class that represents listener of connector.
 *
 * @author Kirilchuk V.E.
 */
public interface ClientHandlerListener {

    /**
     * Notifying listener that some message was received from server.
     *
     * @param message received protocol message.
     */
    void received(ProtocolMessage message);
}
