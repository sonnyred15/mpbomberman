package org.amse.bomberman.client.control.protocolhandlers.impl;

import java.util.List;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.client.control.protocolhandlers.ProtocolHandler;
import org.amse.bomberman.client.models.impl.ClientStateModel;
import org.amse.bomberman.client.models.impl.ClientStateModel.State;

/**
 *
 * @author Kirilchuk V.E.
 */
public class GameStartedMessageHandler implements ProtocolHandler {

    public void process(Controller controller, List<String> args) {
        ClientStateModel clientStateModel = controller.getContext().getClientStateModel();

        clientStateModel.setState(State.GAME);
    }
}
