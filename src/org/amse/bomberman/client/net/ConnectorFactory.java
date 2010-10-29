package org.amse.bomberman.client.net;

import org.amse.bomberman.client.net.impl.ConnectorImpl;
import org.amse.bomberman.protocol.impl.ProtocolMessage;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ConnectorFactory {

    public GenericConnector<ProtocolMessage> newInstance() {
        return new ConnectorImpl();
    }
}
