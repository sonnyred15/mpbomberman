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
public class JoinGameResultHandler implements ProtocolHandler {

    @Override
    public void process(Controller controller, List<String> data) {
        ClientStateModel clientStateModel = controller.getContext().getClientStateModel();

        if (data.get(0).equals("Joined.")) {
            clientStateModel.setState(State.LOBBY);
        } else {
            clientStateModel.stateChangeError(State.NOT_JOINED,
                    "Can not join to the game.\n" + data.get(0));
        }
    }

}
