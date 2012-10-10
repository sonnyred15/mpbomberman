package org.amse.bomberman.client.net;

import org.amse.bomberman.protocol.ProtocolMessage;

/**
 * Class that represents listener of connector.
 *
 * @author Kirilchuk V.E.
 */
public interface ConnectorListener {//TODO must be generic too <MessageType>

    /**
     * Notifying listener that some message was received from server.
     * Note that listener must parse this message in another thread cause
     * some connector`s can`t receive anything while connector`s thread is using
     * in this method.(Depends on implementation of connector)
     *
     * @param message received protocol message.
     */
    void received(ProtocolMessage message);
}
