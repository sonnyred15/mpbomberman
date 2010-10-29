package org.amse.bomberman.client.net.netty;

import org.amse.bomberman.protocol.impl.ProtocolMessage;

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
