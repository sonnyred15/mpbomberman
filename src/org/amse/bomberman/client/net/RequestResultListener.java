package org.amse.bomberman.client.net;

import org.amse.bomberman.protocol.ProtocolMessage;

/**
 * @author Kirilchuk V.E
 */
public interface RequestResultListener {

    void received(ProtocolMessage<Integer, String> response);
}
