package org.amse.bomberman.client.control;

import org.amse.bomberman.protocol.impl.ProtocolMessage;

/**
 * Class that represents listener of connector.
 *
 * @author Kirilchuk V.E.
 */
public interface ConnectorListener {

    /**
     * Notifying listener that some message was received from server.
     * Note that listener must parse this message in another thread cause
     * connector can`t receive anything while connector`s thread is using
     * in this method.
     * <p>
     * <b>Free caller thread as fast as you can!</b>
     *
     * @param message received protocol message.
     */
    void received(ProtocolMessage message);
}
