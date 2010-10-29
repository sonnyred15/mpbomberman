package org.amse.bomberman.client.control.protocolhandlers.impl;

import java.util.List;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.client.control.protocolhandlers.ProtocolHandler;
import org.amse.bomberman.client.models.impl.ClientStateModel;
import org.amse.bomberman.client.models.impl.ClientStateModel.ClientState;

/**
 *
 * @author Kirilchuk V.E.
 */
public class GameStartedMessageHandler implements ProtocolHandler {

    @Override
    public void process(Controller controller, List<String> args) {
        ClientStateModel clientStateModel = controller.getContext().getClientStateModel();

        clientStateModel.setState(ClientState.GAME);
    }
}
