package org.amse.bomberman.client.control.protocolhandlers;

import java.util.List;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.protocol.ProtocolMessage;

/**
 * Class that corresponds for handling protocol message.
 *
 * @author Kirilchuk V.E.
 */
public interface ProtocolHandler {

    /**
     * Processing message by setting some data in proper models, taked from
     * {@link Controller#getContext()}, or making
     * some other things with controller.
     *
     * @param controller controller to work with.
     * @param args arguments/data of protocol message.
     */
    void process(Controller controller, List<String> args);
}
