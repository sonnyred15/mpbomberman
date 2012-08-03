package org.amse.bomberman.client.control.protocolhandlers.impl;

import java.util.List;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.client.control.protocolhandlers.ProtocolHandler;

/**
 *
 * @author Kirilchuk V.E.
 */
public class DoMoveResultHandler implements ProtocolHandler {

    @Override
    public void process(Controller controller, List<String> args) {
        //do nothing
        //we ignore result of do move
    }

}
