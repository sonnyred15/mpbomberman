package org.amse.bomberman.client.control.protocolhandlers;

import java.util.List;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.protocol.ProtocolMessage;

/**
 *
 * @author Kirilchuk V.E.
 */
public interface ProtocolHandler {

    void process(Controller controller, List<String> args);
}
