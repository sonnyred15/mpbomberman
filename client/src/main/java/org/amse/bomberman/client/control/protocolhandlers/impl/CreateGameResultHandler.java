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
public class CreateGameResultHandler implements ProtocolHandler {

    @Override
    public void process(Controller controller, List<String> data) {
        ClientStateModel clientStateModel = controller.getContext().getClientStateModel();

        if (data.get(0).equals("Game created.")) {
            clientStateModel.setState(ClientState.LOBBY);
        } else {
            clientStateModel.stateChangeError(ClientState.NOT_JOINED,
                    "Can not create game.\n" + data.get(0));
        }
    }

}
