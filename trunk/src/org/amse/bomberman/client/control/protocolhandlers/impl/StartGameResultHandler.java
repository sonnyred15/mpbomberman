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
public class StartGameResultHandler implements ProtocolHandler {

    @Override
    public void process(Controller controller, List<String> data) {
        ClientStateModel clientStateModel = controller.getContext().getClientStateModel();

        if (data.get(0).equals("Game started.")) {
            clientStateModel.setState(ClientState.GAME);
        } else {
            clientStateModel.stateChangeError(ClientState.LOBBY,
                    "Can not start game.\n" + data.get(0));
        }
    }
}
