package org.amse.bomberman.client.control.protocolhandlers.impl;

import java.util.List;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.client.control.protocolhandlers.ProtocolHandler;
import org.amse.bomberman.client.models.gamemodel.impl.GameStateModel;
import org.amse.bomberman.client.models.impl.ClientStateModel;
import org.amse.bomberman.client.models.impl.ClientStateModel.ClientState;

/**
 *
 * @author Kirilchuk V.E.
 */
public class LeaveResultHandler implements ProtocolHandler {

    @Override
    public void process(Controller controller, List<String> data) {
        GameStateModel gameStateModel = controller.getContext().getGameStateModel();
        gameStateModel.setEnded(true);

        ClientStateModel clientStateModel = controller.getContext().getClientStateModel();

        if (data.get(0).equals("Disconnected.")) {
            clientStateModel.setState(ClientState.NOT_JOINED);
        } else {
            clientStateModel.stateChangeError(ClientState.NOT_JOINED,
                    "Can not leave game.\n" + data.get(0));
        }
    }

}
