package org.amse.bomberman.client.control.protocolhandlers.impl;

import java.util.List;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.client.control.protocolhandlers.ProtocolHandler;

/**
 *
 * @author Kirilchuk V.E.
 */
public class GameStatusMessageHandler implements  ProtocolHandler {

    @Override
    public void process(Controller controller, List<String> args) {
        //do nothing
        //we ignore this message.
    }
}
