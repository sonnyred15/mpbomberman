package org.amse.bomberman.client.control.protocolhandlers.impl;

import java.util.List;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.client.control.protocolhandlers.ProtocolHandler;
import org.amse.bomberman.protocol.ProtocolMessage;

/**
 *
 * @author Kirilchuk V.E.
 */
public class UpdateGameInfoCommand implements ProtocolHandler {

    public void process(Controller controller, List<String> args) {
        controller.requestGameInfo();
    }
}
