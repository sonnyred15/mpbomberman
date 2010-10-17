package org.amse.bomberman.client.net;

import org.amse.bomberman.protocol.ProtocolMessage;

/**
 * @author Kirilchuk V.E
 */
public interface ServerListener {

    void received(ProtocolMessage<Integer, String> message);
}
