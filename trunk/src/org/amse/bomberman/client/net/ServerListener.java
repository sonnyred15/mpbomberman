package org.amse.bomberman.client.net;

import org.amse.bomberman.protocol.ProtocolMessage;

/**
 * @author Kirilchuk V.E
 * @deprecated controller must set data to models itself.
 * Must be deleted in next version.
 */
public interface ServerListener {

    void received(ProtocolMessage<Integer, String> message);
}
