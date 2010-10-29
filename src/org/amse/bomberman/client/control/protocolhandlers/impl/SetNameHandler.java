package org.amse.bomberman.client.control.protocolhandlers.impl;

import java.util.List;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.client.control.protocolhandlers.ProtocolHandler;
import org.amse.bomberman.client.models.gamemodel.impl.PlayerModel;

/**
 *
 * @author Kirilchuk V.E.
 */
public class SetNameHandler implements ProtocolHandler {

    @Override
    public void process(Controller controller, List<String> args) {
        String name = args.get(0);
        PlayerModel model = controller.getContext().getPlayerModel();
        model.setName(name);
    }
}
