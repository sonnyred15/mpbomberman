package org.amse.bomberman.client.control.protocolhandlers.impl;

import java.util.List;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.client.control.protocolhandlers.ProtocolHandler;
import org.amse.bomberman.client.models.impl.ClientStateModel;
import org.amse.bomberman.client.models.impl.ClientStateModel.ClientState;
import org.amse.bomberman.protocol.impl.ProtocolConstants;

/**
 *
 * @author Kirilchuk V.E.
 */
public class GameTerminatedMessageHandler implements ProtocolHandler {

    @Override
    public void process(Controller controller, List<String> data) {
        ClientStateModel clientStateModel = controller.getContext().getClientStateModel();

        if (data.get(0).equals(ProtocolConstants.MESSAGE_GAME_KICK)) {
            clientStateModel.stateChangeError(ClientState.GAME,
                    "Host is escaped from game!\n Game terminated.");
        }
    }
}
